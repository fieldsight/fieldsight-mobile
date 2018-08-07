package org.bcss.collect.naxa.substages.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.stages.data.StageRemoteSource;
import org.bcss.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

public class SubStageRepository implements BaseLocalDataSource<SubStage> {


    private static SubStageRepository INSTANCE = null;
    private final SubStageLocalSource localSource;
    private final StageRemoteSource remoteSource;

    public static SubStageRepository getInstance(SubStageLocalSource localSource, StageRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (SubStageRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SubStageRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private SubStageRepository(@NonNull SubStageLocalSource localSource, @NonNull StageRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
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

    public LiveData<List<SubStage>> getBySiteId(boolean forceUpdate, String stageId) {
        return localSource.getAll();
    }
}
