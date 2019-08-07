package org.fieldsight.naxa.contact;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


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
