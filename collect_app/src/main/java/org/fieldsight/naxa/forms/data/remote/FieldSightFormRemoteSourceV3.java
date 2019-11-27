package org.fieldsight.naxa.forms.data.remote;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;

import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSourcev3;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.v3.forms.FieldSightFormDownloader;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.fieldsight.naxa.v3.network.SyncLocalSource3;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.dao.FormsDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import okhttp3.ResponseBody;
import timber.log.Timber;

@SuppressWarnings("PMD")
public class FieldSightFormRemoteSourceV3 {

    private static FieldSightFormRemoteSourceV3 fieldSightFormRemoteSourceV3;
    private final FormsDao formsDao;
    FieldSightFormDownloader fieldSightFormDownloader = new FieldSightFormDownloader(false);
    // holds the projectid and total number of FORMS
    SparseIntArray projectIdUrlMap;
    SparseIntArray downloadProjectFormProgressUrlMap;

    public static FieldSightFormRemoteSourceV3 getInstance() {
        if (fieldSightFormRemoteSourceV3 == null) {
            fieldSightFormRemoteSourceV3 = new FieldSightFormRemoteSourceV3();
        }
        return fieldSightFormRemoteSourceV3;
    }

    private FieldSightFormRemoteSourceV3() {
        this.formsDao = new FormsDao();
    }


    private String buildUrlWithParams(List<Project> projects) {
        StringBuilder url = new StringBuilder();
        url.append(APIEndpoint.V3.GET_FORMS);
        url.append('?');
        for (int i = 0; i < projects.size(); i++) {
            url.append(APIEndpoint.PARAMS.PROJECT_ID);
            url.append('=');
            url.append(projects.get(i).getId());
            if (i < projects.size() - 1) {
                url.append('&');
            }
        }
        return url.toString();
    }


    // for download
    public Observable<Pair<FieldsightFormDetailsv3, String>> getFormUsingProjectId(List<Project> projects) {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getFormsFromUrlAsRaw(buildUrlWithParams(projects))
                .map(this::mapToFieldSightFormDetailsV3)
                .doOnNext(new Consumer<ArrayList<FieldsightFormDetailsv3>>() {
                    @Override
                    public void accept(ArrayList<FieldsightFormDetailsv3> fieldSightFormDetails) {

                        String[] projectIds = new String[projects.size()];
                        for (int i = 0; i < projects.size(); i++) {
                            projectIds[i] = projects.get(i).getId();
                        }

                        if (fieldSightFormDetails.isEmpty()) {
                            for (String projectId : projectIds) {
                                SyncLocalSource3.getInstance().markAsCompleted(String.valueOf(projectId), 1);
                            }
                        } else {
                            FieldSightFormsLocalSourcev3.getInstance().updateAll(fieldSightFormDetails, projectIds);
                        }


                    }
                })
                .flatMap((Function<ArrayList<FieldsightFormDetailsv3>, ObservableSource<Pair<FieldsightFormDetailsv3, String>>>) fieldSightFormDetails -> {
                    return Observable.just(fieldSightFormDetails)
                            .flatMapIterable((Function<ArrayList<FieldsightFormDetailsv3>, Iterable<FieldsightFormDetailsv3>>) fieldSightFormDetailsV31 -> fieldSightFormDetailsV31)
                            .filter(new Predicate<FieldsightFormDetailsv3>() {
                                @Override
                                public boolean test(FieldsightFormDetailsv3 fieldsightFormDetailsv3) throws Exception {
                                    String hash = fieldsightFormDetailsv3.getFormDetails().getHash();
                                    boolean downloadFile;
                                    if (hash.matches("\\w+:\\w+")) {
                                        String formHash = hash.split(":")[1];
                                        Cursor fileCursor = formsDao.getFormsCursorForMd5Hash(formHash);
                                        Timber.i("FieldsightFormRemoteSourcev3, formhash = %s, cursor = %s count = %d", formHash, fileCursor.getColumnCount(), fileCursor.getCount());
                                        downloadFile = fileCursor.getCount() < 2;
                                    } else {
                                        downloadFile = false;
                                    }
                                    if (!downloadFile) {
                                        projectIdUrlMap.put(getProjectId(fieldsightFormDetailsv3), projectIdUrlMap.get(getProjectId(fieldsightFormDetailsv3)) - 1);
                                        Timber.i("FieldsightformRemotesourcev3, projectIdUrlMapSize = %d", projectIdUrlMap.size());
                                    }
                                    if (projectIdUrlMap.get(getProjectId(fieldsightFormDetailsv3)) == 0) {
                                        SyncLocalSource3.getInstance().markAsCompleted(String.valueOf(getProjectId(fieldsightFormDetailsv3)), 1);
                                    }
                                    Timber.i("FieldsightFormRemoteSourcev3, downloadFile = " + downloadFile + " skipping download " + fieldsightFormDetailsv3.getFormDetails().getDownloadUrl());
                                    return downloadFile;
                                }
                            })
                            .concatMap((Function<FieldsightFormDetailsv3, ObservableSource<Pair<FieldsightFormDetailsv3, String>>>) fieldSightFormDetailV3 ->
                            {
                                int projectId = getProjectId(fieldSightFormDetailV3);
                                int progressCount = downloadProjectFormProgressUrlMap.get(projectId, 0) + 1;
                                downloadProjectFormProgressUrlMap.put(projectId, progressCount);
                                SyncLocalSource3.getInstance().updateDownloadProgress(String.valueOf(projectId), progressCount, projectIdUrlMap.get(projectId));
                                return downloadSingleForm(fieldSightFormDetailV3);
                            })
                            .doOnNext(new Consumer<Pair<FieldsightFormDetailsv3, String>>() {
                                @Override
                                public void accept(Pair<FieldsightFormDetailsv3, String> fieldSightFormDetailsStringPair) {
                                    String message = fieldSightFormDetailsStringPair.second;
                                    int projectId = getProjectId(fieldSightFormDetailsStringPair.first);
                                    Timber.i("FieldsightformRemotesourcev3, response = %s", message);
                                    Timber.i("FieldsightformRemotesourcev3, downloadCount = %d, projectCunt = %d", downloadProjectFormProgressUrlMap.get(projectId), projectIdUrlMap.get(projectId));
                                    if (downloadProjectFormProgressUrlMap.get(projectId) == projectIdUrlMap.get(projectId)) {
                                        SyncLocalSource3.getInstance().markAsCompleted(String.valueOf(projectId), 1);
                                    }
                                }
                            });
                });
    }

    private ArrayList<FieldsightFormDetailsv3> mapToFieldSightFormDetailsV3(ResponseBody responseBody) throws IOException, JSONException {
        projectIdUrlMap = new SparseIntArray();
        downloadProjectFormProgressUrlMap = new SparseIntArray();
        JSONObject response = new JSONObject(responseBody.string());
        Iterator<String> formTypes = response.keys();
        ArrayList<FieldsightFormDetailsv3> fieldSightFormsv3List = new ArrayList<>();
        while (formTypes.hasNext()) {
            String formKey = formTypes.next();
            JSONArray formJsonArray = response.getJSONArray(formKey);
            if (formKey.equals("stage")) {
                List<FieldsightFormDetailsv3> fieldsightFormDetailsv3List = FieldsightFormDetailsv3.fieldsightFormDetailsV3FromJSON(formJsonArray);
                fieldSightFormsv3List.addAll(fieldsightFormDetailsv3List);
                for (FieldsightFormDetailsv3 fieldsightFormDetailsv3 : fieldsightFormDetailsv3List) {
                    updateProjectIdUrlMap(fieldsightFormDetailsv3);
                }
            } else {
                for (int i = 0; i < formJsonArray.length(); i++) {
                    JSONObject formJSON = formJsonArray.getJSONObject(i);
                    Timber.i("FieldsightFormRemoteSourcev3, type = %s, data = %s", formKey, formJSON.toString());
                    FieldsightFormDetailsv3 fieldsightFormDetailsv3 = FieldsightFormDetailsv3.parseFromJSON(formJSON, formKey);
                    fieldSightFormsv3List.add(fieldsightFormDetailsv3);
                    updateProjectIdUrlMap(fieldsightFormDetailsv3);
                }
            }
        }

        for (int i = 0; i < projectIdUrlMap.size(); i++) {
            SyncLocalSource3.getInstance().markAsQueued(String.valueOf(projectIdUrlMap.keyAt(i)), 1);
        }
        return fieldSightFormsv3List;
    }


    private void updateProjectIdUrlMap(FieldsightFormDetailsv3 fieldsightFormDetailsv3) {
        int projectId = getProjectId(fieldsightFormDetailsv3);
        projectIdUrlMap.put(projectId, projectIdUrlMap.get(projectId, 0) + 1);
    }

    private Integer getProjectId(FieldsightFormDetailsv3 fieldSightForm) {
        String value = TextUtils.isEmpty(fieldSightForm.getProject()) || TextUtils.equals(fieldSightForm.getProject(), "null") ? fieldSightForm.getSite_project_id() : fieldSightForm.getProject();
        Timber.i("Fieldsightformremotesourcev3, id = %s, value = %s, projectName = %s, project = %s, site_project_id = %s",
                fieldSightForm.getId(), value, fieldSightForm.getDescription(), fieldSightForm.getProject(), fieldSightForm.getSite_project_id());
        return Integer.parseInt(value);
    }


    private Observable<Pair<FieldsightFormDetailsv3, String>> downloadSingleForm(FieldsightFormDetailsv3 fieldsightFormDetailsv3) {
        return Observable.fromCallable(new Callable<Pair<FieldsightFormDetailsv3, String>>() {
            @Override
            public Pair<FieldsightFormDetailsv3, String> call() throws Exception {
                return fieldSightFormDownloader.downloadSingleFieldSightForm(fieldsightFormDetailsv3);
            }
        });
    }
}
