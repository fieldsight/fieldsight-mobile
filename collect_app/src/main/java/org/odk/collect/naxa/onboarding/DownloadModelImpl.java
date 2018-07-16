package org.odk.collect.naxa.onboarding;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.MySites;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.project.db.ProjectViewModel;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;

public class DownloadModelImpl implements DownloadModel {

    private SiteViewModel siteViewModel;
    private ProjectViewModel projectViewModel;
    private DownloadFormsTask downloadFormsTask;
    private HashMap<String, FormDetails> formNamesAndURLs;
    private ArrayList<HashMap<String, String>> formList;
    private DownloadFormListTask downloadFormListTask;

    DownloadModelImpl() {
        this.siteViewModel = new SiteViewModel(Collect.getInstance());
        this.projectViewModel = new ProjectViewModel(Collect.getInstance());
        formList = new ArrayList<>();
    }


    @Override
    public void fetchGeneralForms() {

    }

    @Override
    public void fetchProjectSites() {

        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()
                .flatMap((Function<MeResponse, ObservableSource<List<MySites>>>) meResponse -> Observable.just(meResponse.getData().getMySitesModel()))
                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteViewModel.insert(mySites.getSite());
                    projectViewModel.insert(mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObservable(Constant.DownloadUID.PROJECT_SITES));
    }

    @Override
    public void fetchODKForms() {
        XMLFormDownloadReceiver xmlFormDownloadReceiver = new XMLFormDownloadReceiver(new Handler());
        xmlFormDownloadReceiver.setReceiver((resultCode, resultData) -> {
            switch (resultCode) {
                case DownloadProgress.STATUS_RUNNING:
                    break;
                case DownloadProgress.STATUS_PROGRESS_UPDATE:
                    DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                    Timber.i(progress.getMessage());
                    break;
                case DownloadProgress.STATUS_ERROR:
                    break;
                case DownloadProgress.STATUS_FINISHED_FORM:
                    break;
            }
        });
        XMLFormDownloadService.start(Collect.getInstance(), new XMLFormDownloadReceiver(new Handler()));

    }


    private SingleObserver<? super List<Project>> getObservable(int uid) {

        return new SingleObserver<List<Project>>() {
            @Override
            public void onSubscribe(Disposable d) {
                EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));
            }

            @Override
            public void onSuccess(List<Project> projects) {
                EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_END));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
            }
        };
    }
}
