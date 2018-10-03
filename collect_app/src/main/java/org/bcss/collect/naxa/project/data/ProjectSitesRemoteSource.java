package org.bcss.collect.naxa.project.data;

import android.widget.EditText;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.gson.Gson;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.common.RxDownloader.RxDownloader;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.greenrobot.eventbus.EventBus;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.login.model.MeResponse;
import org.bcss.collect.naxa.login.model.MySites;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.site.db.SiteRepository;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ProjectSitesRemoteSource implements BaseRemoteDataSource<MeResponse> {
    private static ProjectSitesRemoteSource INSTANCE;
    private SiteRepository siteRepository;
    private ProjectLocalSource projectLocalSource;
    private SyncRepository syncRepository;

    public static ProjectSitesRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectSitesRemoteSource();
        }
        return INSTANCE;
    }

    public ProjectSitesRemoteSource() {
        siteRepository = SiteRepository.getInstance(SiteLocalSource.getInstance(), SiteRemoteSource.getInstance());
        projectLocalSource = ProjectLocalSource.getInstance();
        syncRepository = SyncRepository.getInstance();
    }

    public Single<List<Object>> fetchProjectAndSites() {
        return ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUser()
                .delay(10, TimeUnit.SECONDS)
                .flatMap(new Function<MeResponse, ObservableSource<MySiteResponse>>() {
                    @Override
                    public ObservableSource<MySiteResponse> apply(MeResponse meResponse) throws Exception {

                        if (!meResponse.getData().getIsSupervisor()) {
                            throw new BadUserException(meResponse.getData().getFull_name() + " has not been assigned as a site supervisor.");
                        }

                        String user = GSONInstance.getInstance().toJson(meResponse.getData());
                        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_KEY.USER, user);
                        return getPageAndNext(APIEndpoint.GET_MY_SITES);

                    }
                })
                .concatMap(new Function<MySiteResponse, Observable<MySiteResponse>>() {
                    @Override
                    public Observable<MySiteResponse> apply(MySiteResponse mySiteResponse) throws Exception {
                        return Observable.just(mySiteResponse);
                    }
                })
                .map(new Function<MySiteResponse, List<MySites>>() {
                    @Override
                    public List<MySites> apply(MySiteResponse mySiteResponse) throws Exception {
                        return mySiteResponse.getResult();
                    }
                })
                .flatMapIterable(new Function<List<MySites>, Iterable<MySites>>() {
                    @Override
                    public Iterable<MySites> apply(List<MySites> mySites) throws Exception {
                        return mySites;
                    }
                })
                .flatMap(new Function<MySites, Observable<Object>>() {
                    @Override
                    public Observable<Object> apply(MySites mySites) throws Exception {
                        Project project = mySites.getProject();
                        siteRepository.saveSitesAsVerified(mySites.getSite(), mySites.getProject());
                        projectLocalSource.save(project);

//                        Observable<String> siteDocumentsObservable = Observable.just(mySites.getSite().getSiteDocuments())
//                                .flatMapIterable((Function<List<String>, Iterable<String>>) urls -> urls)
//                                .flatMap(new Function<String, ObservableSource<String>>() {
//                                    @Override
//                                    public ObservableSource<String> apply(String url) throws Exception {
//                                        String fileName = FilenameUtils.getName(url);
//                                        boolean isPDF = FilenameUtils.getExtension(url).equalsIgnoreCase("pdf");
//                                        String savePath = isPDF ? Collect.PDF : Collect.IMAGES;
//                                        return new RxDownloader(Collect.getInstance())
//                                                .download(url, fileName, savePath, "*/*", false).subscribeOn(Schedulers.io());
//                                    }
//                                });


                        Observable<Project> siteRegionObservable = ServiceGenerator.getRxClient().create(ApiInterface.class)
                                .getRegionsByProjectId(project.getId())
                                .flatMap(new Function<List<SiteRegion>, ObservableSource<Project>>() {
                                    @Override
                                    public ObservableSource<Project> apply(List<SiteRegion> siteRegions) throws Exception {
                                        siteRegions.add(new SiteRegion("", "Unassigned ", ""));
                                        String value = GSONInstance.getInstance().toJson(siteRegions);
                                        ProjectLocalSource.getInstance().updateSiteClusters(project.getId(), value);
                                        return Observable.just(project);
                                    }
                                });

                        return Observable.concat(siteRegionObservable,Observable.just("demo"));
//                        return Observable.concat(siteDocumentsObservable, siteRegionObservable);


                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


    }


    private Observable<MySiteResponse> getPageAndNext(String url) {
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getAssignedSites(url)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof SocketTimeoutException) {
                                    return throwableObservable.delay(10, TimeUnit.SECONDS);
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .concatMap(new Function<MySiteResponse, ObservableSource<MySiteResponse>>() {
                    @Override
                    public ObservableSource<MySiteResponse> apply(MySiteResponse mySiteResponse) throws Exception {
                        if (mySiteResponse.getNext() == null) {
                            return Observable.just(mySiteResponse);
                        }

                        return Observable.just(mySiteResponse)
                                .delay(5, TimeUnit.SECONDS)
                                .concatWith(getPageAndNext(mySiteResponse.getNext()));
                    }
                });


    }

    @Deprecated
    //old sites and project api
    public Single<List<Project>> fetchProjecSites() {
        return ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()
                .flatMap(new Function<MeResponse, ObservableSource<List<MySites>>>() {
                    @Override
                    public ObservableSource<List<MySites>> apply(MeResponse meResponse) throws Exception {
                        String user = new Gson().toJson(meResponse.getData());
                        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_KEY.USER, user);
                        return Observable.just(meResponse.getData().getMySitesModel());
                    }
                })
                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .flatMap(new Function<MySites, ObservableSource<Project>>() {
                    @Override
                    public ObservableSource<Project> apply(MySites mySites) throws Exception {
                        Project project = mySites.getProject();
                        siteRepository.saveSitesAsVerified(mySites.getSite(), mySites.getProject());
                        projectLocalSource.save(project);


                        return ServiceGenerator.createService(ApiInterface.class)
                                .getRegionsByProjectId(project.getId())
                                .flatMap(new Function<List<SiteRegion>, ObservableSource<Project>>() {
                                    @Override
                                    public ObservableSource<Project> apply(List<SiteRegion> siteRegions) throws Exception {
                                        siteRegions.add(new SiteRegion("", "Unassigned", ""));
                                        String value = new Gson().toJson(siteRegions);
                                        ProjectLocalSource.getInstance().updateSiteClusters(project.getId(), value);
                                        return Observable.just(project);
                                    }
                                });
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void getAll() {
        int uid = Constant.DownloadUID.PROJECT_SITES;

        Single<List<Object>> observable = fetchProjectAndSites();

        ReactiveNetwork
                .observeNetworkConnectivity(Collect.getInstance().getApplicationContext())

                .flatMapSingle((Function<Connectivity, SingleSource<List<Object>>>) connectivity -> observable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        ProjectLocalSource.getInstance().deleteAll();
                        SiteLocalSource.getInstance().deleteAll();
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));
                        SyncRepository.getInstance().showProgress(Constant.DownloadUID.PROJECT_SITES);
                    }

                    @Override
                    public void onNext(List<Object> objectList) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_END));
                        FieldSightNotificationLocalSource.getInstance().markSitesAsRead();
                        syncRepository.setSuccess(Constant.DownloadUID.PROJECT_SITES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_ERROR));
                        syncRepository.setError(Constant.DownloadUID.PROJECT_SITES);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
