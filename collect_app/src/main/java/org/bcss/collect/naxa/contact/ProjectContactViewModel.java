package org.bcss.collect.naxa.contact;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;


public class ProjectContactViewModel extends ViewModel {
    private final ContactRepository repository;

    public ProjectContactViewModel(ContactRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<FieldSightContactModel>> getContacts() {
        return repository.getAll(false);
    }
}
