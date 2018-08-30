package org.bcss.collect.naxa.stages.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.substages.data.SubStageLocalSource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class StageLocalSource implements BaseLocalDataSource<Stage> {

    private static StageLocalSource INSTANCE;
    private StageFormDAO dao;

    private StageLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getStageDAO();
    }


    public static StageLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StageLocalSource();
        }
        return INSTANCE;
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
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Stage> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    public LiveData<List<Stage>> getBySiteId(String siteId, String siteTypeId) {
        MediatorLiveData<List<Stage>> mediatorLiveData = new MediatorLiveData<>();
        LiveData<List<Stage>> stagesliveData = dao.getBySiteId(siteId);

        mediatorLiveData.addSource(stagesliveData, new Observer<List<Stage>>() {
            @Override
            public void onChanged(@Nullable List<Stage> stages) {
                if (stages == null) {
                    return;
                }

                List<Stage> filteredStages = new ArrayList<>();
                for (Stage stage : stages) {
                    LiveData<List<SubStage>> substages = SubStageLocalSource.getInstance().getByStageId(stage.getId(), siteTypeId);
                    substages.observeForever(new Observer<List<SubStage>>() {
                        @Override
                        public void onChanged(@Nullable List<SubStage> subStages) {
                            substages.removeObserver(this);
                            if (subStages != null && subStages.size() > 0) {
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


    public LiveData<List<Stage>> getByProjectId(String siteId) {
        return dao.getByProjectId(siteId);
    }
}
