package org.bcss.collect.naxa.contact;

import android.arch.lifecycle.LiveData;

import org.bcss.collect.naxa.common.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class ContactRepository implements BaseRepository<FieldSightContactModel> {


    private final ContactLocalSource localSource;
    private final ContactRemoteSource remoteSource;
    private static ContactRepository INSTANCE = null;


    public static ContactRepository getInstance(ContactLocalSource localSource, ContactRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (ContactRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ContactRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    public ContactRepository(ContactLocalSource localSource, ContactRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
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
