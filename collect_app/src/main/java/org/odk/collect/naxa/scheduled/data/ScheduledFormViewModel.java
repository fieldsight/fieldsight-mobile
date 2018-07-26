package org.odk.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class ScheduledFormViewModel extends ViewModel {
    private final ScheduledFormRepository repository;

    public ScheduledFormViewModel(ScheduledFormRepository repository) {
        this.repository = repository;
    }


    public LiveData<List<ScheduleForm>> getBySiteId(Boolean forcedUpdate,String id, boolean isProject) {
        return repository.getBySiteId(forcedUpdate,id, isProject);
    }

    public LiveData<List<ScheduleForm>> getAll(boolean forcedUpdate) {
        return repository.getAll();
    }


}
