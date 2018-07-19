package org.odk.collect.naxa.generalforms;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.generalforms.db.GeneralFormRepository;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.onboarding.XMLForm;
import org.odk.collect.naxa.onboarding.XMLFormBuilder;
import org.odk.collect.naxa.project.db.ProjectRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class GeneralFormRemoteSource implements BaseLocalDataSource<GeneralForm> {

    private static GeneralFormRemoteSource INSTANCE;

    public static GeneralFormRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralFormRemoteSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<GeneralForm>> getById(@NonNull String id) {
        return null;
    }

    @Override
    public LiveData<List<GeneralForm>> getAll() {
        return null;
    }

    @Override
    public void save(GeneralForm... items) {

    }

    @Override
    public void save(ArrayList<GeneralForm> items) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void updateAll() {
        new ProjectRepository(Collect.getInstance()).getAllProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<GeneralForm>>>) this::downloadGeneralForm)
                .flatMap(generalForms -> {
                    GeneralFormLocalSource.getInstance().save(generalForms);
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

}
