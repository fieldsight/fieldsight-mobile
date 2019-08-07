package org.fieldsight.naxa.notificationslist;

import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseRepository;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;

import java.util.ArrayList;
import java.util.List;

public class FieldSightNotificationRepository implements BaseRepository<FieldSightNotification> {


    private final FieldSightNotificationLocalSource localSource;
    private static FieldSightNotificationRepository INSTANCE = null;


    public static FieldSightNotificationRepository getInstance(FieldSightNotificationLocalSource localSource) {
        if (INSTANCE == null) {
            synchronized (FieldSightNotificationLocalSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FieldSightNotificationRepository(localSource);
                }
            }
        }
        return INSTANCE;
    }


    public FieldSightNotificationRepository(FieldSightNotificationLocalSource localSource) {
        this.localSource = localSource;
    }

    @Override
    public LiveData<List<FieldSightNotification>> getAll(boolean forceUpdate) {
        return localSource.getAll();
    }

    @Override
    public void save(FieldSightNotification... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<FieldSightNotification> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<FieldSightNotification> items) {
        //not implemented
    }
}
