package org.odk.collect.naxa.generalforms;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.generalforms.data.GeneralFormRepository;

import java.util.List;

public class GeneralFormViewModel extends AndroidViewModel {
    private final GeneralFormRepository repository;
    private final Application application;


    public GeneralFormViewModel(Application application, GeneralFormRepository repository) {
        super(application);
        this.application = application;
        this.repository = repository;
    }

    public LiveData<List<GeneralForm>> loadGeneralForms(boolean forceUpdate, String siteId) {

        return repository.getById(forceUpdate,siteId);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

}
