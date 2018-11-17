package org.bcss.collect.naxa.stages;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.bcss.collect.naxa.common.BaseLocalDataSource;


import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.stages.data.StageLocalSource;
import org.bcss.collect.naxa.stages.data.StageRemoteSource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

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

    public LiveData<List<Stage>> getBySiteId(boolean forceUpdate, String siteId, String siteIdType,String projectId) {
        if (forceUpdate) {
            remoteSource.getAll();
        }

        return localSource.getBySiteId(siteId, siteIdType,projectId);
    }


    public LiveData<List<Stage>> getByProjectId(boolean forcedUpdate, String projectId, String siteTypeId) {
        if (forcedUpdate) {
            remoteSource.getAll();
        }

        return localSource.getByProjectId(projectId, siteTypeId);
    }

    public Observable<List<Stage>> getByProjectIdMaybe(String projectId, String siteTypeId) {
        return localSource.getByProjectIdMaybe(projectId, siteTypeId);
    }


    public Observable<List<Stage>> getBySiteIdMaybe(String siteId, String siteIdType, String projectId){
        return localSource.getBySiteIdMaybe(siteId,siteIdType,projectId);
    }


}
