package org.odk.collect.naxa.database.project;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ProjectDao {

    @Insert
    void insert(ProjectModel project);

    @Query("DELETE FROM project_table")
    void deleteAll();

    @Query("SELECT * from project_table ORDER BY id ASC")
    LiveData<List<ProjectModel>> getAllProjects();
}
