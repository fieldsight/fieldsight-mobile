package org.fieldsight.naxa.flagform;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dto.Instance;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.RxDownloader.RxDownloader;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.helpers.FSInstancesDao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class InstanceRemoteSource {
    private static InstanceRemoteSource INSTANCE;


    public static InstanceRemoteSource getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new InstanceRemoteSource();
        }
        return INSTANCE;
    }


    Observable<Uri> downloadInstances(FieldSightNotification fieldSightNotification, String[] nameAndPath) {
        String downloadUrl = String.format(FieldSightUserSession.getServerUrl(Collect.getInstance()) + APIEndpoint.GET_INSTANCE_XML + "/%s", fieldSightNotification.getFormSubmissionId());
        return downloadInstance(mapNotificationToInstance(fieldSightNotification), downloadUrl, nameAndPath);
    }

    String[] getNameAndPath(String instanceName) {

        String formattedInstanceName = addDateTimeToFileName(instanceName);
        formattedInstanceName = formatFileName(formattedInstanceName);

        String pathToDownload = Collect.INSTANCES_PATH.replace(Environment.getExternalStorageDirectory().toString(), "");  //todo: value returned from getExternalStorageDiectory is twice, so removing one

        pathToDownload = pathToDownload + File.separator + formattedInstanceName;
        pathToDownload = formatFileName(pathToDownload);

        return new String[]{formattedInstanceName.concat(".xml"), pathToDownload};
    }

    private Observable<Uri> downloadInstance(Instance.Builder instance, String downloadUrl, String[] nameAndPath) {
        Timber.i("Downloading filled form from %s for %s", downloadUrl, nameAndPath[0]);

        return Observable.just(instance)
                .flatMap((Function<Instance.Builder, ObservableSource<Uri>>) instance1 -> {

                    String instanceName = nameAndPath[0];
                    String pathToDownload = nameAndPath[1];

                    String mimeType = "*/*";

                    return RxDownloader.getINSTANCE(Collect.getInstance())
                            .download(downloadUrl,
                                    instanceName,
                                    pathToDownload,
                                    mimeType,
                                    false)
                            .doOnError(new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                }
                            })
                            .map(path -> {
                                path = path.replace("file://", "");
                                instance.instanceFilePath(path);
                                return saveInstance(instance.build());
                            });
                });
    }


    Observable<HashMap<String, String>> downloadAttachedMedia(String fsSubmissionId) {
        return ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getInstanceMediaList(fsSubmissionId)
                .subscribeOn(Schedulers.io());

    }

    private Uri saveInstance(Instance instance) {
        FSInstancesDao dao = new FSInstancesDao();
        ContentValues values = dao.getValuesFromInstanceObject(instance);
        Uri instanceUri = dao.saveInstance(values);
        Timber.i("Downloaded and saved instance at %s", instanceUri);
        return instanceUri;
    }


    private Instance.Builder mapNotificationToInstance(FieldSightNotification notificationFormDetail) {
        String siteId = notificationFormDetail.getSiteId() == null ? "0" : notificationFormDetail.getSiteId();//survey form have 0 as siteId
        String fsFormIdProject = notificationFormDetail.getFsFormIdProject();
        String formName = notificationFormDetail.getFormName();
        String jrFormId = notificationFormDetail.getIdString();
        String fsFormId = notificationFormDetail.getFsFormId();
        String formVersion = notificationFormDetail.getFormVersion();
        String fsInstanceId = notificationFormDetail.getFormSubmissionId();


        String url = FSInstancesDao.generateSubmissionUrl(
                notificationFormDetail.isDeployedFromSite() ? SITE : PROJECT,
                siteId, fsFormId);


        return new Instance.Builder()
                .status(InstanceProviderAPI.STATUS_INCOMPLETE)
                .jrFormId(jrFormId)
                .fieldSightInstanceId(fsInstanceId)
                .jrVersion(formVersion)
                .submissionUri(url)
                .fieldSightSiteId(siteId)
                .displayName(formName)
                .canEditWhenComplete("true")
                .lastStatusChangeDate(System.currentTimeMillis());
    }

    private String formatFileName(String text) {
        return text.replace(" ", "_");
    }

    private String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }


    private String getInstanceFolderPath(String formName) {

        String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String instancePath = Collect.INSTANCES_PATH.replace(Environment.getExternalStorageDirectory().toString(), "");
        return instancePath + File.separator + formName + "_" + time;
    }

    private String addDateTimeToFileName(String formName) {
        String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        return formName + "_" + time;
    }


}
