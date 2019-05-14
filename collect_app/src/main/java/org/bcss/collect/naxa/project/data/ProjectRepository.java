package org.bcss.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.bcss.collect.naxa.common.BaseRepository;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRepository;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.v3.network.LoadProjectCallback;
import org.bcss.collect.naxa.v3.network.ProjectBuilder;
import org.bcss.collect.naxa.v3.network.ProjectRemoteSource;
import org.bcss.collect.naxa.v3.network.Region;
import org.json.JSONArray;
import org.json.JSONException;
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

public class ProjectRepository implements BaseRepository<Project> {

    private static ProjectRepository INSTANCE = null;
    private final ProjectLocalSource localSource;
    private final ProjectSitesRemoteSource remoteSource;

    private MediatorLiveData<List<ScheduleForm>> mediatorLiveData = new MediatorLiveData<>();

    public static ProjectRepository getInstance() {
        return getInstance(ProjectLocalSource.getInstance(), ProjectSitesRemoteSource.getInstance());
    }

    public static ProjectRepository getInstance(ProjectLocalSource localSource, ProjectSitesRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (GeneralFormRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProjectRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private ProjectRepository(@NonNull ProjectLocalSource localSource, @NonNull ProjectSitesRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    @Override
    public LiveData<List<Project>> getAll(boolean forceUpdate) {
        if (forceUpdate) remoteSource.getAll();
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
//                        boolean isDataNotAvailable = projects.isEmpty();
                        boolean isDataNotAvailable = true;
                        if (isDataNotAvailable) {
                            getProjectFromRemoteSource(callback);
                        } else {
                            callback.onProjectLoaded(projects);
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
                    JSONArray jsonArray = new JSONArray(responseBody.string());
                    return Observable.range(0, jsonArray.length())
                            .map(jsonArray::getJSONObject);
                })
                .map(new Function<JSONObject, Project>() {
                    @Override
                    public Project apply(JSONObject json) throws Exception {
                        Project p = new ProjectBuilder()
                                .setName(json.getString("name"))
                                .setId(json.getString("id"))
                                .setAddress(json.getString("address"))
                                .setMetaAttributes(mapJSONtoMetaArributes(json.getJSONArray("meta_attributes")))
                                .setOrganizationName(json.getJSONObject("organization").getString("name"))
                                .createProject();

                        p.setRegionList(mapJSONtoRegionList(json.getJSONArray("project_region")));
                        return p;
                    }

                    private List<SiteMetaAttribute> mapJSONtoMetaArributes(JSONArray jsonArray) {
                        Type siteMetaAttrsList = new TypeToken<List<SiteMetaAttribute>>() {
                        }.getType();
                        return new Gson().fromJson(jsonArray.toString(), siteMetaAttrsList);
                    }

                    private List<Region> mapJSONtoRegionList(JSONArray jsonArray) {
                        Type regionType = new TypeToken<List<Region>>() {
                        }.getType();
                        return new Gson().fromJson(jsonArray.toString(), regionType);
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<Project>>() {
                    @Override
                    public void onSuccess(List<Project> list) {
                        localSource.save((ArrayList<Project>) list);
                        callback.onProjectLoaded(list);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        callback.onDataNotAvailable();
                    }
                });
    }
}
