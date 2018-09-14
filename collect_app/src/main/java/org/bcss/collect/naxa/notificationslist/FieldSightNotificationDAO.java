package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.data.FieldSightNotification;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;


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

    @Query("SELECT COUNT(notificationType) FROM fieldsightnotification " +
            "WHERE notificationType in (:strings) " +
            "and (siteId =:siteId or projectId =:projectId)  and isRead =:read ")
    public abstract LiveData<Integer> notificationCount(Boolean read, String siteId, String projectId, String... strings);

    @Query("SELECT COUNT(notificationType) FROM fieldsightnotification WHERE notificationType in (:notificationTypes) and isRead =:read ")
    public abstract Maybe<Integer> countForNotificationType(Boolean read, String... notificationTypes);

    @Query("UPDATE fieldsightnotification SET isRead=:read WHERE notificationType in (:notificationTypes)")
    public abstract void applyReadToNotificationType(Boolean read, String... notificationTypes);

    @Query("SELECT " +
            "(SELECT COUNT(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType =:assign and isRead =:read ) " +
            " - " +
            "(SELECT COUNT(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType =:assign AND projectId in(:projectIds) and isRead =:read )")
    public abstract LiveData<Integer> countNonExistentProjectInNotification(Boolean read,String assign, String... projectIds);


        /*
    *
 SELECT ( SELECT count(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType = "Assign Site") -
 ( SELECT count(DISTINCT projectId) FROM fieldsightnotification WHERE notificationType = "Assign Site" AND projectId in(183))
     */
}
