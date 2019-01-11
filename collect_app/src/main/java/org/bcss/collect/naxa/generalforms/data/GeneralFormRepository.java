package org.bcss.collect.naxa.generalforms.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.bcss.collect.naxa.common.BaseRepository;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;

import java.util.ArrayList;
import java.util.List;

public class GeneralFormRepository implements BaseRepository<GeneralForm> {

    private static GeneralFormRepository INSTANCE = null;
    private final GeneralFormLocalSource localSource;
    private final GeneralFormRemoteSource remoteSource;

    public static GeneralFormRepository getInstance(GeneralFormLocalSource localSource, GeneralFormRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (GeneralFormRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GeneralFormRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private GeneralFormRepository(@NonNull GeneralFormLocalSource localSource, @NonNull GeneralFormRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }


    @Deprecated
    public LiveData<List<GeneralForm>> getBySiteId(boolean forcedUpdate, @NonNull String siteId, String projectId) {
        if (forcedUpdate) {
            remoteSource.getAll();
        }

        return localSource.getBySiteId(siteId, projectId);
    }


    @Deprecated
    public LiveData<List<GeneralForm>> getByProjectId(boolean forcedUpdate, String project) {
        if (forcedUpdate) {
            remoteSource.getAll();
        }

        return localSource.getByProjectId(project);
    }

    public LiveData<List<GeneralFormAndSubmission>> getFormsBySiteId(@NonNull String siteId,@NonNull String projectId) {
        return localSource.getFormsBySiteId(siteId,projectId);
    }

    public LiveData<List<GeneralFormAndSubmission>> getFormsByProjectIdId(@NonNull String projectId) {
        return localSource.getFormsByProjectId(projectId);
    }


    @Override
    public LiveData<List<GeneralForm>> getAll(boolean forceUpdate) {
        if (forceUpdate) {
            remoteSource.getAll();
        }

        return localSource.getAll();
    }


    @Override
    public void save(GeneralForm... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<GeneralForm> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<GeneralForm> items) {
        localSource.updateAll(items);
    }


    public void deleteAll() {
        localSource.deleteAll();
    }


}
