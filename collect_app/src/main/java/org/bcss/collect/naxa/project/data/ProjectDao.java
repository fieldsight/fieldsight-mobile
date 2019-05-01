package org.bcss.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.login.model.Project;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class ProjectDao implements BaseDaoFieldSight<Project> {

    @Query("SELECT * FROM project")
    public abstract Single<List<Project>> getProjectsMaybe();

    @Query("SELECT * FROM project")
    public abstract LiveData<List<Project>> getProjectsLive();


    @Query("DELETE FROM project")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<Project> items) {
        deleteAll();
        insert(items);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertWithIgnore(Project[] project);

    @Query("UPDATE project  SET siteClusters = :siteClusters WHERE id = :projectId")
    public abstract void updateCluster(String projectId, String siteClusters);

    @Query("SELECT * from project where id=:id")
    public abstract LiveData<Project> getById(String id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Project> items);

}

