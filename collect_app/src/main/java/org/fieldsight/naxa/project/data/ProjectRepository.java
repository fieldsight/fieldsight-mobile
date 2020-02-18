package org.fieldsight.naxa.project.data;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.fieldsight.naxa.common.BaseRepository;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.SiteMetaAttribute;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.NetworkUtils;
import org.fieldsight.naxa.site.SiteType;
import org.fieldsight.naxa.site.SiteTypeLocalSource;
import org.fieldsight.naxa.v3.network.LoadProjectCallback;
import org.fieldsight.naxa.v3.network.ProjectBuilder;
import org.fieldsight.naxa.v3.network.ProjectRemoteSource;
import org.fieldsight.naxa.v3.network.Region;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;

@SuppressWarnings({"PMD.SingleMethodSingleton"})
public class ProjectRepository implements BaseRepository<Project> {

    private static ProjectRepository projectRepository;
    private final ProjectLocalSource localSource;
    private final ProjectSitesRemoteSource remoteSource;


    public static ProjectRepository getInstance() {
        return getInstance(ProjectLocalSource.getInstance(), ProjectSitesRemoteSource.getInstance());
    }

    public synchronized static ProjectRepository getInstance(ProjectLocalSource localSource, ProjectSitesRemoteSource remoteSource) {
        if (projectRepository == null) {
            projectRepository = new ProjectRepository(localSource, remoteSource);
        }
        return projectRepository;
    }


    private ProjectRepository(@NonNull ProjectLocalSource localSource, @NonNull ProjectSitesRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    @Override
    public LiveData<List<Project>> getAll(boolean forceUpdate) {
        if (forceUpdate) {
            remoteSource.getAll();
        }
        return localSource.getAll();
    }


    @Override
    public void save(Project... items) {
        AsyncTask.execute(() -> localSource.save(items));
    }

    @Override
    public void save(ArrayList<Project> items) {
        AsyncTask.execute(() -> localSource.save(items));
    }

    @Override
    public void updateAll(ArrayList<Project> items) {
        localSource.updateAll(items);
    }

    //added in v3
//    TODO before sending daata in the call
    public void getAll(LoadProjectCallback callback) {
        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Project>>() {
                    @Override
                    public void onSuccess(List<Project> projects) {
                        if (NetworkUtils.isNetworkConnected()) {
                            getProjectFromRemoteSource(callback);
                        } else {
                            callback.onProjectLoaded(projects, false);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        callback.onDataNotAvailable();
                    }
                });
    }


    private void getProjectFromRemoteSource(LoadProjectCallback callback) {
        ProjectRemoteSource.getInstance().getProjects()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(ResponseBody projecResponseBody) throws Exception {
                        JSONArray projectArray = new JSONArray(projecResponseBody.string());
                        StringBuilder urlParams = new StringBuilder();
                        boolean first = true;
                        for (int i = 0; i < projectArray.length(); i++) {
                            if (!first) {
                                urlParams.append("&");
                            }
                            first = false;
                            urlParams.append("project_id=").append(projectArray.optJSONObject(i).optString("id"));
                        }
                        String mUrl = APIEndpoint.V3.GET_PROJECT_ATTR_COUNT + "/?" + urlParams;
                        Timber.i("newUrl = %s", mUrl);

                        return ProjectRemoteSource.getInstance().getProjectCounts(mUrl).toObservable()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .flatMap((Function<ResponseBody, ObservableSource<JSONObject>>) responseBody -> {
                                    // create jsonarray with count
                                    JSONArray respArray = new JSONArray(responseBody.string());

                                    JSONArray newProjectArray = new JSONArray();
                                    for (int j = 0; j < projectArray.length(); j++) {
                                        JSONObject mJson = projectArray.optJSONObject(j);
                                        for (int k = 0; k < respArray.length(); k++) {
                                            JSONObject respObject = respArray.optJSONObject(k);
                                            if (mJson.optString("id").equals(respObject.optString("id"))) {
                                                mJson.put("total_regions", respObject.optInt("total_regions"));
                                                mJson.put("total_sites", respObject.optInt("total_sites"));
                                                mJson.put("total_users", respObject.optInt("total_users"));
                                                mJson.put("total_submissions", respObject.optInt("total_submissions"));
                                            }
                                        }
                                        newProjectArray.put(j, mJson);
                                    }
                                    Timber.i("ProjectRepository, newProjectArray = %s", newProjectArray.length());
                                    return Observable.range(0, newProjectArray.length())
                                            .map(newProjectArray::getJSONObject);
                                })
                                .map(new Function<JSONObject, Project>() {
                                    @Override
                                    public Project apply(JSONObject json) throws Exception {
                                        Project p = new ProjectBuilder()
                                                .setName(json.optString("name"))
                                                .setId(json.optString("id"))
                                                .setUrl(json.optString("url"))
                                                .setAddress(json.optString("address"))
                                                .setTermsAndLabels(json.optString("terms_and_labels"))
                                                .setMetaAttributes(mapJSONtoMetaArributes(json.optJSONArray("meta_attributes").toString()))
                                                .setOrganizationName(json.getJSONObject("organization").optString("name"))
                                                .setHasClusteredSites(json.optBoolean("has_site_role"))
                                                .setTotalRegion(json.optInt("total_regions"))
                                                .setTotalSubmission(json.optInt("total_submissions"))
                                                .setTotaluser(json.optInt("total_users"))
                                                .setTotalSites(json.optInt("total_sites"))
                                                .createProject();

                                        p.setRegionList(mapJSONtoRegionList(json.getJSONArray("project_region").toString()));
                                        ArrayList<SiteType> siteTypes = mapJSONtoSiteTypes(json.optString("types"));
                                        SiteTypeLocalSource.getInstance().deleteByProjectId(json.optString("id"));
                                        SiteTypeLocalSource.getInstance().save(siteTypes);

                                        return p;
                                    }

                                    private ArrayList<SiteType> mapJSONtoSiteTypes(String types) {
                                        if (TextUtils.isEmpty(types)) {
                                            return new ArrayList<>();
                                        }
                                        Type siteTypeToken = new TypeToken<ArrayList<SiteType>>() {
                                        }.getType();
                                        return new Gson().fromJson(types, siteTypeToken);
                                    }

                                    private List<SiteMetaAttribute> mapJSONtoMetaArributes(String jsonArray) {
                                        if (TextUtils.isEmpty(jsonArray)) {
                                            return new ArrayList<>();
                                        }
                                        Type siteMetaAttrsList = new TypeToken<List<SiteMetaAttribute>>() {
                                        }.getType();
                                        return new Gson().fromJson(jsonArray, siteMetaAttrsList);
                                    }

                                    private List<Region> mapJSONtoRegionList(String jsonArray) {


                                        List<Region> regions = new ArrayList<>();
                                        regions.add(new Region("", "Unassigned "));

                                        if (TextUtils.isEmpty(jsonArray)) {
                                            return regions;
                                        }

                                        Type regionType = new TypeToken<List<Region>>() {
                                        }.getType();
                                        regions.addAll(new Gson().fromJson(jsonArray, regionType));

                                        return regions;
                                    }
                                })
                                .toList()
                                .doOnSuccess(new Consumer<List<Project>>() {
                                    @Override
                                    public void accept(List<Project> projects) throws Exception {
                                        localSource.save((ArrayList<Project>) projects);
                                        callback.onProjectLoaded(projects, true);
                                    }
                                }).doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Timber.e(throwable);
                                        callback.onDataNotAvailable();
                                    }
                                }).toObservable();

                    }
                })
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
