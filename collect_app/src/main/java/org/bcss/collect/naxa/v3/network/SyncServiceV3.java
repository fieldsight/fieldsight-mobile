package org.bcss.collect.naxa.v3.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.bcss.collect.naxa.login.model.MySites;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
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

            //Start syncing sites
            downloadByRegionObservable(selectedProject, selectedMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<List<Site>>() {
                        @Override
                        public void onNext(List<Site> sites) {
                            Timber.i("%s", sites);

                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private Observable<List<Site>> downloadByRegionObservable(ArrayList<Project> selectedProject, HashMap<String, List<Syncable>> selectedMap) {
        return Observable.just(selectedProject)
                .flatMapIterable((Function<ArrayList<Project>, Iterable<Project>>) projects -> projects)
                .filter(project -> selectedMap.get(project.getId()).get(0).sync)
                .flatMap(new Function<Project, Observable<List<Site>>>() {
                    @Override
                    public Observable<List<Site>> apply(Project project) {

                        Observable<List<Site>> regionObservable = Observable.just(project.getRegionList())
                                .flatMapIterable((Function<List<Region>, Iterable<Region>>) regions -> regions)
                                .flatMapSingle((Function<Region, SingleSource<SiteResponse>>) region -> SiteRemoteSource.getInstance().getSitesByRegionId(region.getId()).doOnSuccess(saveSites()))
                                .filter(siteResponse -> siteResponse.getNext() != null)
                                .flatMap((Function<SiteResponse, Observable<List<Site>>>) siteResponse -> getSitesByUrl(siteResponse.getNext()));


                        Observable<List<Site>> projectObservable = Observable.just(project)
                                .filter(Project::getHasClusteredSites)
                                .flatMapSingle((Function<Project, SingleSource<SiteResponse>>) project1 -> SiteRemoteSource.getInstance().getSitesByProjectId(project1.getId()).doOnSuccess(saveSites()))
                                .filter(siteResponse -> siteResponse.getNext() != null)
                                .flatMap((Function<SiteResponse, Observable<List<Site>>>) siteResponse -> getSitesByUrl(siteResponse.getNext()));


                        return Observable.concat(regionObservable, projectObservable);


//                        return Observable.zip(projectObservable, Observable.just(new ArrayList<>()), new BiFunction<List<MySites>, List<MySites>, String>() {
//                            @Override
//                            public String apply(List<MySites> mySites, List<MySites> mySites2) throws Exception {
//                                int totalSites = mySites.size() + mySites2.size();
//                                return String.format("%s sites downloaded", totalSites);
//                            }
//                        });
                    }
                });

    }

    private Consumer<? super SiteResponse> saveSites() {
        return (Consumer<SiteResponse>) siteResponse -> {
            Timber.i("Saving %d sites", siteResponse.getResult().size());
            SiteLocalSource.getInstance().save((ArrayList<Site>) siteResponse.getResult());
        };
    }

    private Observable<List<Site>> getSitesByUrl(String url) {
        return SiteRemoteSource.getInstance().getSitesByURL(url)
                .toObservable()
                .filter(new Predicate<SiteResponse>() {
                    @Override
                    public boolean test(SiteResponse siteResponse) {
                        return siteResponse.getNext() != null;
                    }
                })
                .doOnNext(saveSites())
                .flatMap(new Function<SiteResponse, Observable<List<Site>>>() {
                    @Override
                    public Observable<List<Site>> apply(SiteResponse siteResponse) {
                        return getSitesByUrl(siteResponse.getNext());
                    }
                });
    }


    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.getSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }
}
