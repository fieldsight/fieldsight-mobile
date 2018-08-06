package org.odk.collect.naxa.project.data;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseRemoteDataSource;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.SharedPreferenceUtils;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.MySites;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.site.db.SiteLocalSource;
import org.odk.collect.naxa.site.db.SiteRemoteSource;
import org.odk.collect.naxa.site.db.SiteRepository;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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

    @Override
    public void getAll() {
        int uid = Constant.DownloadUID.PROJECT_SITES;
        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()
                .flatMap(new Function<MeResponse, ObservableSource<List<MySites>>>() {
                    @Override
                    public ObservableSource<List<MySites>> apply(MeResponse meResponse) throws Exception {
                        String user = new Gson().toJson(meResponse.getData());
                        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(),SharedPreferenceUtils.PREF_KEY.USER,user);
                        return Observable.just(meResponse.getData().getMySitesModel());
                    }
                })
                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteRepository.saveSitesAsVerified(mySites.getSite(), mySites.getProject());
                    projectLocalSource.save(mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Project>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));

                    }

                    @Override
                    public void onSuccess(List<Project> projects) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_END));
                        syncRepository.setSuccess(Constant.DownloadUID.PROJECT_SITES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_ERROR));
                        syncRepository.setFailed(Constant.DownloadUID.PROJECT_SITES);
                    }
                });

    }


}
