package org.bcss.collect.naxa.substages;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.previoussubmission.model.SubStageAndSubmission;
import org.bcss.collect.naxa.substages.data.SubStageRepository;

import java.util.List;

public class SubStageViewModel extends ViewModel {

    private final SubStageRepository repository;

    public SubStageViewModel(SubStageRepository repository) {
        this.repository = repository;
    }

    public MediatorLiveData<List<SubStageAndSubmission>> loadSubStages(String id, String project, String stageId, String siteTypeId) {

        return repository.getByStageId(stageId, siteTypeId);
    }

}
