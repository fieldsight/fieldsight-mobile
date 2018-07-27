package org.odk.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class ScheduledFormViewModel extends ViewModel {
    private final ScheduledFormRepository repository;

    public ScheduledFormViewModel(ScheduledFormRepository repository) {
        this.repository = repository;
    }


    public LiveData<List<ScheduleForm>> getBySiteId(Boolean forcedUpdate,String id, String formDeployedFrom) {
        return repository.getBySiteId(forcedUpdate,id, formDeployedFrom);
    }



}
