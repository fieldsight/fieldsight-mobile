package org.odk.collect.naxa.substages;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.odk.collect.naxa.stages.data.SubStage;
import org.odk.collect.naxa.substages.data.SubStageRepository;

import java.util.List;

public class SubStageViewModel extends ViewModel {

    private final SubStageRepository repository;


    public SubStageViewModel(SubStageRepository repository) {
        this.repository = repository;
    }


    public LiveData<List<SubStage>> loadSubStages(boolean forceUpdate, String id, String project, String stageId) {
        if (forceUpdate) {

        }

        boolean isDeployedFromProject = true;

        return repository.getById(forceUpdate, id);
    }

}
