package org.fieldsight.naxa.forms.data.remote;

import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseIntArray;

import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSource;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSourcev3;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.v3.forms.FieldSightFormDownloader;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.logic.FormDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class FieldSightFormRemoteSourceV3 {

    private static FieldSightFormRemoteSourceV3 INSTANCE;
    private FormsDao formsDao;

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
                    FieldSightFormDownloader fieldSightFormDownloader = new FieldSightFormDownloader(false);
                    return Observable.just(fieldSightFormDetails)
                            .flatMapIterable((Function<ArrayList<FieldsightFormDetailsv3>, Iterable<FieldsightFormDetailsv3>>) fieldSightFormDetailsV31 -> fieldSightFormDetailsV31)
                            .concatMap((Function<FieldsightFormDetailsv3, ObservableSource<Pair<FieldsightFormDetailsv3, String>>>) formDetails -> downloadSingleForm(formDetails, fieldSightFormDownloader))
                            .doOnNext(new Consumer<Pair<FieldsightFormDetailsv3, String>>() {
                                @Override
                                public void accept(Pair<FieldsightFormDetailsv3, String> fieldSightFormDetailsStringPair) {
                                    if (fieldSightFormDetailsStringPair == null) {
                                        Timber.w("FieldSightFormDetails pair is null");
                                        return;
                                    }
                                    String message = fieldSightFormDetailsStringPair.second;
                                    Timber.i(message);
                                }
                            });
                });
    }

    private ArrayList<FieldsightFormDetailsv3> mapToFieldSightFormDetailsV3(ResponseBody responseBody) throws IOException, JSONException {
        JSONObject response = new JSONObject(responseBody.string());
        Iterator<String> formTypes = response.keys();
        ArrayList<FieldsightFormDetailsv3> fieldSightFormsv3List = new ArrayList<>();
//        SparseIntArray projectFormMap = new SparseIntArray();

        while (formTypes.hasNext()) {
            String formKey = formTypes.next();
            JSONArray formJsonArray = response.getJSONArray(formKey);
            if(formKey.equals("stage")) {
                fieldSightFormsv3List.addAll(FieldsightFormDetailsv3.fieldsightFormDetailsV3FromJSON(formJsonArray));
            } else {
                for (int i = 0; i < formJsonArray.length(); i++) {
                    JSONObject formJSON = formJsonArray.getJSONObject(i);
                     fieldSightFormsv3List.add(FieldsightFormDetailsv3.parseFromJSON(formJSON, formKey));
//                fieldSightForm = GSONInstance.getInstance().fromJson(form.toString(), FieldSightFormDetails.class);
//                fieldSightForm.setFormName(fieldSightForm.getOdkFormName());
//                fieldSightForm.setFormType(formKey);
//
//                if (TextUtils.equals("stage", formKey)) {
//                    //todo: stopping stage download form tests; needs to be removed - Nishon
//                    continue;
//                }
//
//                fieldSightForm.setProjectId(getProjectId(fieldSightForm));
//                fieldSightForms.add(fieldSightForm);
//
                }
            }
//            incrementFormCountForProject(projectFormMap, getProjectId(fieldSightForm));
        }

//        for (FieldSightFormDetails fd : fieldSightForms) {
//            fd.setTotalFormsInProject(projectFormMap.get(fd.getProjectId()));
//        }

        return fieldSightFormsv3List;
    }

    private Integer getProjectId(FieldSightFormDetails fieldSightForm) {
        String value = fieldSightForm.getFormDeployedProjectId() != null ? fieldSightForm.getFormDeployedProjectId() : String.valueOf(fieldSightForm.getProjectId());
        return Integer.parseInt(value);
    }


    private Observable<Pair<FieldsightFormDetailsv3, String>> downloadSingleForm(FieldsightFormDetailsv3 fieldsightFormDetailsv3, FieldSightFormDownloader formDownloader) {
        return Observable.fromCallable(new Callable<Pair<FieldsightFormDetailsv3, String>>() {
            @Override
            public Pair<FieldsightFormDetailsv3, String> call() throws Exception {
                return formDownloader.downloadSingleFieldSightForm(fieldsightFormDetailsv3);
            }
        });
    }




    private void incrementFormCountForProject(SparseIntArray projectTotalFormMap, Integer projectId) {
        projectTotalFormMap.put(projectId, projectTotalFormMap.get(projectId, 0) + 1);
    }
}
