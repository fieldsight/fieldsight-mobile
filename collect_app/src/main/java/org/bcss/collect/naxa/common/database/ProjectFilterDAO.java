package org.bcss.collect.naxa.common.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import io.reactivex.Maybe;
@Dao
public abstract class ProjectFilterDAO implements BaseDaoFieldSight<ProjectFilter> {

    @Query("SELECT * from projectfilter WHERE id =:projectId")
    public abstract LiveData<ProjectFilter> getById(String projectId);

    @Query("SELECT * from projectfilter WHERE id =:projectId")
    public abstract Maybe<ProjectFilter> getOnceById(String projectId);
}
