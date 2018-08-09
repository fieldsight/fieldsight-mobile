package org.bcss.collect.naxa.survey;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class SurveyFormViewModel extends ViewModel {
    private SurveyFormRepository repository;


    public SurveyFormViewModel(SurveyFormRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<SurveyForm>> getByProjectId(String projectId) {
        return repository.getByProjectId(projectId);
    }
}
