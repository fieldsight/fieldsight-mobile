package org.bcss.collect.naxa.contact;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactLocalSource implements BaseLocalDataSource<FieldSightContactModel> {

    private static ContactLocalSource INSTANCE;
    private final ContacstDao dao;


    private ContactLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getContactsDao();
    }


    public static ContactLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContactLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<FieldSightContactModel>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(FieldSightContactModel... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<FieldSightContactModel> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<FieldSightContactModel> items) {

    }
}
