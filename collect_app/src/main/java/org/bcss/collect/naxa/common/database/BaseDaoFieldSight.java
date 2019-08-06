package org.bcss.collect.naxa.common.database;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface BaseDaoFieldSight<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArrayList<T> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T... items);

    @Update
    void update(T... entity);

    @Delete
    void delete(T entity);

}
