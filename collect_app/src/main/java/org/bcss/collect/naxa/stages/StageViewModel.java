package org.bcss.collect.naxa.stages;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.stages.data.Stage;

import java.util.List;

import io.reactivex.Observable;

import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.SITE;

public class StageViewModel extends ViewModel {

    private final StageFormRepository repository;


    public StageViewModel(StageFormRepository repository) {
        this.repository = repository;
    }


    public LiveData<List<Stage>> getForms(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteId(forcedUpdate, loadedSite.getId(), loadedSite.getTypeId());
            case PROJECT:
            default:
                return repository.getByProjectId(forcedUpdate, loadedSite.getProject(), loadedSite.getTypeId());

        }
    }

    public Observable<List<Stage>> getStages(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
            case PROJECT:
            default:
                return repository.getByProjectIdMaybe(loadedSite.getProject(), loadedSite.getTypeId());

        }
    }


}


