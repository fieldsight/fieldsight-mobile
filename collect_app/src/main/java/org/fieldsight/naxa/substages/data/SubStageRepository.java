package org.fieldsight.naxa.substages.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.previoussubmission.model.SubStageAndSubmission;
import org.fieldsight.naxa.stages.data.StageRemoteSource;
import org.fieldsight.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

public class SubStageRepository implements BaseLocalDataSource<SubStage> {


    private static SubStageRepository INSTANCE;
    private final SubStageLocalSource localSource;

    public static SubStageRepository getInstance(SubStageLocalSource localSource) {
        if (INSTANCE == null) {
            synchronized (SubStageRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SubStageRepository(localSource);
                }
            }
        }
        return INSTANCE;
    }


    private SubStageRepository(@NonNull SubStageLocalSource localSource) {
        this.localSource = localSource;

    }


    @Override
    public LiveData<List<SubStage>> getAll( ) {
        return localSource.getAll();
    }

    @Override
    public void save(SubStage... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<SubStage> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<SubStage> items) {
        localSource.updateAll(items);
    }

    public MediatorLiveData<List<SubStageAndSubmission>> getByStageId(String stageId, String siteTypeId) {
        return localSource.getByStageId(stageId,siteTypeId);
    }
}
