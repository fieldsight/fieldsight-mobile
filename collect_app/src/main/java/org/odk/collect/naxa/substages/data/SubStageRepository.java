package org.odk.collect.naxa.substages.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.stages.StageFormRepository;
import org.odk.collect.naxa.stages.data.StageRemoteSource;
import org.odk.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

public class SubStageRepository implements BaseLocalDataSource<SubStage> {


    private static SubStageRepository INSTANCE = null;
    private final SubStageLocalSource localSource;
    private final StageRemoteSource remoteSource;

    public static SubStageRepository getInstance(SubStageLocalSource localSource, StageRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (StageFormRepository.class) {
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
    public LiveData<List<SubStage>> getById(boolean forceUpdate, String id) {
        return localSource.getById(forceUpdate, id);
    }

    @Override
    public LiveData<List<SubStage>> getAll() {
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
}
