package org.odk.collect.naxa.database.site;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SiteDao {

    @Insert
    void insert(SiteModel word);

    @Query("DELETE FROM site_table")
    void deleteAll();

    @Query("SELECT * from site_table ORDER BY id ASC")
    List<SiteModel> getAllSites();

    @Query("SELECT * from site_table")
}
