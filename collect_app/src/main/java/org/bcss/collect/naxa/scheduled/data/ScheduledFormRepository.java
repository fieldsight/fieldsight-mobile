package org.bcss.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.BaseRepository;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;

import java.util.ArrayList;
import java.util.List;

public class ScheduledFormRepository implements BaseRepository<ScheduleForm> {


    private static ScheduledFormRepository INSTANCE = null;
    private final ScheduledFormsLocalSource localSource;
    private final ScheduledFormsRemoteSource remoteSource;

    private MediatorLiveData<List<ScheduleForm>> mediatorLiveData = new MediatorLiveData<>();


    public static ScheduledFormRepository getInstance(ScheduledFormsLocalSource localSource, ScheduledFormsRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (ScheduledFormRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ScheduledFormRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }

    private ScheduledFormRepository(ScheduledFormsLocalSource localSource, ScheduledFormsRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }


    @Override
    public LiveData<List<ScheduleForm>> getAll(boolean forceUpdate) {
        if (forceUpdate) {
            remoteSource.getAll();
        }

        return localSource.getAll();
    }

    @Override
    public void save(ScheduleForm... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<ScheduleForm> items) {
        localSource.save();
    }

    @Override
    public void updateAll(ArrayList<ScheduleForm> items) {
        localSource.updateAll(items);
    }

    public LiveData<List<ScheduleForm>> getBySiteId(boolean forceUpdate, String siteId,String projectId) {
//        MediatorLiveData<List<ScheduleForm>> mediatorLiveData = new MediatorLiveData<>();
//        LiveData<List<ScheduleForm>> forms = localSource.getById(forceUpdate, id);
//
//        mediatorLiveData.addSource(forms, new Observer<List<ScheduleForm>>() {
//            @Override
//            public void onChanged(@Nullable List<ScheduleForm> scheduleForms) {
//                if (scheduleForms == null || scheduleForms.isEmpty()) {
//                    remoteSource.getAll();
//                } else {
//                    mediatorLiveData.removeSource(forms);
//                    mediatorLiveData.setValue(scheduleForms);
//                }
//            }
//        });
//
//        return mediatorLiveData;

        if (forceUpdate) {
            remoteSource.getAll();
        }

        return localSource.getBySiteId(siteId,projectId);
    }

    public LiveData<List<ScheduleForm>> getByProjectId(boolean forcedUpdate, String project) {
        if (forcedUpdate) {
            remoteSource.getAll();
        }

        return localSource.getByProjectId(project);
    }

}
