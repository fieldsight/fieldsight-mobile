package org.fieldsight.naxa.educational;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;
import org.fieldsight.naxa.generalforms.data.Em;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class EducationalMaterialsDao implements BaseDaoFieldSight<Em> {


    @Query("SELECT * FROM educational_materials where fsFormId =:fsFormId ")
    public abstract Single<Em> getByFsFormId(String fsFormId);


    @Query("SELECT * FROM educational_materials")
    public abstract Single<List<Em>> getByFsFormId();

    @Query("SELECT * FROM educational_materials")
    public abstract LiveData<List<Em>> getAll();
}
