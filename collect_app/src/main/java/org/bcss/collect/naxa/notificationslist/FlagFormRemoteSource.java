package org.bcss.collect.naxa.notificationslist;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.dto.Instance;
import org.bcss.collect.android.listeners.DownloadFormsTaskListener;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.RxDownloader.RxDownloader;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.tasks.DownloadFormsTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_AUTH_REQUIRED;
import static org.odk.collect.android.utilities.DownloadFormListUtils.DL_ERROR_MSG;

public class FlagFormRemoteSource {
    private static FlagFormRemoteSource INSTANCE;
    String INSTANCES_PATH = Collect.INSTANCES_PATH.replace(Environment.getExternalStorageDirectory().toString(), "");
    private InstancesDao instancesDao;


    public static FlagFormRemoteSource getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new FlagFormRemoteSource();
        }
        return INSTANCE;
    }

    private FlagFormRemoteSource() {
        this.instancesDao = new InstancesDao();
    }

    Observable<String> getXMLInstance(String submissionId) {
        String INSTANCES_PATH = Collect.FORMS_PATH.replace(Environment.getExternalStorageDirectory().toString(), "");

        String url = String.format(APIEndpoint.BASE_URL + "/forms/api/instance/download_xml_version/%s", submissionId);


        return new RxDownloader(Collect.getInstance())
                .download(url, "temp.xml", INSTANCES_PATH, "*/*", true);
    }


    Observable<Uri> runAll(FieldSightNotification fieldSightNotification) {
        return getKOBOForm(fieldSightNotification)
                .toObservable()
                .flatMap(new Function<String, ObservableSource<Uri>>() {
                    @Override
                    public ObservableSource<Uri> apply(String s) {
                        return getODKInstance(fieldSightNotification);
                    }
                });
    }

    private Single<String> getKOBOForm(FieldSightNotification notificationFormDetail) {

        String fsFormId = notificationFormDetail.getFsFormId();
        String siteId = notificationFormDetail.getSiteId();
        String formName = notificationFormDetail.getFormName();
        String fsFormSubmissionId = notificationFormDetail.getFormSubmissionId();
        String jrFormId = "";
        return getODKForm(fsFormId, siteId, formName, fsFormSubmissionId, jrFormId);
    }


    public void getODKForm(String formName, String fsSubmissionId, String jrFormId, DownloadFormsTaskListener listener) {
        String downloadUrl = String.format(APIEndpoint.BASE_URL + "/forms/api/instance/download_xml_version/%s", fsSubmissionId);
        DownloadFormsTask mDownloadFormsTask = new DownloadFormsTask();
        ArrayList<FormDetails> filesToDownload = new ArrayList<FormDetails>();
        FormDetails formDetails = new FormDetails(formName,
                downloadUrl,
                null,
                jrFormId,
                null,
                null,
                null,
                false,
                false);

        filesToDownload.add(formDetails);
        mDownloadFormsTask.setDownloaderListener(listener);
        mDownloadFormsTask.execute(filesToDownload);

    }

    private Single<String> getODKForm(String fsFormId, String siteId, String formName, String fsFormSubmissionId, String jrFormId) {


        return Single.create(emitter -> {
            String downloadUrl = String.format(APIEndpoint.BASE_URL + "/forms/api/instance/download_xml_version/%s", fsFormSubmissionId);
            ArrayList<FormDetails> filesToDownload = new ArrayList<FormDetails>();
            DownloadFormsTask mDownloadFormsTask = new DownloadFormsTask();

            FormDetails formDetails = new FormDetails(formName,
                    downloadUrl,
                    null,
                    jrFormId,
                    null,
                    null,
                    null,
                    false,
                    false);

            filesToDownload.add(formDetails);
            Timber.i("FLAG: Preparing to download xml form of the name %s", formDetails.getFormName());

            mDownloadFormsTask.setDownloaderListener(new DownloadFormsTaskListener() {
                @Override
                public void formsDownloadingComplete(HashMap<FormDetails, String> result) {
                    String msg = result.get(formDetails);


                    if (result.containsKey(DL_ERROR_MSG)) {
                        Timber.e(" Forms could not be downloaded ");
                        msg = "Forms could not be downloaded ";
                    }
                    if (result.containsKey(DL_AUTH_REQUIRED)) {
                        Timber.e(" Mismatched token");
                        msg = "Mismatched token";
                    }

                    if ("Success".equals(msg)) {
                        emitter.onSuccess(msg);
                    } else {
                        emitter.onError(new RuntimeException(msg));
                    }

                    Timber.i("FLAG: Download completed for %s with message: %s ", formDetails.getFormName(), msg);
                }

                @Override
                public void progressUpdate(String currentFile, int progress, int total) {
                    Timber.i("FLAG:  Current file: %s, progress: %d, total: %d", currentFile, progress, total);
                }

                @Override
                public void formsDownloadingCancelled() {
                    emitter.onError(new RuntimeException("Download canceled"));
                    Timber.i("FLAG: Form Download Cancled ");
                }
            });

            mDownloadFormsTask.execute(filesToDownload);
            Timber.i("FLAG: Download Task Started For %s", formDetails.getFormName());
        });

    }

    public Observable<Uri> getODKInstance(FieldSightNotification notificationFormDetail) {

        String siteId = notificationFormDetail.getSiteId();
        String formName = notificationFormDetail.getFormName();
        String fsFormSubmissionId = notificationFormDetail.getFormSubmissionId();
        String jrFormId = notificationFormDetail.getIdString();
        String downloadUrl = String.format(APIEndpoint.BASE_URL + "/forms/api/instance/download_submission/%s", fsFormSubmissionId);

        Instance.Builder flaggedInstance = new Instance.Builder()
                .status(InstanceProviderAPI.STATUS_COMPLETE)
                .jrFormId(jrFormId)
                .fieldSightSiteId(siteId == null ? "0" : siteId)//survey form have 0 as siteId
                .displayName(formName)
                .canEditWhenComplete("true")
                .displaySubtext("");


        return Observable.just(flaggedInstance)
                .flatMap((Function<Instance.Builder, ObservableSource<Uri>>) instance -> {
                    String pathToDownload = getInstanceFolderPath(formatFileName(instance.build().getDisplayName()));

                    return new RxDownloader(Collect.getInstance())
                            .download(downloadUrl,
                                    formatFileName(formName).concat(".xml"),
                                    pathToDownload,
                                    "*/*",
                                    true)
                            .map(new Function<String, Uri>() {
                                @Override
                                public Uri apply(String path) {
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

                                    ContentValues values = instancesDao.getValuesFromInstanceObject(instance.build());
                                    Uri instanceUri = instancesDao.saveInstance(values);
                                    Timber.i("Downloaded and saved instance at %s", path);
                                    return instanceUri;
                                }
                            });
                });
    }

    private String formatFileName(String text) {
        return text.replace(" ", "_");
    }

    public static String getColumnString(Cursor cursor, String columnName) {
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

    private Observable<Uri> getODKInstance(String fsFormId, String siteId, String formName, String jrFormId, Long date, String downloadUrl) {

        Instance.Builder flaggedInstance = new Instance.Builder()
                .status(InstanceProviderAPI.STATUS_FLAGGED)
                .jrFormId(jrFormId)
                .fieldSightSiteId(siteId == null ? "0" : siteId)//survey form have 0 as siteId
                .displayName(formName)
                .displaySubtext("");


        return Observable.just(flaggedInstance)
                .flatMap((Function<Instance.Builder, ObservableSource<Uri>>) instance -> {
                    String pathToDownload = getInstanceFolderPath(instance.build().getDisplayName());

                    return new RxDownloader(Collect.getInstance())
                            .download(downloadUrl,
                                    formName.concat(".xml"),
                                    pathToDownload,
                                    "*/*",
                                    true)
                            .map(new Function<String, Uri>() {
                                @Override
                                public Uri apply(String path) {
                                    path = path.replace("file:///", "");

                                    instance.instanceFilePath(path);
                                    instance.lastStatusChangeDate(System.currentTimeMillis());
                                    ContentValues values = instancesDao.getValuesFromInstanceObject(instance.build());
                                    Uri instanceUri = instancesDao.saveInstance(values);
                                    Timber.i("Downloaded and saved instance at %s", path);
                                    return instanceUri;
                                }
                            });
                });


    }


}
