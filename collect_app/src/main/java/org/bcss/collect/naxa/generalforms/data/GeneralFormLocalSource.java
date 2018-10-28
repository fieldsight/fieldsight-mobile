package org.bcss.collect.naxa.generalforms.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.previoussubmission.LastSubmissionLocalSource;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.SITE;

public class GeneralFormLocalSource implements BaseLocalDataSource<GeneralForm> {

    private static GeneralFormLocalSource INSTANCE;
    private GeneralFormDAO dao;


    private GeneralFormLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectGeneralFormDao();
    }


    public static GeneralFormLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralFormLocalSource();
        }
        return INSTANCE;
    }


    @Deprecated
    public LiveData<List<GeneralForm>> getBySiteId(@NonNull String siteId, String projectId) {
        return dao.getSiteGeneralForms(siteId, projectId);
    }

    @Deprecated
    public LiveData<List<GeneralForm>> getByProjectId(String projectId) {
        return dao.getProjectGeneralForms(projectId);
    }

    public LiveData<List<GeneralFormAndSubmission>> getFormsBySiteId(@NonNull String siteId, @NonNull String projectId) {

        MediatorLiveData<List<GeneralFormAndSubmission>> generalFormMediator = new MediatorLiveData<>();
        LiveData<List<GeneralForm>> source = dao.getSiteGeneralForms(siteId, projectId);

        generalFormMediator.addSource(source,
                new android.arch.lifecycle.Observer<List<GeneralForm>>() {
                    @Override
                    public void onChanged(@Nullable List<GeneralForm> generalForms) {
                        if (generalForms != null) {

                            Observable.just(generalForms)
                                    .flatMapIterable((Function<List<GeneralForm>, Iterable<GeneralForm>>) generalForms1 -> generalForms1)
                                    .flatMap(new Function<GeneralForm, Observable<GeneralFormAndSubmission>>() {
                                        @Override
                                        public Observable<GeneralFormAndSubmission> apply(GeneralForm generalForm) throws Exception {
                                            Maybe<SubmissionDetail> submissionDetailsSource;

                                            if (SITE.equals(generalForm.getFormDeployedFrom())) {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getBySiteFsId(generalForm.getFsFormId());
                                            } else {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getByProjectFsId(generalForm.getFsFormId());
                                            }
                                            return submissionDetailsSource.toObservable()
                                                    .defaultIfEmpty(new SubmissionDetail())
                                                    .map(new Function<SubmissionDetail, GeneralFormAndSubmission>() {
                                                        @Override
                                                        public GeneralFormAndSubmission apply(SubmissionDetail submissionDetail) throws Exception {
                                                            GeneralFormAndSubmission generalFormAndSubmission = new GeneralFormAndSubmission();
                                                            generalFormAndSubmission.setGeneralForm(generalForm);
                                                            generalFormAndSubmission.setSubmissionDetail(submissionDetail);
                                                            return generalFormAndSubmission;
                                                        }
                                                    });
                                        }
                                    })
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .toList()
                                    .subscribe(new SingleObserver<List<GeneralFormAndSubmission>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(List<GeneralFormAndSubmission> generalFormAndSubmissions) {
                                            generalFormMediator.setValue(generalFormAndSubmissions);
                                            generalFormMediator.removeSource(source);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            e.printStackTrace();
                                            generalFormMediator.removeSource(source);
                                        }
                                    });
                        }
                    }
                });

        return generalFormMediator;
//        return dao.getSiteGeneralFormAndSubmission(siteId, projectId);
    }

    public LiveData<List<GeneralFormAndSubmission>> getFormsByProjectId(@NonNull String projectId) {

        MediatorLiveData<List<GeneralFormAndSubmission>> generalFormMediator = new MediatorLiveData<>();
        LiveData<List<GeneralForm>> source = dao.getProjectGeneralForms(projectId);

        generalFormMediator.addSource(source,
                new android.arch.lifecycle.Observer<List<GeneralForm>>() {
                    @Override
                    public void onChanged(@Nullable List<GeneralForm> generalForms) {
                        if (generalForms != null) {
                            Observable.just(generalForms)
                                    .flatMapIterable((Function<List<GeneralForm>, Iterable<GeneralForm>>) generalForms1 -> generalForms1)
                                    .flatMap(new Function<GeneralForm, Observable<GeneralFormAndSubmission>>() {
                                        @Override
                                        public Observable<GeneralFormAndSubmission> apply(GeneralForm generalForm) throws Exception {
                                            Maybe<SubmissionDetail> submissionDetailsSource;

                                            if (SITE.equals(generalForm.getFormDeployedFrom())) {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getBySiteFsId(generalForm.getFsFormId());
                                            } else {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getByProjectFsId(generalForm.getFsFormId());
                                            }

                                            return submissionDetailsSource.toObservable()
                                                    .defaultIfEmpty(new SubmissionDetail())
                                                    .map(new Function<SubmissionDetail, GeneralFormAndSubmission>() {
                                                        @Override
                                                        public GeneralFormAndSubmission apply(SubmissionDetail submissionDetail) throws Exception {
                                                            GeneralFormAndSubmission generalFormAndSubmission = new GeneralFormAndSubmission();
                                                            generalFormAndSubmission.setGeneralForm(generalForm);
                                                            generalFormAndSubmission.setSubmissionDetail(submissionDetail);
                                                            return generalFormAndSubmission;
                                                        }
                                                    });
                                        }
                                    })
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .toList()
                                    .subscribe(new SingleObserver<List<GeneralFormAndSubmission>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(List<GeneralFormAndSubmission> generalFormAndSubmissions) {
                                            generalFormMediator.setValue(generalFormAndSubmissions);
                                            generalFormMediator.removeSource(source);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            generalFormMediator.removeSource(source);
                                        }
                                    });
                        }
                    }
                });

        return generalFormMediator;

        //        return dao.getProjectGeneralFormAndSubmission(projectId);
    }

    @Override
    public LiveData<List<GeneralForm>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(GeneralForm... items) {
        io.reactivex.Observable.just(items)
                .flatMap(generalForms -> {
                    dao.insert(generalForms);
                    return io.reactivex.Observable.empty();
                })
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
    public void save(ArrayList<GeneralForm> items) {
        io.reactivex.Observable.just(items)
                .flatMap(generalForms -> {
                    dao.insert(generalForms);
                    return io.reactivex.Observable.empty();
                })
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
    public void updateAll(ArrayList<GeneralForm> items) {

        AsyncTask.execute(() -> dao.updateAll(items));

    }

    private <T> Observer applySubscriber() {
        return new DisposableObserver() {

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }

        };
    }

    private <T> ObservableTransformer applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> SingleTransformer applySchedulersSingle() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteAll() {
        AsyncTask.execute(() -> dao.deleteAll());
    }


    public LiveData<List<GeneralForm>> getById(String fsFormId) {
        return dao.getById(fsFormId);
    }

    public void updateLastSubmission(SubmissionDetail formResponse) {

    }


}
