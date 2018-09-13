package org.bcss.collect.naxa.contact;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.login.model.Project;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

@Dao
public abstract class ContacstDao implements BaseDaoFieldSight<FieldSightContactModel> {
    @Query("SELECT * FROM contacts")
    public abstract LiveData<List<FieldSightContactModel>> getAll();


}
