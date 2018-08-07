package org.odk.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;
import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.Project;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

@Dao
public abstract class ProjectDao implements BaseDaoFieldSight<Project> {

    @Query("SELECT * FROM project")
    public abstract Maybe<List<Project>> getProjectsMaybe();

    @Query("SELECT * FROM project")
    public abstract LiveData<List<Project>> getProjectsLive();


    @Query("DELETE FROM project")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<Project> items) {
        deleteAll();
        insert(items);
    }

    @Query("UPDATE project  SET siteClusters = :siteClusters WHERE id = :projectId")
    public abstract void updateCluster(String projectId, String siteClusters);
}
