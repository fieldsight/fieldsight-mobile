package org.bcss.collect.naxa.notification;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class FieldSightNotificationDAO implements BaseDaoFieldSight<FieldSightNotification> {


    @Query("SELECT * FROM fieldsightnotification")
    public abstract LiveData<List<FieldSightNotification>> getAll();


    @Query("DELETE FROM fieldsightnotification")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<FieldSightNotification> items) {
        deleteAll();
        insert(items);
    }

}
