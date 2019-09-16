package org.fieldsight.naxa.common.database;

import androidx.room.Dao;
import androidx.room.Query;

import io.reactivex.Maybe;

@Dao
public abstract class SiteOverideDAO implements BaseDaoFieldSight<SiteOveride> {

    @Query("SELECT * FROM site_overide_ids WHERE projectId = :projectId")
    public abstract Maybe<SiteOveride> getSiteOverideFormIds(String projectId);

    @Query("SELECT * FROM site_overide_ids")
    public abstract Maybe<SiteOveride> getAll( );
}
