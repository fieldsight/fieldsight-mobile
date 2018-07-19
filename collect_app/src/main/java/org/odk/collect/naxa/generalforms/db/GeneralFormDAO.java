package org.odk.collect.naxa.generalforms.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;
import org.odk.collect.naxa.generalforms.GeneralForm;

import java.util.List;

@Dao
public abstract class GeneralFormDAO implements BaseDaoFieldSight<GeneralForm> {

    @Query("SELECT * FROM general_form_proj")
    public abstract LiveData<List<GeneralForm>> getProjectGeneralForms();
}
