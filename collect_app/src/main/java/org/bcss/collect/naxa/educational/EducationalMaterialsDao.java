package org.bcss.collect.naxa.educational;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.generalforms.data.Em;

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
