package org.bcss.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bcss.collect.naxa.common.BaseRepository;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRepository;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.ProjectBuilder;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
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


    public void getAll(LoadProjectCallback callback) {
        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Project>>() {
                    @Override
                    public void onSuccess(List<Project> projects) {
                        boolean isDataNotAvailable = projects.isEmpty();
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


    public interface LoadProjectCallback {
        void onProjectLoaded(List<Project> projects);

        void onDataNotAvailable();
    }


    private void getProjectFromRemoteSource(LoadProjectCallback callback) {


        ProjectRemoteSource.getInstance().getProjects()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, ObservableSource<JSONObject>>() {
                    @Override
                    public ObservableSource<JSONObject> apply(ResponseBody responseBody) throws Exception {
                        JSONArray jsonArray = new JSONArray(responseBody.string());
                        return Observable.range(0, jsonArray.length())
                                .map(jsonArray::getJSONObject);
                    }
                })
                .map(new Function<JSONObject, Project>() {
                    @Override
                    public Project apply(JSONObject jsonObject) throws Exception {
                        return mapJSONtoProject(jsonObject);
                    }

                    private Project mapJSONtoProject(JSONObject json) throws JSONException {

                        return new ProjectBuilder()
                                .setName(json.getString("name"))
                                .setId(json.getString("id"))
                                .setAddress(json.getString("address"))
                                .setMetaAttributes(mapJSONtoMetaArributes(json.getJSONArray("meta_attributes")))
                                .setOrganizationName(json.getJSONObject("organization").getString("name"))
                                .createProject();
                    }

                    private List<SiteMetaAttribute> mapJSONtoMetaArributes(JSONArray jsonArray) {
                        Type siteMetaAttrsList = new TypeToken<List<SiteMetaAttribute>>() {
                        }.getType();
                        return new Gson().fromJson(jsonArray.toString(), siteMetaAttrsList);
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<Project>>() {
                    @Override
                    public void onSuccess(List<Project> list) {
                        refreshLocalDataSource(list);
                        callback.onProjectLoaded(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        callback.onDataNotAvailable();
                    }
                });
    }

    private void refreshLocalDataSource(List<Project> list) {
        localSource.save(list);
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


}
