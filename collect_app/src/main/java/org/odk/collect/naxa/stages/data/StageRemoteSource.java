package org.odk.collect.naxa.stages.data;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseRemoteDataSource;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.onboarding.XMLForm;
import org.odk.collect.naxa.onboarding.XMLFormBuilder;
import org.odk.collect.naxa.project.db.ProjectRepository;
import org.odk.collect.naxa.substages.data.SubStageLocalSource;

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

public class StageRemoteSource implements BaseRemoteDataSource<Stage> {

    private static StageRemoteSource INSTANCE;

    public static StageRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StageRemoteSource();
        }
        return INSTANCE;
    }


    @Override
    public void getAll() {
        new ProjectRepository().getAllProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<Stage>>>) this::downloadProjectStages)
                .map(stages -> {
                    StageLocalSource.getInstance().updateAll(stages);
                    return stages;
                })
                .flatMapIterable((Function<ArrayList<Stage>, Iterable<Stage>>) stages -> stages)
                .map(stage -> stage.getSubStage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<SubStage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<SubStage> subStages) {
                        SubStageLocalSource.getInstance().updateAll(subStages);
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
    public void getById(Stage... items) {

    }

    private Observable<ArrayList<Stage>> downloadProjectStages(XMLForm xmlForm) {

        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();

        return ServiceGenerator
                .getRxClient()
                .create(ApiInterface.class)
                .getStageSubStage(createdFromProject, creatorsId)
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
