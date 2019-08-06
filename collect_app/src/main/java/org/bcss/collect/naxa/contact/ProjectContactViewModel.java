package org.bcss.collect.naxa.contact;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;


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
