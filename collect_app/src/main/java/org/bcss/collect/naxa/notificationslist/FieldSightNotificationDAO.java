package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.data.FieldSightNotification;

import java.util.ArrayList;
import java.util.List;


@Dao
public abstract class FieldSightNotificationDAO implements BaseDaoFieldSight<FieldSightNotification> {


    @Query("SELECT * FROM fieldsightnotification ORDER BY id DESC")
    public abstract LiveData<List<FieldSightNotification>> getAll();


    @Query("DELETE FROM fieldsightnotification")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<FieldSightNotification> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM fieldsightnotification  ORDER BY id DESC")
    public abstract LiveData<FieldSightNotification> getById();


    @Query("SELECT COUNT(notificationType) FROM fieldsightnotification " +
            "WHERE notificationType in (:strings) " +
            "and (siteId =:siteId or projectId =:projectId)")
    public abstract LiveData<Integer> notificationCount(String siteId, String projectId, String... strings);


    /*
    *
 SELECT ( SELECT count(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType = "Assign Site") -
 ( SELECT count(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType = "Assign Site" AND projectId in(183))
     */


    @Query("SELECT " +
            "(SELECT COUNT(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType =:assign) " +
            " - " +
            "(SELECT COUNT(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType =:assign AND projectId in(:projectIds))")

    public abstract LiveData<Integer> countNonExistentProjectInNotification(String assign, String... projectIds);
}
