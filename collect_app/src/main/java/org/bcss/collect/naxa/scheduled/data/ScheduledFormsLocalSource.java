package org.bcss.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.previoussubmission.LastSubmissionLocalSource;
import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.SITE;

public class ScheduledFormsLocalSource implements BaseLocalDataSource<ScheduleForm> {

    private static ScheduledFormsLocalSource INSTANCE;
    private ScheduledFormDAO dao;

    private ScheduledFormsLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectScheduledFormsDAO();
    }

    public static ScheduledFormsLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScheduledFormsLocalSource();
        }
        return INSTANCE;


    }


    @Override
    public LiveData<List<ScheduleForm>> getAll() {
        return dao.getAll();
    }

    public List<ScheduleForm> getDailyForms() {
        return dao.getDailyForms();
    }


    @Override
    public void save(ScheduleForm... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<ScheduleForm> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<ScheduleForm> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    @Deprecated
    public LiveData<List<ScheduleForm>> getBySiteId(String siteId, String projectId) {
        return dao.getBySiteId(siteId, projectId);
    }

    @Deprecated
    public LiveData<List<ScheduleForm>> getByProjectId(String projectId) {
        return dao.getByProjectId(projectId);
    }

    public LiveData<List<ScheduleForm>> getById(String fsFormId) {
        return dao.getById(fsFormId);
    }


    public LiveData<List<ScheduledFormAndSubmission>> getFormsBySiteId(@NonNull String siteId, @NonNull String projectId) {
        MediatorLiveData<List<ScheduledFormAndSubmission>> generalFormMediator = new MediatorLiveData<>();
        LiveData<List<ScheduleForm>> source = dao.getBySiteId(siteId, projectId);

        generalFormMediator.addSource(source,
                new android.arch.lifecycle.Observer<List<ScheduleForm>>() {
                    @Override
                    public void onChanged(@Nullable List<ScheduleForm> generalForms) {
                        if (generalForms != null) {

                            Observable.just(generalForms)
                                    .flatMapIterable((Function<List<ScheduleForm>, Iterable<ScheduleForm>>) generalForms1 -> generalForms1)
                                    .flatMap(new Function<ScheduleForm, Observable<ScheduledFormAndSubmission>>() {
                                        @Override
                                        public Observable<ScheduledFormAndSubmission> apply(ScheduleForm generalForm) {
                                            Maybe<SubmissionDetail> submissionDetailsSource;

                                            if (SITE.equals(generalForm.getFormDeployedFrom())) {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getBySiteFsId(generalForm.getFsFormId());
                                            } else {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getByProjectFsId(generalForm.getFsFormId());
                                            }
                                            return submissionDetailsSource.toObservable()
                                                    .defaultIfEmpty(new SubmissionDetail())
                                                    .map(new Function<SubmissionDetail, ScheduledFormAndSubmission>() {
                                                        @Override
                                                        public ScheduledFormAndSubmission apply(SubmissionDetail submissionDetail) {
                                                            ScheduledFormAndSubmission generalFormAndSubmission = new ScheduledFormAndSubmission();
                                                            generalFormAndSubmission.setScheduleForm(generalForm);
                                                            generalFormAndSubmission.setSubmissionDetail(submissionDetail);
                                                            return generalFormAndSubmission;
                                                        }
                                                    });
                                        }
                                    })
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .toList()
                                    .subscribe(new SingleObserver<List<ScheduledFormAndSubmission>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(List<ScheduledFormAndSubmission> generalFormAndSubmissions) {
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

        //        return dao.getSiteScheduleFormAndSubmission(siteId, projectId);
    }

    public LiveData<List<ScheduledFormAndSubmission>> getFormsByProjectId(@NonNull String projectId) {
        MediatorLiveData<List<ScheduledFormAndSubmission>> generalFormMediator = new MediatorLiveData<>();
        LiveData<List<ScheduleForm>> source = dao.getByProjectId(projectId);

        generalFormMediator.addSource(source,
                new android.arch.lifecycle.Observer<List<ScheduleForm>>() {
                    @Override
                    public void onChanged(@Nullable List<ScheduleForm> generalForms) {
                        if (generalForms != null) {

                            Observable.just(generalForms)
                                    .flatMapIterable((Function<List<ScheduleForm>, Iterable<ScheduleForm>>) generalForms1 -> generalForms1)
                                    .flatMap(new Function<ScheduleForm, Observable<ScheduledFormAndSubmission>>() {
                                        @Override
                                        public Observable<ScheduledFormAndSubmission> apply(ScheduleForm generalForm) {
                                            Maybe<SubmissionDetail> submissionDetailsSource;

                                            if (SITE.equals(generalForm.getFormDeployedFrom())) {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getBySiteFsId(generalForm.getFsFormId());
                                            } else {
                                                submissionDetailsSource = LastSubmissionLocalSource.getInstance().getByProjectFsId(generalForm.getFsFormId());
                                            }
                                            return submissionDetailsSource.toObservable()
                                                    .defaultIfEmpty(new SubmissionDetail())
                                                    .map(new Function<SubmissionDetail, ScheduledFormAndSubmission>() {
                                                        @Override
                                                        public ScheduledFormAndSubmission apply(SubmissionDetail submissionDetail) {
                                                            ScheduledFormAndSubmission generalFormAndSubmission = new ScheduledFormAndSubmission();
                                                            generalFormAndSubmission.setScheduleForm(generalForm);
                                                            generalFormAndSubmission.setSubmissionDetail(submissionDetail);
                                                            return generalFormAndSubmission;
                                                        }
                                                    });
                                        }
                                    })
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .toList()
                                    .subscribe(new SingleObserver<List<ScheduledFormAndSubmission>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(List<ScheduledFormAndSubmission> generalFormAndSubmissions) {
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

//        return dao.getProjectScheduleFormAndSubmission(projectId);
    }

}
