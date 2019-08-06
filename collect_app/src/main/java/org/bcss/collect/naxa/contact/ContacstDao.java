package org.bcss.collect.naxa.contact;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

@Dao
public abstract class ContacstDao implements BaseDaoFieldSight<FieldSightContactModel> {
    @Query("SELECT * FROM contacts")
    public abstract LiveData<List<FieldSightContactModel>> getAll();


}
