package org.bcss.collect.naxa.generalforms;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRepository;
import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.SITE;

public class GeneralFormViewModel extends ViewModel {
    private final GeneralFormRepository repository;


    public GeneralFormViewModel(GeneralFormRepository repository) {
        this.repository = repository;
    }


    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<GeneralForm>> getForms(boolean forcedUpdate, Site loadedSite) {
        switch (loadedSite.getGeneralFormDeployedFrom()) {
            case SITE:
                return repository.getBySiteId(forcedUpdate, loadedSite.getId());
            case PROJECT:
            default:
                return repository.getByProjectId(forcedUpdate, loadedSite.getProject());

        }
    }
}
