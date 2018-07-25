package org.odk.collect.naxa.stages;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.common.BaseLocalDataSource;


import org.odk.collect.naxa.stages.data.Stage;
import org.odk.collect.naxa.stages.data.StageLocalSource;
import org.odk.collect.naxa.stages.data.StageRemoteSource;

import java.util.ArrayList;
import java.util.List;

public class StageFormRepository implements BaseLocalDataSource<Stage> {

    private static StageFormRepository INSTANCE = null;
    private final StageLocalSource localSource;
    private final StageRemoteSource remoteSource;

    public static StageFormRepository getInstance(StageLocalSource localSource, StageRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (StageFormRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StageFormRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private StageFormRepository(@NonNull StageLocalSource localSource, @NonNull StageRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    @Override
    public LiveData<List<Stage>> getById(boolean forceUpdate, String id) {
        return null;
    }

    @Override
    public LiveData<List<Stage>> getAll() {
        remoteSource.getAll();
        return localSource.getAll();
    }

    @Override
    public void save(Stage... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<Stage> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<Stage> items) {
        localSource.updateAll(items);
    }
}
