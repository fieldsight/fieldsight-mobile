package org.odk.collect.naxa.project;


import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.MySites;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.project.event.ErrorEvent;
import org.odk.collect.naxa.project.event.PayloadEvent;
import org.odk.collect.naxa.project.event.ProgressEvent;
import org.odk.collect.naxa.project.event.SucessEvent;
import org.odk.collect.naxa.site.db.SiteRepository;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
                    siteViewModel.insert(mySites.getSite());
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
                EventBus.getDefault().post(new ProgressEvent());
            }

            @Override
            public void onSuccess(List<Project> projects) {
                EventBus.getDefault().post(new PayloadEvent(projects));
                EventBus.getDefault().post(new SucessEvent());


            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                EventBus.getDefault().post(new SucessEvent());
            }
        };
    }
}
