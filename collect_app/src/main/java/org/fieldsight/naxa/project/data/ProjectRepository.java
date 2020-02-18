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
import io.reactivex.functions.Function;
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
                .flatMap((Function<ResponseBody, ObservableSource<JSONObject>>) responseBody -> {

                    // call another api for project detail count

                    JSONArray jsonArray = new JSONArray(responseBody.string());
                    return Observable.range(0, jsonArray.length())
                            .map(jsonArray::getJSONObject);
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
                .subscribe(new DisposableSingleObserver<List<Project>>() {
                    @Override
                    public void onSuccess(List<Project> list) {
                        localSource.save((ArrayList<Project>) list);
                        callback.onProjectLoaded(list, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        callback.onDataNotAvailable();
                    }
                });
    }
}
