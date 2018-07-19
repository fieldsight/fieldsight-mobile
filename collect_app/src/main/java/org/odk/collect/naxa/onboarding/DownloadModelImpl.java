package org.odk.collect.naxa.onboarding;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.generalforms.GeneralForm;
import org.odk.collect.naxa.generalforms.GeneralFormLocalSource;
import org.odk.collect.naxa.generalforms.GeneralFormRemoteSource;
import org.odk.collect.naxa.generalforms.db.GeneralFormRepository;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.MySites;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.project.db.ProjectRepository;
import org.odk.collect.naxa.site.db.SiteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class DownloadModelImpl implements DownloadModel {

    private SiteRepository siteRepository;
    private GeneralFormRepository generalFormRepository;
    private ProjectRepository projectRepository;
    private DownloadFormsTask downloadFormsTask;
    private HashMap<String, FormDetails> formNamesAndURLs;
    private ArrayList<HashMap<String, String>> formList;
    private DownloadFormListTask downloadFormListTask;

    DownloadModelImpl() {
        this.siteRepository = new SiteRepository(Collect.getInstance());
        this.generalFormRepository = GeneralFormRepository.getInstance(GeneralFormLocalSource.getInstance(), GeneralFormRemoteSource.getInstance());
        this.projectRepository = new ProjectRepository(Collect.getInstance());
        formList = new ArrayList<>();

    }

    @Override
    public void fetchGeneralForms() {

        projectRepository.getAllProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<GeneralForm>>>) this::downloadGeneralForm)
                .flatMap(generalFormRespons -> {
                    generalFormRepository.save(generalFormRespons);
                    return Observable.empty();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_START));
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_ERROR));

                    }

                    @Override
                    public void onComplete() {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_END));

                    }
                });

    }


    private Observable<ArrayList<GeneralForm>> downloadGeneralForm(XMLForm xmlForm) {
        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();

        return ServiceGenerator
                .getRxClient()
                .create(ApiInterface.class)
                .getGeneralFormsObservable(createdFromProject, creatorsId)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof IOException) {
                                    return throwableObservable;
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void fetchProjectSites() {
        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()
                .flatMap((Function<MeResponse, ObservableSource<List<MySites>>>) meResponse -> Observable.just(meResponse.getData().getMySitesModel()))
                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteRepository.insertSitesAsVerified(mySites.getSite(), mySites.getProject());
                    projectRepository.insert(mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObservable(Constant.DownloadUID.PROJECT_SITES));

    }


    @Override
    public void fetchODKForms() {
        int uid = Constant.DownloadUID.ODK_FORMS;

        XMLFormDownloadReceiver xmlFormDownloadReceiver = new XMLFormDownloadReceiver(new Handler());
        xmlFormDownloadReceiver.setReceiver((resultCode, resultData) -> {
            switch (resultCode) {
                case DownloadProgress.STATUS_RUNNING:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_START));
                    break;
                case DownloadProgress.STATUS_PROGRESS_UPDATE:
                    DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                    Timber.i(progress.getMessage());
                    EventBus.getDefault().post(new DataSyncEvent(uid, progress));
                    break;
                case DownloadProgress.STATUS_ERROR:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
                    break;
                case DownloadProgress.STATUS_FINISHED_FORM:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_END));
                    break;
            }
        });
        XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);

    }

    @Override
    public void fetchProjectContacts() {
        int uid = Constant.DownloadUID.PROJECT_CONTACTS;
        ToastUtils.showShortToastInMiddle("Starting fetchProjectContacts");
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
