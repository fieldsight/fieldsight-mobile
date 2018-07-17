package org.odk.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.odk.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Site... sites);

    @Query("SELECT * FROM site")
    LiveData<List<Site>> getSites();

    @Query("SELECT * FROM site WHERE name LIKE :siteName OR phone LIKE :sitePhoneNumber OR identifier LIKE :siteIdentifier OR address LIKE :siteAddress")
    List<Site> searchSites(String searchQuery, String sitePhoneNumber, String siteIdentifier, String siteAddress);
}
