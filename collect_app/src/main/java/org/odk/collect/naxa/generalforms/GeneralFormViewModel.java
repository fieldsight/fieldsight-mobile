package org.odk.collect.naxa.generalforms;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.generalforms.data.GeneralFormRepository;

import java.util.List;

public class GeneralFormViewModel extends ViewModel {
    private final GeneralFormRepository repository;


    public GeneralFormViewModel(GeneralFormRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<GeneralForm>> getBySiteId(boolean forceUpdate, String siteId, String formDeployedFrom) {
        return repository.getBySiteId(forceUpdate, siteId, formDeployedFrom);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

}
