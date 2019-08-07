package org.fieldsight.naxa.project;


import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.event.DataSyncEvent;
import org.fieldsight.naxa.login.model.MeResponse;
import org.fieldsight.naxa.login.model.MySites;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.fieldsight.naxa.site.db.SiteViewModel;
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
