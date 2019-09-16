package org.fieldsight.naxa.substages;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.previoussubmission.model.SubStageAndSubmission;
import org.fieldsight.naxa.substages.data.SubStageRepository;

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
