package org.bcss.collect.naxa.notification;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class FieldSightNotificationLocalSource implements BaseLocalDataSource<FieldSightNotification> {

    private static FieldSightNotificationLocalSource INSTANCE;
    private final FieldSightNotificationDAO dao;

    public static FieldSightNotificationLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightNotificationLocalSource();
        }
        return INSTANCE;
    }

    private FieldSightNotificationLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getFieldSightNotificationDAO();
    }

    @Override
    public LiveData<List<FieldSightNotification>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(FieldSightNotification... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<FieldSightNotification> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<FieldSightNotification> items) {
        throw new RuntimeException("Not implemented");
    }

    public void clear() {
        AsyncTask.execute(() -> dao.deleteAll());

    }
}
