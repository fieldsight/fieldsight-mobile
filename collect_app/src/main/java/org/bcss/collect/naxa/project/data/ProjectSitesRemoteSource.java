package org.bcss.collect.naxa.project.data;

import com.google.gson.Gson;

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
import org.bcss.collect.naxa.site.data.SiteCluster;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.site.db.SiteRepository;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
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
        syncRepository = new SyncRepository(Collect.getInstance());
    }


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
                                .getClusterByProjectId(project.getId())
                                .flatMap(new Function<List<SiteCluster>, ObservableSource<Project>>() {
                                    @Override
                                    public ObservableSource<Project> apply(List<SiteCluster> siteClusters) throws Exception {
                                        String value = new Gson().toJson(siteClusters);
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
        fetchProjecSites()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Project>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));
                        SyncRepository.getInstance().showProgress(Constant.DownloadUID.PROJECT_SITES);
                    }

                    @Override
                    public void onSuccess(List<Project> projects) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_END));
                        syncRepository.setSuccess(Constant.DownloadUID.PROJECT_SITES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_ERROR));
                        syncRepository.setError(Constant.DownloadUID.PROJECT_SITES);
                    }
                });

    }


}
