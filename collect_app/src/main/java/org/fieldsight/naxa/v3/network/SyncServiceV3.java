package org.fieldsight.naxa.v3.network;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

import org.apache.commons.io.FilenameUtils;
import org.fieldsight.naxa.common.downloader.RxDownloader;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSourcev3;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.forms.data.remote.FieldSightFormRemoteSourceV3;
import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.db.SiteRemoteSource;
import org.odk.collect.android.utilities.FileUtils;

import java.io.File;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    HashMap<String, List<Syncable>> selectedMap;
    private final List<String> failedSiteUrls = new ArrayList<>();
    private final ArrayList<Disposable> syncDisposable = new ArrayList<>();
    int currentWorkingProjectIndex;

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

            // sync form begins
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
                            return FieldSightFormRemoteSourceV3.getInstance()
                                    .getFormUsingProjectId(projects)
                                    .toList()
                                    .map(new Function<List<Pair<FieldsightFormDetailsv3, String>>, ArrayList<Integer>>() {
                                        @Override
                                        public ArrayList<Integer> apply(List<Pair<FieldsightFormDetailsv3, String>> pairs) throws Exception {
                                            HashSet<Integer> failedProjectId = new HashSet<>();
                                            //collect PROJECT ids with failed FORMS
                                            for (Pair<FieldsightFormDetailsv3, String> pair : pairs) {
                                                String message = pair.second;
                                                FieldsightFormDetailsv3 fd = pair.first;

                                                int projectId = Integer.parseInt(getProjectId(fd));
                                                boolean hasDowloadFailed = !TextUtils.isEmpty(message);
                                                if (hasDowloadFailed) {
                                                    failedProjectId.add(projectId);
                                                    // update the mark as failed. (currently failed will not be shown. new design will be added to handle this)
                                                }
                                            }

                                            Timber.i("%s projects download failed", failedProjectId.toString());
                                            return new ArrayList<>(failedProjectId);
                                        }
                                    });
                        }
                    }).subscribe();

            DisposableManager.add(formDisposable);
            // sync form ends

            // Education material sync begins
            HashMap<String, Integer> eduMaterialsMap = new HashMap<>();
            List<String> projectidList = new ArrayList<>();
            Disposable educationMaterialObserver = Observable.just(selectedProject)
                    .flatMapIterable(projects -> projects)
                    .filter(project -> selectedMap.get(project.getId()).get(2).sync)
                    .map(new Function<Project, List<String>>() {
                        @Override
                        public List<String> apply(Project project) throws Exception {
                            // get the education material from database
                            List<String> educationMaterialUrls = new ArrayList<>();
                            List<FieldsightFormDetailsv3> educationMaterial = FieldSightFormsLocalSourcev3.getInstance().getEducationMaterial(project.getId());
                            Timber.i("SyncService3, educationMaterial list = %s", educationMaterial.size());
                            for (FieldsightFormDetailsv3 fieldsightFormDetailsv3 : educationMaterial) {
                                String em = fieldsightFormDetailsv3.getEm();
                                Timber.i("SyncServicev3, em = %s", em);
                                if (TextUtils.isEmpty(em) || TextUtils.equals(em, "null")) {
                                    continue;
                                }

                                JSONObject jsonObject = new JSONObject(em);
                                // add image
                                if (jsonObject.has("em_images")) {
                                    JSONArray jsonArray = jsonObject.optJSONArray("em_images");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject imageJSON = jsonArray.optJSONObject(i);
                                        String imageUrl = imageJSON.optString("image");
                                        if (!TextUtils.isEmpty(imageUrl)) {
                                            boolean isFileAlreadyDownloaded = FileUtils.isFileExists(Collect.IMAGES + File.separator + FilenameUtils.getName(imageUrl));
                                            if (!isFileAlreadyDownloaded) {
                                                educationMaterialUrls.add(imageUrl);
                                            }
                                        }
                                    }
                                }
                                // get the pdf url
                                if (jsonObject.optBoolean("is_pdf")) {
                                    String pdfUrl = jsonObject.optString("pdf");
                                    // check if file exists
                                    if (!TextUtils.isEmpty(pdfUrl)) {
                                        boolean isFileAlreadyDownloaded = FileUtils.isFileExists(Collect.PDF + File.separator + FilenameUtils.getName(pdfUrl));
                                        Timber.i("syncServicev3, isAlreadyExists = " + isFileAlreadyDownloaded + " pdf url = %s", pdfUrl);
                                        if (!isFileAlreadyDownloaded) {
                                            educationMaterialUrls.add(pdfUrl);
                                        }
                                    }
                                }
                            }
                            if (educationMaterialUrls.size() == 0) {
                                markAsCompleted(project.getId(), 2);
                            } else {
                                markAsRunning(project.getId(), 2);
                                projectidList.add(project.getId());
                                eduMaterialsMap.put(project.getId(), educationMaterialUrls.size());
                            }
                            Timber.i("SyncServiceV3, educationMaterialurls = %d", educationMaterialUrls.size());
                            return educationMaterialUrls;
                        }
                    }).concatMapIterable(strings -> strings)
                    .concatMap(new Function<String, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(String url) throws Exception {
                            Timber.i("SyncServicev3, educationMaterial, downloading url = %s", url);
                            return RxDownloader.getINSTANCE(getApplicationContext()).download(url, FilenameUtils.getName(url), getSavePath(url), "*/*", false);
                        }
                    }).subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            Timber.i("SyncService: educationMaterials finalized = %s, currentWorkingIndex = %s, currentWorkingProject = %s", o.toString(), currentWorkingProjectIndex, projectidList.get(currentWorkingProjectIndex));
                            String projectId = projectidList.get(currentWorkingProjectIndex);
                            if (eduMaterialsMap.containsKey(projectId)) {
                                eduMaterialsMap.put(projectId, eduMaterialsMap.get(projectId) - 1);
                                if (eduMaterialsMap.get(projectId) <= 0) {
                                    markAsCompleted(projectId, 2);
                                    currentWorkingProjectIndex = currentWorkingProjectIndex + 1;
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Timber.e(throwable);
                            Timber.i("SyncService: educationMaterials error = %s", throwable.toString());
                        }
                    });
            DisposableManager.add(educationMaterialObserver);
            // education Material sync ends

        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }


    private String getSavePath(String url) {

        //todo bug RxDownloadmanager is adding /storage/emulated so remove it before we send path
        String savePath = "";
        switch (FileUtils.getFileExtension(url).toLowerCase(Locale.getDefault())) {
            case "pdf":
                savePath = Collect.PDF.replace(Environment.getExternalStorageDirectory().toString(), "");
                break;
            default:
                savePath = Collect.IMAGES.replace(Environment.getExternalStorageDirectory().toString(), "");
                break;
        }
        Timber.i("SyncServicev3, savePath = %s", url);

        return savePath;
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


    private Function<Project, Observable<List<Object>>> getSitesObservable() {
        return new Function<Project, Observable<List<Object>>>() {
            @Override
            public Observable<List<Object>> apply(Project project) throws Exception {
                Observable<Object> regionSitesObservable = Observable.just(project.getRegionList())
                        .flatMapIterable((Function<List<Region>, Iterable<Region>>) regions -> regions)
                        .filter(region -> !TextUtils.isEmpty(region.id))
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
                        .flatMapSingle((Function<Project, SingleSource<SiteResponse>>) project1 -> SiteRemoteSource.getInstance()
                                .getSitesByProjectId(project1.getId())
                                .doOnSuccess(saveSites()))
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
                        .toList()
                        .doOnSuccess(new Consumer<List<Object>>() {
                            @Override
                            public void accept(List<Object> objects) throws Exception {

                                boolean hasErrorBeenThrown = false;
                                for (Object o : objects) {
                                    hasErrorBeenThrown = o instanceof String;
                                    if (hasErrorBeenThrown) {
                                        break;
                                    }
                                }

                                if (!hasErrorBeenThrown) {//error has been thrown
                                    markAsCompleted(project.getId(), 0);
                                }

                                if (failedSiteUrls.size() > 0) {
                                    markAsFailed(project.getId(), 0, failedSiteUrls.toString());
                                    failedSiteUrls.clear();
                                }
                            }
                        })
                        .doOnDispose(new Action() {
                            @Override
                            public void run() throws Exception {
                                markAsFailed(project.getId(), 0, "");
                            }
                        })
                        .toObservable();


            }
        };

    }


    private String[] getFailedFormUrl(Throwable throwable) {
        String failedUrl = "";
        String projectId = "";
        if (throwable instanceof RetrofitException) {
            RetrofitException retrofitException = (RetrofitException) throwable;
            failedUrl = retrofitException.getUrl();
            projectId = retrofitException.getProjectId();

        }

        return new String[]{failedUrl, projectId};
    }

    private void markAsFailed(String projectId, int type, String failedUrl) {
        saveState(projectId, type, failedUrl, false, Constant.DownloadStatus.FAILED);
        Timber.e("Download stopped %s for PROJECT %s", failedUrl, projectId);
    }



    private void markAsRunning(String projectId, int type) {
        saveState(projectId, type, "", true, Constant.DownloadStatus.RUNNING);
    }


    private void markAsCompleted(String projectId, int type) {
        saveState(projectId, type, "", false, Constant.DownloadStatus.COMPLETED);
        Timber.i("Download completed for PROJECT %s", projectId);
    }

    private void saveState(String projectId, int type, String failedUrl, boolean started, int status) {
        Timber.d("saving for for %d stopped at %s for %s", type, failedUrl, projectId);
        if (selectedMap != null && selectedMap.containsKey(projectId)) {
//            selectedMap.get(projectId).get(type).completed = true;
            SyncStat syncStat = new SyncStat(projectId, String.valueOf(type) , failedUrl, started, status, System.currentTimeMillis());
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
                .doOnNext(saveSites())
                .filter(new Predicate<SiteResponse>() {
                    @Override
                    public boolean test(SiteResponse siteResponse) {
                        return siteResponse.getNext() != null;
                    }
                })

                .flatMap(new Function<SiteResponse, ObservableSource<? extends SiteResponse>>() {
                    @Override
                    public ObservableSource<? extends SiteResponse> apply(SiteResponse siteResponse) {
                        return getSitesByUrl(siteResponse.getNext());
                    }
                });
    }

    private String getProjectId(FieldsightFormDetailsv3 fd) {
        return TextUtils.isEmpty(fd.getProject()) || TextUtils.equals(fd.getProject(), "null") ? fd.getSite_project_id() : fd.getProject();
    }


    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.isSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }
}

