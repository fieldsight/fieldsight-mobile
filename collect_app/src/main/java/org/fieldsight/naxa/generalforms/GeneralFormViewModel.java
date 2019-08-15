package org.fieldsight.naxa.generalforms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.generalforms.data.GeneralFormRepository;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.previoussubmission.model.GeneralFormAndSubmission;

import java.util.List;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class GeneralFormViewModel extends ViewModel {
    private final GeneralFormRepository repository;


    public GeneralFormViewModel(GeneralFormRepository repository) {
        this.repository = repository;
    }




    @Deprecated
    public LiveData<List<GeneralForm>> getForms(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteId(forcedUpdate, loadedSite.getId(), loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getByProjectId(forcedUpdate, loadedSite.getProject());

        }
    }

    public LiveData<List<GeneralFormAndSubmission>> getFormsAndSubmission(Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getFormsBySiteId(loadedSite.getId(),loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getFormsByProjectIdId(loadedSite.getProject());

        }
    }
}
