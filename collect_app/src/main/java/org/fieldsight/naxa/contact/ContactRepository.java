package org.fieldsight.naxa.contact;

import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class ContactRepository implements BaseRepository<FieldSightContactModel> {


    private final ContactLocalSource localSource;

    private static ContactRepository contactRepository;


    public static synchronized ContactRepository getInstance(ContactLocalSource localSource) {
        if (contactRepository == null) {
            contactRepository = new ContactRepository(localSource);
        }
        return contactRepository;
    }


    public ContactRepository(ContactLocalSource localSource) {
        this.localSource = localSource;

    }

    @Override
    public LiveData<List<FieldSightContactModel>> getAll(boolean forceUpdate) {
        return localSource.getAll();
    }

    @Override
    public void save(FieldSightContactModel... items) {

    }

    @Override
    public void save(ArrayList<FieldSightContactModel> items) {

    }

    @Override
    public void updateAll(ArrayList<FieldSightContactModel> items) {

    }
}
