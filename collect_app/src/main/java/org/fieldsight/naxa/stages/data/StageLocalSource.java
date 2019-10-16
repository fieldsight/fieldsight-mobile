package org.fieldsight.naxa.stages.data;

import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;


import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.previoussubmission.model.SubStageAndSubmission;
import org.fieldsight.naxa.substages.data.SubStageLocalSource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class StageLocalSource implements BaseLocalDataSource<Stage> {

    private static StageLocalSource stageLocalSource;
    private final StageFormDAO dao;

    private StageLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getStageDAO();
    }

    public synchronized static StageLocalSource getInstance() {
        if (stageLocalSource == null) {
            stageLocalSource = new StageLocalSource();
        }
        return stageLocalSource;
    }

    @Override
    public LiveData<List<Stage>> getAll() {
        return dao.getAllStages();
    }

    @Override
    public void save(Stage... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Stage> items) {
        dao.insert(items);
    }

    public void deleteAllByProjectId(String projectId) {
        dao.deleteAllById(projectId);
    }

    @Deprecated
    @Override
    public void updateAll(ArrayList<Stage> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    public LiveData<List<Stage>> getBySiteId(String siteId, String siteTypeId, String projectId) {
        MediatorLiveData<List<Stage>> mediatorLiveData = new MediatorLiveData<>();
        LiveData<List<Stage>> stagesliveData = dao.getBySiteId(siteId, projectId);

        mediatorLiveData.addSource(stagesliveData, new Observer<List<Stage>>() {
            @Override
            public void onChanged(@Nullable List<Stage> stages) {
                if (stages == null) {
                    return;
                }

                List<Stage> filteredStages = new ArrayList<>();
                for (Stage stage : stages) {
                    MediatorLiveData<List<SubStageAndSubmission>> substages = SubStageLocalSource.getInstance().getByStageId(stage.getId(), siteTypeId);
                    substages.observeForever(new Observer<List<SubStageAndSubmission>>() {
                        @Override
                        public void onChanged(@Nullable List<SubStageAndSubmission> subStageAndSubmissions) {
                            substages.removeObserver(this);
                            if (subStageAndSubmissions != null && subStageAndSubmissions.size() > 0) {
                                filteredStages.add(stage);
                            }
                        }
                    });
                }

                mediatorLiveData.removeSource(stagesliveData);
                mediatorLiveData.setValue(filteredStages);

            }
        });

        return mediatorLiveData;
    }


    public Observable<List<Stage>> getByProjectIdMaybe(String projectId, String siteTypeId) {
        return dao.getByProjectIdMaybe(projectId)
                .toObservable()
                .flatMapIterable((Function<List<Stage>, Iterable<Stage>>) stages -> stages)
                .flatMap(new Function<Stage, Observable<Stage>>() {
                    @Override
                    public Observable<Stage> apply(Stage stage) {
                        return SubStageLocalSource.getInstance()
                                .getByStageIdMaybe(stage.getId(), siteTypeId)
                                .filter(new Predicate<List<SubStage>>() {
                                    @Override
                                    public boolean test(List<SubStage> subStages) {

                                        return subStages.size() > 0;
                                    }
                                }).map(new Function<List<SubStage>, Stage>() {
                                    @Override
                                    public Stage apply(List<SubStage> subStages) {
                                        return stage;
                                    }
                                });

                    }
                })
                .toList()
                .toObservable();


    }

    public Observable<List<Stage>> getBySiteIdMaybe(String siteId, String siteTypeId, String projectId) {
        return dao.getBySiteIdMaybe(siteId, projectId)
                .toObservable()
                .flatMapIterable((Function<List<Stage>, Iterable<Stage>>) stages -> stages)
                .flatMap(new Function<Stage, Observable<Stage>>() {
                    @Override
                    public Observable<Stage> apply(Stage stage) {
                        return SubStageLocalSource.getInstance()
                                .getByStageIdMaybe(stage.getId(), siteTypeId)
                                .filter(new Predicate<List<SubStage>>() {
                                    @Override
                                    public boolean test(List<SubStage> subStages) {

                                        return subStages.size() > 0;
                                    }
                                }).map(new Function<List<SubStage>, Stage>() {
                                    @Override
                                    public Stage apply(List<SubStage> subStages) {
                                        return stage;
                                    }
                                });

                    }
                })
                .toList()
                .toObservable();


    }

    public LiveData<List<Stage>> getByProjectId(String projectId, String siteTypeId) {
        MediatorLiveData<List<Stage>> mediatorLiveData = new MediatorLiveData<>();
        LiveData<List<Stage>> stagesliveData = dao.getByProjectId(projectId);


        mediatorLiveData.addSource(stagesliveData, new Observer<List<Stage>>() {
            @Override
            public void onChanged(@Nullable List<Stage> stages) {
                if (stages == null) {
                    return;
                }

                List<Stage> filteredStages = new ArrayList<>();
                for (Stage stage : stages) {
                    MediatorLiveData<List<SubStageAndSubmission>> substages = SubStageLocalSource.getInstance().getByStageId(stage.getId(), siteTypeId);
                    substages.observeForever(new Observer<List<SubStageAndSubmission>>() {
                        @Override
                        public void onChanged(@Nullable List<SubStageAndSubmission> subStageAndSubmissions) {
                            substages.removeObserver(this);
                            if (subStageAndSubmissions != null && subStageAndSubmissions.size() > 0) {
                                filteredStages.add(stage);
                            }
                        }
                    });
                }
                mediatorLiveData.removeSource(stagesliveData);
                mediatorLiveData.setValue(filteredStages);
            }
        });

        return mediatorLiveData;
    }

    public void deleteAllBySiteId(String siteId) {
        dao.deleteAllBySiteId(siteId);
    }
}
