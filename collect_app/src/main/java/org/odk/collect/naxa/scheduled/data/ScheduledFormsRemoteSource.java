package org.odk.collect.naxa.scheduled.data;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseRemoteDataSource;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ScheduledFormsRemoteSource implements BaseRemoteDataSource<ScheduleForm> {

    private static ScheduledFormsRemoteSource INSTANCE;


    public static ScheduledFormsRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScheduledFormsRemoteSource();
        }
        return INSTANCE;
    }


    @Override
    public void getAll() {
        new ProjectRepository(Collect.getInstance()).getAllProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .flatMap(this::downloadProjectSchedule)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void getById(ScheduleForm... items) {

    }


    private Observable<Object> downloadProjectSchedule(XMLForm xmlForm) {

        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getScheduleForms(createdFromProject, creatorsId)
                .flatMap(new Function<ArrayList<ScheduleForm>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(ArrayList<ScheduleForm> scheduleForms) throws Exception {
                        ScheduledFormsLocalSource.getInstance().save(scheduleForms);
                        return Observable.empty();
                    }
                }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
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
                });

    }
}
