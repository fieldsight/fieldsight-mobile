package org.bcss.collect.naxa.notificationslist;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.dao.FormsDao;
import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.dto.Instance;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.RxDownloader.RxDownloader;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.network.APIEndpoint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import timber.log.Timber;

public class InstanceRemoteSource {
    private static InstanceRemoteSource INSTANCE;


    public static InstanceRemoteSource getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new InstanceRemoteSource();
        }
        return INSTANCE;
    }


    Observable<Uri> downloadInstances(FieldSightNotification fieldSightNotification) {
        String downloadUrl = String.format(APIEndpoint.BASE_URL + "/forms/api/instance/download_submission/%s", fieldSightNotification.getFormSubmissionId());
        return downloadInstance(mapNotificationToInstance(fieldSightNotification), downloadUrl);
    }

    private Observable<Uri> downloadInstance(Instance.Builder instance, String downloadUrl) {

        return Observable.just(instance)
                .flatMap((Function<Instance.Builder, ObservableSource<Uri>>) instance1 -> {
                    String pathToDownload = getInstanceFolderPath(formatFileName(instance1.build().getDisplayName()));

                    return new RxDownloader(Collect.getInstance())
                            .download(downloadUrl,
                                    formatFileName(instance1.build().getDisplayName()).concat(".xml"),
                                    pathToDownload,
                                    "*/*",
                                    true)
                            .map(processOneInstance(instance1));
                });
    }

    private Function<String, Uri> processOneInstance(Instance.Builder instance) {
        return path -> {
            path = path.replace("file://", "");
            instance.instanceFilePath(path);
            instance.lastStatusChangeDate(System.currentTimeMillis());

            String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + " = ? ";
            String[] selectionArgs = new String[]{instance.build().getJrFormId()};
            // retrieve the form definition
            Cursor formCursor = new FormsDao().getFormsCursor(selection, selectionArgs);
            formCursor.moveToFirst();

            String jrVersion = getColumnString(formCursor, FormsProviderAPI.FormsColumns.JR_VERSION);
            instance.jrVersion(jrVersion);


            return saveInstance(instance.build());
        };
    }

    private Uri saveInstance(Instance instance) {
        InstancesDao dao = new InstancesDao();
        ContentValues values = dao.getValuesFromInstanceObject(instance);
        Uri instanceUri = dao.saveInstance(values);
        Timber.i("Downloaded and saved instance at %s", instanceUri);
        return instanceUri;

    }


    private Instance.Builder mapNotificationToInstance(FieldSightNotification notificationFormDetail) {
        String fsFormId = notificationFormDetail.getFsFormId();//todo: project site fsFormId unhandled
        String siteId = notificationFormDetail.getSiteId();
        String formName = notificationFormDetail.getFormName();
        String fsFormSubmissionId = notificationFormDetail.getFormSubmissionId();
        String jrFormId = notificationFormDetail.getIdString();
        Long date = System.currentTimeMillis();
        String downloadUrl = String.format(APIEndpoint.BASE_URL + "/forms/api/instance/download_submission/%s", fsFormSubmissionId);

        Instance.Builder flaggedInstance = new Instance.Builder()
                .status(InstanceProviderAPI.STATUS_COMPLETE)
                .jrFormId(jrFormId)
                .fieldSightSiteId(siteId == null ? "0" : siteId)//survey form have 0 as siteId
                .displayName(formName)
                .canEditWhenComplete("true")
                .displaySubtext("");

        return flaggedInstance;
    }

    private String formatFileName(String text) {
        return text.replace(" ", "_");
    }

    private String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }


    private String getInstanceFolderPath(String formName) {
        // Create new answer folder.
        String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.ENGLISH).format(Calendar.getInstance().getTime());

        String instancePath = Collect.INSTANCES_PATH.replace(Environment.getExternalStorageDirectory().toString(), "");

        return instancePath + File.separator + formName + "_"
                + time;
    }

}
