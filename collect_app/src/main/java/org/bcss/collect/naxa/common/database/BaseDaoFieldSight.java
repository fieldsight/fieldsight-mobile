package org.bcss.collect.naxa.common.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import java.util.ArrayList;

@Dao
public interface BaseDaoFieldSight<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArrayList<T> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T... items);

    @Update
    void update(T entity);

    @Delete
    void delete(T entity);




}
