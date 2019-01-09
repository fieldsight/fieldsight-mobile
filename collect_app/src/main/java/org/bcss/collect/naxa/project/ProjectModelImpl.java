package org.bcss.collect.naxa.project;


import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.login.model.MeResponse;
import org.bcss.collect.naxa.login.model.MySites;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.site.db.SiteViewModel;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ProjectModelImpl implements ProjectModel {

    private SiteViewModel siteViewModel;

    public ProjectModelImpl() {
        this.siteViewModel = new SiteViewModel(Collect.getInstance());
    }

    @Override
    public void downloadUserInformation() {
        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()

                .flatMap((Function<MeResponse, ObservableSource<List<MySites>>>) meResponse -> Observable.just(meResponse.getData().getMySitesModel()))

                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteViewModel.insertSitesAsVerified(mySites.getSite(),mySites.getProject());
                    ProjectLocalSource.getInstance().save(mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObsebable());
    }

    private SingleObserver<? super List<Project>> getObsebable() {

        return new SingleObserver<List<Project>>() {
            @Override
            public void onSubscribe(Disposable d) {
                EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.PROJECT_SITES, DataSyncEvent.EventStatus.EVENT_START));
            }

            @Override
            public void onSuccess(List<Project> projects) {
                EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.PROJECT_SITES, DataSyncEvent.EventStatus.EVENT_END));


            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.PROJECT_SITES, DataSyncEvent.EventStatus.EVENT_ERROR));
            }
        };
    }
}
