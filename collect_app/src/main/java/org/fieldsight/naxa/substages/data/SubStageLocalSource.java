package org.fieldsight.naxa.substages.data;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import android.text.TextUtils;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.previoussubmission.LastSubmissionLocalSource;
import org.fieldsight.naxa.previoussubmission.model.SubStageAndSubmission;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;
import org.fieldsight.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class SubStageLocalSource implements BaseLocalDataSource<SubStage> {

    private static SubStageLocalSource INSTANCE;
    private final SubStageDAO dao;


    private SubStageLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSubStageDAO();
    }

    public static SubStageLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SubStageLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<SubStage>> getAll() {
        return dao.getAllSubStages();
    }

    public MediatorLiveData<List<SubStageAndSubmission>> getByStageId(String stageId, String siteTypeId) {
        MediatorLiveData<List<SubStageAndSubmission>> mediatorLiveData = new MediatorLiveData<>();
        LiveData<List<SubStage>> source = dao.getByStageId(stageId);


        mediatorLiveData.addSource(source, new Observer<List<SubStage>>() {
            @Override
            public void onChanged(@Nullable List<SubStage> subStages) {
                if (subStages != null) {

                    Observable.just(subStages)
                            .map(new Function<List<SubStage>, List<SubStage>>() {
                                @Override
                                public List<SubStage> apply(List<SubStage> subStages) {
                                    ArrayList<SubStage> filteredSubstages = new ArrayList<>();

                                    for (SubStage subStage : subStages) {
                                        if (TextUtils.isEmpty(siteTypeId)
                                                || Constant.DEFAULT_SITE_TYPE.equals(siteTypeId)
                                                || subStage.getTagIds().contains(siteTypeId)
                                                || subStage.getTagIds().size() == 0) {

                                            filteredSubstages.add(subStage);
                                        }
                                    }
                                    return filteredSubstages;
                                }
                            })
                            .flatMapIterable((Function<List<SubStage>, Iterable<SubStage>>) subStages1 -> subStages1)
                            .flatMap(new Function<SubStage, ObservableSource<SubStageAndSubmission>>() {
                                @Override
                                public ObservableSource<SubStageAndSubmission> apply(SubStage subStage) {
                                    Maybe<SubmissionDetail> submissionDetailsSource;

                                    if (SITE.equals(subStage.getSubStageDeployedFrom())) {
                                        submissionDetailsSource = LastSubmissionLocalSource.getInstance().getBySiteFsId(subStage.getFsFormId());
                                    } else {
                                        submissionDetailsSource = LastSubmissionLocalSource.getInstance().getByProjectFsId(subStage.getFsFormId());
                                    }


                                    return submissionDetailsSource.toObservable()
                                            .defaultIfEmpty(new SubmissionDetail())
                                            .map(new Function<SubmissionDetail, SubStageAndSubmission>() {
                                                @Override
                                                public SubStageAndSubmission apply(SubmissionDetail submissionDetail) {
                                                    SubStageAndSubmission subStageAndSubmission = new SubStageAndSubmission();
                                                    subStageAndSubmission.setSubStage(subStage);
                                                    subStageAndSubmission.setSubmissionDetail(submissionDetail);
                                                    return subStageAndSubmission;
                                                }
                                            });
                                }
                            })
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .toList()
                            .subscribe(new DisposableSingleObserver<List<SubStageAndSubmission>>() {
                                @Override
                                public void onSuccess(List<SubStageAndSubmission> subStageAndSubmissions) {
                                    mediatorLiveData.setValue(subStageAndSubmissions);
                                    mediatorLiveData.removeSource(source);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Timber.e(e);
                                    mediatorLiveData.removeSource(source);
                                }
                            });
                }
            }
        });

        return mediatorLiveData;
    }

    @Override
    public void save(SubStage... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<SubStage> items) {
        dao.insert(items);
    }

    @Override
    public void updateAll(ArrayList<SubStage> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    public Observable<List<SubStage>> getByStageIdMaybe(String id, String siteTypeId) {
        return dao.getByStageIdMaybe(id).toObservable()
                .flatMap(new Function<List<SubStage>, ObservableSource<List<SubStage>>>() {
                    @Override
                    public ObservableSource<List<SubStage>> apply(List<SubStage> subStages) {
                        ArrayList<SubStage> filteredSubstages = new ArrayList<>();

                        for (SubStage subStage : subStages) {

                            boolean siteTypeNotDefined = TextUtils.isEmpty(siteTypeId)
                                    || Constant.DEFAULT_SITE_TYPE.equals(siteTypeId);
                            boolean subStageTagNotDefined = subStage.getTagIds().size() == 0;
                            boolean siteTypeMatchesSubStageTag = subStage.getTagIds().contains(siteTypeId);

                            if (siteTypeNotDefined
                                    || subStageTagNotDefined
                                    || siteTypeMatchesSubStageTag) {

                                filteredSubstages.add(subStage);
                            }
                        }
                        return Observable.just(filteredSubstages);
                    }
                });
    }


    public void deleteAll(ArrayList<SubStage> subStageList) {
        dao.deleteAll(subStageList);
    }
}
