package org.fieldsight.naxa.forms.data.remote;

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

public class FieldSightFormRemoteSourceV3 {

    private static FieldSightFormRemoteSourceV3 INSTANCE;
    private FormsDao formsDao;
    FieldSightFormDownloader fieldSightFormDownloader = new FieldSightFormDownloader(false);
    // holds the projectid and total number of forms
    SparseIntArray projectIdUrlMap;
    SparseIntArray downloadProjectFormProgressUrlMap;

    public static FieldSightFormRemoteSourceV3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormRemoteSourceV3();
        }
        return INSTANCE;
    }

    private FieldSightFormRemoteSourceV3() {
        this.formsDao = new FormsDao();
    }

    private String buildUrlWithParams(List<Project> projects) {
        StringBuilder url = new StringBuilder();
        url.append(APIEndpoint.V3.GET_FORMS);
        url.append("?");
        for (int i = 0; i < projects.size(); i++) {
            url.append(APIEndpoint.PARAMS.PROJECT_ID);
            url.append("=");
            url.append(projects.get(i).getId());
            if (i < projects.size() - 1) {
                url.append("&");
            }
        }
        return url.toString();
    }

    public Observable<Pair<FieldsightFormDetailsv3, String>> getFormUsingProjectId(List<Project> projects) {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getFormsFromUrlAsRaw(buildUrlWithParams(projects))
                .map(this::mapToFieldSightFormDetailsV3)
                .doOnNext(new Consumer<ArrayList<FieldsightFormDetailsv3>>() {
                    @Override
                    public void accept(ArrayList<FieldsightFormDetailsv3> fieldSightFormDetails) {
                        FieldSightFormsLocalSourcev3.getInstance().save(fieldSightFormDetails);
                    }
                })
                .flatMap((Function<ArrayList<FieldsightFormDetailsv3>, ObservableSource<Pair<FieldsightFormDetailsv3, String>>>) fieldSightFormDetails -> {
                    return Observable.just(fieldSightFormDetails)
                            .flatMapIterable((Function<ArrayList<FieldsightFormDetailsv3>, Iterable<FieldsightFormDetailsv3>>) fieldSightFormDetailsV31 -> fieldSightFormDetailsV31)
                            .filter(new Predicate<FieldsightFormDetailsv3>() {
                                @Override
                                public boolean test(FieldsightFormDetailsv3 fieldsightFormDetailsv3) throws Exception {
                                    String hash = fieldsightFormDetailsv3.getFormDetails().getHash();
                                    boolean isALreadyDownloadedOrInvalidFormat = true;
                                    if(hash.matches("\\w+:\\w+")) {
                                        String formHash = hash.split(":")[0];
                                        isALreadyDownloadedOrInvalidFormat = formsDao.getFormsCursorForMd5Hash(formHash).getCount() > 0;

                                    }
                                    Timber.i("FieldsightFormRemoteSourcev3, isALreadyDownloaded = " + isALreadyDownloadedOrInvalidFormat + "skipping download " + fieldsightFormDetailsv3.getFormDetails().getDownloadUrl());
                                    return !isALreadyDownloadedOrInvalidFormat;
                                }
                            })
                            .concatMap((Function<FieldsightFormDetailsv3, ObservableSource<Pair<FieldsightFormDetailsv3, String>>>) fieldSightFormDetailV3 ->
                            {
                                int projectId = getProjectId(fieldSightFormDetailV3);
                                SyncLocalSource3.getInstance().updateDownloadProgress(projectId+"", 1, projectIdUrlMap.get(projectId));
                                return downloadSingleForm(fieldSightFormDetailV3);
                            })
                            .doOnNext(new Consumer<Pair<FieldsightFormDetailsv3, String>>() {
                                @Override
                                public void accept(Pair<FieldsightFormDetailsv3, String> fieldSightFormDetailsStringPair) {
                                    String message = fieldSightFormDetailsStringPair.second;
                                    int projectId = getProjectId(fieldSightFormDetailsStringPair.first);
                                    downloadProjectFormProgressUrlMap.put(projectId, downloadProjectFormProgressUrlMap.get(projectId, 0) + 1);
                                    Timber.i(message);
                                    if(downloadProjectFormProgressUrlMap.get(projectId) == projectIdUrlMap.get(projectId)) {
                                        SyncLocalSource3.getInstance().markAsCompleted(projectId+"", 1);
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
            if(formKey.equals("stage")) {
                List<FieldsightFormDetailsv3> fieldsightFormDetailsv3List = FieldsightFormDetailsv3.fieldsightFormDetailsV3FromJSON(formJsonArray);
                fieldSightFormsv3List.addAll(fieldsightFormDetailsv3List);
                for(FieldsightFormDetailsv3 fieldsightFormDetailsv3 : fieldsightFormDetailsv3List) {
                    updateProjectIdUrlMap(fieldsightFormDetailsv3);
                }
            } else {
                for (int i = 0; i < formJsonArray.length(); i++) {
                    JSONObject formJSON = formJsonArray.getJSONObject(i);
                    FieldsightFormDetailsv3 fieldsightFormDetailsv3 = FieldsightFormDetailsv3.parseFromJSON(formJSON, formKey);
                     fieldSightFormsv3List.add(fieldsightFormDetailsv3);
                     updateProjectIdUrlMap(fieldsightFormDetailsv3);
                }
            }
        }
        for(int i = 0; i < projectIdUrlMap.size(); i ++ ) {
            SyncLocalSource3.getInstance().markAsQueued(projectIdUrlMap.keyAt(i)+"", 1);
        }
        return fieldSightFormsv3List;
    }


    private void updateProjectIdUrlMap(FieldsightFormDetailsv3 fieldsightFormDetailsv3) {
        int projectId = getProjectId(fieldsightFormDetailsv3);
        projectIdUrlMap.put(projectId, projectIdUrlMap.get(projectId, 0) + 1);
    }
    private Integer getProjectId(FieldsightFormDetailsv3 fieldSightForm) {
        String value = TextUtils.isEmpty(fieldSightForm.getProject())? fieldSightForm.getSite_project_id() : fieldSightForm.getProject();
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
