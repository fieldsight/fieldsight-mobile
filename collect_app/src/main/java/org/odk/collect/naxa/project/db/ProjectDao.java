package org.odk.collect.naxa.project.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;

import java.util.List;

@Dao
public interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Project... projects);

    @Query("SELECT * FROM project")
    LiveData<List<Project>> getProjects();
}
