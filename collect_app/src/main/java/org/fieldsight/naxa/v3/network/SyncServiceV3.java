package org.fieldsight.naxa.v3.network;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.common.collect.Sets;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.forms.data.remote.FieldSightFormRemoteSourceV2;
import org.fieldsight.naxa.forms.data.remote.FieldSightFormRemoteSourceV3;
import org.fieldsight.naxa.v3.HashMapUtils;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.fieldsight.naxa.ResponseUtils;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.common.ODKFormRemoteSource;
import org.fieldsight.naxa.common.exception.FormDownloadFailedException;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.educational.EducationalMaterialsRemoteSource;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.generalforms.data.GeneralFormRemoteSource;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.db.SiteRemoteSource;
import org.fieldsight.naxa.stages.data.Stage;
import org.fieldsight.naxa.stages.data.StageRemoteSource;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SyncServiceV3 extends IntentService {
    /***
     * @Author: Yubaraj Poudel
     * @Since : 14/05/2019
     */

    ArrayList<Project> selectedProject;
    HashMap<String, List<Syncable>> selectedMap = null;
    private List<String> failedSiteUrls = new ArrayList<>();
    private ArrayList<Disposable> syncDisposable = new ArrayList<>();
    private int downloadProgress = 0;

    public SyncServiceV3() {
        super("SyncserviceV3");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sentAction = intent.getAction();
        if (TextUtils.equals(sentAction, Constant.SERVICE.STOP_SYNC)) {
            cancelAllTask();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            selectedProject = Objects.requireNonNull(intent).getParcelableArrayListExtra("projects");
            Timber.i("SyncServiceV3 selectedProject size = %d", selectedProject.size());

            selectedMap = (HashMap<String, List<Syncable>>) intent.getSerializableExtra("selection");
            for (String key : selectedMap.keySet()) {
                Timber.i(readaableSyncParams(key, selectedMap.get(key)));
            }

            //Start syncing sites
            Disposable sitesObservable = downloadByRegionObservable(selectedProject, selectedMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Object>() {
                        @Override
                        public void onNext(Object o) {
                            //unused
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);
                        }

                        @Override
                        public void onComplete() {
                            //unused
                        }
                    });

            DisposableManager.add(sitesObservable);

            Disposable projectEduMatObservable = Observable.just(selectedProject)
                    .flatMapIterable((Function<ArrayList<Project>, Iterable<Project>>) projects -> projects)
                    .filter(project -> selectedMap.get(project.getId()).get(2).sync)
                    .flatMap(new Function<Project, Observable<String>>() {
                        @Override
                        public Observable<String> apply(Project project) throws Exception {

                            return EducationalMaterialsRemoteSource.getInstance()
                                    .getByProjectId(project.getId())
                                    .toObservable()
                                    .doOnDispose(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            markAsFailed(project.getId(), 2, "");
                                        }
                                    })
                                    .doOnSubscribe(new Consumer<Disposable>() {
                                        @Override
                                        public void accept(Disposable disposable) throws Exception {
                                            markAsRunning(project.getId(), 2);
                                        }
                                    })
                                    .onErrorReturn(throwable -> {
                                        String url = getFailedFormUrl(throwable)[0];
                                        markAsFailed(project.getId(), 2, url);
                                        return "error";
                                    })
                                    .doOnNext(o -> {
                                        boolean hasErrorBeenThrown = TextUtils.equals(o, "error");
                                        if (!hasErrorBeenThrown) {//error has been thrown
                                            markAsCompleted(project.getId(), 2);
                                        }
                                    });
                        }
                    })

                    .subscribe(projectId -> {
                        //unused
                    }, Timber::e);

            DisposableManager.add(projectEduMatObservable);

            SparseIntArray completedForms = new SparseIntArray();
            HashMapUtils hashMapUtils = new HashMapUtils();

            Disposable formDisposable = filterSelectedProjects()
                    .subscribeOn(Schedulers.io())
                    .map(projects -> {
                        for (Project project : projects) {
                            SyncLocalSource3.getInstance().markAsQueued(project.getId(), 1);
                        }
                        return projects;
                    })
                    .flatMapSingle(new Function<List<Project>, SingleSource<ArrayList<Integer>>>() {
                        @Override
                        public SingleSource<ArrayList<Integer>> apply(List<Project> projects) throws Exception {
                            return FieldSightFormRemoteSourceV3.getInstance().getFormUsingProjectId(projects)
                                    .doOnNext(fieldSightFormDetailsStringPair -> {

                                       // steps
                                        // increment the counter of synced project
                                        FieldsightFormDetailsv3 fd = fieldSightFormDetailsStringPair.first;
                                        int projectId = Integer.parseInt(TextUtils.isEmpty(fd.getSite_project_id()) ? fd.getSite_project_id() : fd.getProject());
                                        hashMapUtils.putOrUpdate(completedForms, projectId);
                                       // SyncLocalSource3.getInstance().updateDownloadProgress(projectId, completedForms.get(projectId), fd.getTotalFormsInProject());
                                        Timber.i(completedForms.toString());
                                    })
                                    .toList()
                                    .map(new Function<List<Pair<FieldsightFormDetailsv3, String>>, ArrayList<Integer>>() {
                                        @Override
                                        public ArrayList<Integer> apply(List<Pair<FieldsightFormDetailsv3, String>> pairs) throws Exception {
                                            SparseArray<ArrayList<String>> failedFormsMap = new SparseArray<>();
                                            HashSet<Integer> projectIds = new HashSet<>();
                                            HashSet<Integer> failedProjectId = new HashSet<>();

                                            for (Project project : projects) {
                                                projectIds.add(Integer.valueOf(project.getId()));
                                            }

                                            //collect project ids with failed forms
                                            for (Pair<FieldsightFormDetailsv3, String> pair : pairs) {
                                                String message = pair.second;
                                                FieldsightFormDetailsv3 fd = pair.first;
                                                int projectId = Integer.parseInt(TextUtils.isEmpty(fd.getSite_project_id()) ? fd.getSite_project_id() : fd.getProject());
                                                boolean hasDownloadFailed = !Collect.getInstance().getString(R.string.success).equals(message);
                                                if (hasDownloadFailed) {
                                                    hashMapUtils.putOrUpdate(failedFormsMap, projectId, fd.getFormDetails().getFormID());
                                                    failedProjectId.add(projectId);
                                                }

                                            }

                                            for (int i = 0; i < failedFormsMap.size(); i++) {
                                                int projectId = failedFormsMap.keyAt(i);
                                                SyncLocalSource3.getInstance().markAsFailed(String.valueOf(projectId), 1, Objects.requireNonNull(failedFormsMap.get(projectId)).toString());
                                            }


                                            Set<Integer> downloadedProjects = Sets.symmetricDifference(projectIds, failedProjectId);

                                            Timber.i("%s projects download succeeded", downloadedProjects.toString());
                                            Timber.i("%s projects download failed", failedProjectId.toString());

                                            return new ArrayList<>(downloadedProjects);
                                        }
                                    });


                        }
                    })
                    .subscribe(projectIds -> {
                        for (Integer projectId : projectIds) {
                            Timber.i("Marking %s project completed", projectId);
                            SyncLocalSource3.getInstance().markAsCompleted(String.valueOf(projectId), 1);
                        }
                    }, Timber::e);



//            Disposable formDisposable = filterSelectedProjects()
//                    .subscribeOn(Schedulers.io())
//                    .map(projects -> {
//                        for (Project project : projects) {
//                            SyncLocalSource3.getInstance().markAsQueued(project.getId(), 1);
//                        }
//                        return projects;
//                    })
//                    .flatMapSingle(new Function<List<Project>, SingleSource<ArrayList<Integer>>>() {
//                        @Override
//                        public SingleSource<ArrayList<Integer>> apply(List<Project> projects) throws Exception {
//                            return FieldSightFormRemoteSourceV2.getInstance().getFormUsingProjectId(projects)
//                                    .doOnNext(fieldSightFormDetailsStringPair -> {
//                                        FieldSightFormDetails fd = fieldSightFormDetailsStringPair.first;
//                                        String projectId = String.valueOf(fd.getProjectId());
//                                        hashMapUtils.putOrUpdate(completedForms, fd.getProjectId());
//                                        SyncLocalSource3.getInstance().updateDownloadProgress(projectId, completedForms.get(fd.getProjectId()), fd.getTotalFormsInProject());
//                                        Timber.i(completedForms.toString());
//                                    })
//                                    .toList()
//                                    .map(new Function<List<Pair<FieldSightFormDetails, String>>, ArrayList<Integer>>() {
//                                        @Override
//                                        public ArrayList<Integer> apply(List<Pair<FieldSightFormDetails, String>> pairs) throws Exception {
//                                            SparseArray<ArrayList<String>> failedFormsMap = new SparseArray<>();
//                                            HashSet<Integer> projectIds = new HashSet<>();
//                                            HashSet<Integer> failedProjectId = new HashSet<>();
//
//                                            for (Project project : projects) {
//                                                projectIds.add(Integer.valueOf(project.getId()));
//                                            }
//
//                                            //collect project ids with failed forms
//                                            for (Pair<FieldSightFormDetails, String> pair : pairs) {
//                                                String message = pair.second;
//                                                FieldSightFormDetails fd = pair.first;
//                                                boolean hasDownloadFailed = !Collect.getInstance().getString(R.string.success).equals(message);
//                                                if (hasDownloadFailed) {
//                                                    hashMapUtils.putOrUpdate(failedFormsMap, fd.getProjectId(), fd.getFormID());
//                                                    failedProjectId.add(fd.getProjectId());
//                                                }
//
//                                            }
//
//                                            for (int i = 0; i < failedFormsMap.size(); i++) {
//                                                int projectId = failedFormsMap.keyAt(i);
//                                                SyncLocalSource3.getInstance().markAsFailed(String.valueOf(projectId), 1, Objects.requireNonNull(failedFormsMap.get(projectId)).toString());
//                                            }
//
//
//                                            Set<Integer> downloadedProjects = Sets.symmetricDifference(projectIds, failedProjectId);
//
//                                            Timber.i("%s projects download succeeded", downloadedProjects.toString());
//                                            Timber.i("%s projects download failed", failedProjectId.toString());
//
//                                            return new ArrayList<>(downloadedProjects);
//                                        }
//                                    });
//
//
//                        }
//                    })
//                    .subscribe(projectIds -> {
//                        for (Integer projectId : projectIds) {
//                            Timber.i("Marking %s project completed", projectId);
//                            SyncLocalSource3.getInstance().markAsCompleted(String.valueOf(projectId), 1);
//                        }
//                    }, Timber::e);



        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }


    private Observable<List<Project>> filterSelectedProjects() {
        return Observable.just(selectedProject)
                .flatMapIterable((Function<ArrayList<Project>, Iterable<Project>>) projects -> projects)
                .filter(project -> selectedMap.get(project.getId()).get(1).sync)
                .toList()
                .toObservable();
    }


    private void cancelAllTask() {
        for (Disposable disposable : syncDisposable) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }


    private Function<Project, Observable<Object>> getSitesObservable() {
        return new Function<Project, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Project project) throws Exception {
                Observable<Object> regionSitesObservable = Observable.just(project.getRegionList())
                        .flatMapIterable((Function<List<Region>, Iterable<Region>>) regions -> regions)
                        .flatMapSingle(new Function<Region, SingleSource<SiteResponse>>() {
                            @Override
                            public SingleSource<SiteResponse> apply(Region region) {
                                return SiteRemoteSource.getInstance().getSitesByRegionId(region.getId())
                                        .doOnSuccess(saveSites());
                            }
                        })
                        .concatMap(new Function<SiteResponse, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(SiteResponse siteResponse) throws Exception {
                                if (siteResponse.getNext() == null) {
                                    return Observable.just(siteResponse);
                                }

                                return Observable.just(siteResponse)
                                        .concatWith(getSitesByUrl(siteResponse.getNext()));

                            }
                        });


                Observable<Object> projectObservable = Observable.just(project)
                        .filter(Project::getHasClusteredSites)
                        .flatMapSingle((Function<Project, SingleSource<SiteResponse>>) project1 -> SiteRemoteSource.getInstance().getSitesByProjectId(project1.getId()).doOnSuccess(saveSites()))
                        .concatMap(new Function<SiteResponse, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(SiteResponse siteResponse) {
                                if (siteResponse.getNext() == null) {
                                    return Observable.just(siteResponse);
                                }
                                return Observable.just(siteResponse)
                                        .concatWith(getSitesByUrl(siteResponse.getNext()));

                            }
                        });


                return Observable.concat(projectObservable, regionSitesObservable)
                        .doOnSubscribe(disposable -> markAsRunning(project.getId(), 0))
                        .onErrorReturn(throwable -> {
                            String url = getFailedFormUrl(throwable)[0];
                            failedSiteUrls.add(url);
                            return project.getId();
                        })
                        .doOnNext(o -> {
                            boolean hasErrorBeenThrown = o instanceof String;
                            if (!hasErrorBeenThrown) {//error has been thrown
                                markAsCompleted(project.getId(), 0);
                            }

                            if (failedSiteUrls.size() > 0) {
                                markAsFailed(project.getId(), 0, failedSiteUrls.toString());
                                failedSiteUrls.clear();
                            }
                        }).doOnDispose(new Action() {
                            @Override
                            public void run() throws Exception {
                                markAsFailed(project.getId(), 0, "");
                            }
                        });
            }
        };

    }


    private String[] getFailedFormUrl(Throwable throwable) {
        String failedUrl = "";
        String projectId = "";
        if (throwable instanceof RetrofitException) {
            RetrofitException retrofitException = (RetrofitException) throwable;
            String msg = retrofitException.getMessage();
            failedUrl = retrofitException.getUrl();
            projectId = retrofitException.getProjectId();

        }

        return new String[]{failedUrl, projectId};
    }

    private void markAsFailed(String projectId, int type, String failedUrl, boolean shouldRetry) {
        saveState(projectId, type, failedUrl, false, Constant.DownloadStatus.FAILED);
        Timber.e("Download stopped %s for project %s", failedUrl, projectId);
    }

    private void markAsFailed(String projectId, int type, String failedUrl) {
        markAsFailed(projectId, type, failedUrl, false);
    }


    private void markAsRunning(String projectId, int type) {
        saveState(projectId, type, "", true, Constant.DownloadStatus.RUNNING);
    }


    private void markAsCompleted(String projectId, int type) {
        saveState(projectId, type, "", false, Constant.DownloadStatus.COMPLETED);
        Timber.i("Download completed for project %s", projectId);
    }

    private void saveState(String projectId, int type, String failedUrl, boolean started, int status) {
        Timber.d("saving for for %d stopped at %s for %s", type, failedUrl, projectId);
        if (selectedMap != null && selectedMap.containsKey(projectId)) {
//            selectedMap.get(projectId).get(type).completed = true;
            SyncStat syncStat = new SyncStat(projectId, type + "", failedUrl, started, status, System.currentTimeMillis());
            SyncLocalSource3.getInstance().save(syncStat);
        }
    }

    private Observable<Object> downloadByRegionObservable(ArrayList<Project> selectedProject, HashMap<String, List<Syncable>> selectedMap) {
        return Observable.just(selectedProject)
                .flatMapIterable((Function<ArrayList<Project>, Iterable<Project>>) projects -> projects)
                .filter(project -> selectedMap.get(project.getId()).get(0).sync)
                .flatMap(getSitesObservable());


    }


    private Consumer<? super SiteResponse> saveSites() {
        return (Consumer<SiteResponse>) siteResponse -> {
            Timber.i("Saving %d sites", siteResponse.getResult().size());

            SiteLocalSource.getInstance().save((ArrayList<Site>) siteResponse.getResult());
        };
    }

    private ObservableSource<? extends SiteResponse> getSitesByUrl(String url) {
        return SiteRemoteSource.getInstance().getSitesByURL(url)
                .toObservable()
                .filter(new Predicate<SiteResponse>() {
                    @Override
                    public boolean test(SiteResponse siteResponse) {
                        return siteResponse.getNext() != null;
                    }
                })
                .doOnNext(saveSites())
                .flatMap(new Function<SiteResponse, ObservableSource<? extends SiteResponse>>() {
                    @Override
                    public ObservableSource<? extends SiteResponse> apply(SiteResponse siteResponse) {
                        return getSitesByUrl(siteResponse.getNext());
                    }
                });
    }

    private String getProjectId(FieldsightFormDetailsv3 fd) {
        return TextUtils.isEmpty(fd.getSite_project_id())? fd.getProject() : fd.getSite_project_id();
    }


    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.getSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }
}

