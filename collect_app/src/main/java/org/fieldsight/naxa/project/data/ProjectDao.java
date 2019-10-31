package org.fieldsight.naxa.project.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;
import org.fieldsight.naxa.login.model.Project;

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


    @Query("SELECT * from project where id=:id")
    public abstract Single<Project> getByIdAsSingle(String id);

    @Query("SELECT * from project where id=:id")
    public abstract Project getProject(String id);
}
