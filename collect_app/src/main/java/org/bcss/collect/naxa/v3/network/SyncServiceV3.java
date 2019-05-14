package org.bcss.collect.naxa.v3.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SyncServiceV3 extends IntentService {
    /***
     *
     *
     * @Author: Yubaraj Poudel
     * @Since : 14/05/2019
     */

    int projectIndex = 0;
    int regionIndex = 0;
    ArrayList<Project> selectedProject;

    public SyncServiceV3() {
        super("SyncserviceV3");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            selectedProject = Objects.requireNonNull(intent).getParcelableArrayListExtra("projects");
            Timber.i("SyncServiceV3 slectedProject size = %d", selectedProject.size());

            HashMap<String, List<Syncable>> selectedMap = (HashMap<String, List<Syncable>>) intent.getSerializableExtra("selection");

            for (String key : selectedMap.keySet()) {
                Timber.i(readaableSyncParams(key, selectedMap.get(key)));
            }

            downloadByRegionObservable(selectedProject, selectedMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<SiteResponse>() {
                @Override
                public void onNext(SiteResponse siteResponse) {
                    Timber.i(siteResponse.toString());
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

//            maintain the sync history
//            for projects
//            iterate through the region


        } catch (
                NullPointerException e) {
            Timber.i("Null Pointer");
            e.printStackTrace();
        }

    }

    private Observable<SiteResponse> downloadByRegionObservable(ArrayList<Project> selectedProject, HashMap<String, List<Syncable>> selectedMap) {
        return Observable.just(selectedProject)
                .flatMapIterable(new Function<ArrayList<Project>, Iterable<Project>>() {
                    @Override
                    public Iterable<Project> apply(ArrayList<Project> projects) throws Exception {
                        return projects;
                    }
                })
                .filter(new Predicate<Project>() {
                    @Override
                    public boolean test(Project project) throws Exception {
                        List<Syncable> list = selectedMap.get(project.getId());
                        boolean shouldDownloadSites = false;
                        for (Syncable s : list) {
                            shouldDownloadSites = TextUtils.equals("Regions and sites", s.getTitle());
                            if (shouldDownloadSites) break;
                        }
                        return shouldDownloadSites;
                    }
                })
                .map(new Function<Project, List<Region>>() {
                    @Override
                    public List<Region> apply(Project project) throws Exception {
                        return project.getRegionList();
                    }
                })
                .flatMapIterable(new Function<List<Region>, Iterable<Region>>() {
                    @Override
                    public Iterable<Region> apply(List<Region> regions) throws Exception {
                        return regions;
                    }
                })
                .flatMapSingle(new Function<Region, SingleSource<SiteResponse>>() {
                    @Override
                    public SingleSource<SiteResponse> apply(Region region) throws Exception {
                        return SiteRemoteSource.getInstance().getSitesByRegionId(region.getId());
                    }
                });

    }

    void sartSync() {
        if (projectIndex < selectedProject.size()) {
            Project p = selectedProject.get(projectIndex);

        }
    }


    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.getSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }
}
