package org.fieldsight.naxa.generalforms.data;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.previoussubmission.LastSubmissionLocalSource;
import org.fieldsight.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class GeneralFormLocalSource implements BaseLocalDataSource<GeneralForm> {

    private static GeneralFormLocalSource generalFormLocalSource;
    private final GeneralFormDAO dao;


    private GeneralFormLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectGeneralFormDao();
    }


    public static synchronized GeneralFormLocalSource getInstance() {
        if (generalFormLocalSource == null) {
            generalFormLocalSource = new GeneralFormLocalSource();
        }
        return generalFormLocalSource;
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
                new Observer<List<GeneralForm>>() {
                    @Override
                    public void onChanged(@Nullable List<GeneralForm> generalForms) {
                        if (generalForms != null) {

                            Observable.just(generalForms)
                                    .flatMapIterable((Function<List<GeneralForm>, Iterable<GeneralForm>>) generalForms1 -> generalForms1)
                                    .flatMap(new Function<GeneralForm, Observable<GeneralFormAndSubmission>>() {
                                        @Override
                                        public Observable<GeneralFormAndSubmission> apply(GeneralForm generalForm) {
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
                                                        public GeneralFormAndSubmission apply(SubmissionDetail submissionDetail) {
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
                                            Timber.e(e);
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
                new Observer<List<GeneralForm>>() {
                    @Override
                    public void onChanged(@Nullable List<GeneralForm> generalForms) {
                        if (generalForms != null) {
                            Observable.just(generalForms)
                                    .flatMapIterable((Function<List<GeneralForm>, Iterable<GeneralForm>>) generalForms1 -> generalForms1)
                                    .flatMap(new Function<GeneralForm, Observable<GeneralFormAndSubmission>>() {
                                        @Override
                                        public Observable<GeneralFormAndSubmission> apply(GeneralForm generalForm) {
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
                                                        public GeneralFormAndSubmission apply(SubmissionDetail submissionDetail) {
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

        dao.insert(items);

    }

    @Override
    public void updateAll(ArrayList<GeneralForm> items) {

        AsyncTask.execute(() -> dao.updateAll(items));

    }

    public LiveData<List<GeneralForm>> getById(String fsFormId) {
        return dao.getById(fsFormId);
    }


    public Integer getSiteFormCount(String siteId) {
        return dao.getSiteFormCount(siteId);
    }
}
