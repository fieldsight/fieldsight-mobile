package org.fieldsight.naxa.survey;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SurveyFormViewModel extends ViewModel {
    private final SurveyFormRepository repository;


    public SurveyFormViewModel(SurveyFormRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<SurveyForm>> getByProjectId(String projectId) {
        return repository.getByProjectId(projectId);
    }
}
