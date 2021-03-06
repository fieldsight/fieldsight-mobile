package org.fieldsight.naxa.scheduled.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.previoussubmission.model.ScheduledFormAndSubmission;

import java.util.List;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class ScheduledFormViewModel extends ViewModel {
    private final ScheduledFormRepository repository;

    public ScheduledFormViewModel(ScheduledFormRepository repository) {
        this.repository = repository;
    }

    @Deprecated
    public LiveData<List<ScheduleForm>> getForms(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteId(forcedUpdate, loadedSite.getId(), loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getByProjectId(forcedUpdate, loadedSite.getProject());

        }
    }


    public LiveData<List<ScheduledFormAndSubmission>> getForms(Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getFormsBySiteId(loadedSite.getId(), loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getFormsByProjectId(loadedSite.getProject());

        }
    }
}
