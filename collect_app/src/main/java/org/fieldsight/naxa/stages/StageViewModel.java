package org.fieldsight.naxa.stages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.stages.data.Stage;

import java.util.List;

import io.reactivex.Observable;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class StageViewModel extends ViewModel {

    private final StageFormRepository repository;


    public StageViewModel(StageFormRepository repository) {
        this.repository = repository;
    }


    public LiveData<List<Stage>> getForms(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteId(forcedUpdate, loadedSite.getId(), loadedSite.getTypeId(),loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getByProjectId(forcedUpdate, loadedSite.getProject(), loadedSite.getTypeId());

        }
    }

    public Observable<List<Stage>> getStages(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getStagedFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteIdMaybe(loadedSite.getId(),loadedSite.getTypeId(),loadedSite.getProject());
            case PROJECT:
            default:
                return repository.getByProjectIdMaybe(loadedSite.getProject(), loadedSite.getTypeId());

        }
    }


}


