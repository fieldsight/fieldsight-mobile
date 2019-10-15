package org.fieldsight.naxa.scheduled.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseRepository;
import org.fieldsight.naxa.previoussubmission.model.ScheduledFormAndSubmission;

import java.util.ArrayList;
import java.util.List;

public class ScheduledFormRepository implements BaseRepository<ScheduleForm> {


    private static ScheduledFormRepository scheduledFormRepository;
    private final ScheduledFormsLocalSource localSource;
    private final ScheduledFormsRemoteSource remoteSource;



    public static ScheduledFormRepository getInstance(ScheduledFormsLocalSource localSource, ScheduledFormsRemoteSource remoteSource) {
        if (scheduledFormRepository == null) {
            synchronized (ScheduledFormRepository.class) {
                if (scheduledFormRepository == null) {
                    scheduledFormRepository = new ScheduledFormRepository(localSource, remoteSource);
                }
            }
        }
        return scheduledFormRepository;
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

    @Deprecated
    public LiveData<List<ScheduleForm>> getBySiteId(boolean forceUpdate, String siteId, String projectId) {
//        MediatorLiveData<List<ScheduleForm>> mediatorLiveData = new MediatorLiveData<>();
//        LiveData<List<ScheduleForm>> FORMS = localSource.getById(forceUpdate, id);
//
//        mediatorLiveData.addSource(FORMS, new Observer<List<ScheduleForm>>() {
//            @Override
//            public void onChanged(@Nullable List<ScheduleForm> scheduleForms) {
//                if (scheduleForms == null || scheduleForms.isEmpty()) {
//                    remoteSource.getAll();
//                } else {
//                    mediatorLiveData.removeSource(FORMS);
//                    mediatorLiveData.setValue(scheduleForms);
//                }
//            }
//        });
//
//        return mediatorLiveData;

        if (forceUpdate) {
            remoteSource.getAll();
        }

        return localSource.getBySiteId(siteId, projectId);
    }

    @Deprecated
    public LiveData<List<ScheduleForm>> getByProjectId(boolean forcedUpdate, String project) {
        if (forcedUpdate) {
            remoteSource.getAll();
        }

        return localSource.getByProjectId(project);
    }

    public LiveData<List<ScheduledFormAndSubmission>> getFormsByProjectId(@NonNull String project) {
        return localSource.getFormsByProjectId(project);
    }

    public LiveData<List<ScheduledFormAndSubmission>> getFormsBySiteId(@NonNull String siteId, @NonNull String projectId) {
        return localSource.getFormsBySiteId(siteId, projectId);
    }

}
