package org.fieldsight.naxa.forms.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

@Dao
public abstract class FieldSightFormDAO implements BaseDaoFieldSight<FieldSightForm> {

    @Transaction
    public void updateAll(FieldSightForm... fieldSightForms) {
        deleteAll();
        insert(fieldSightForms);
    }

    @Query("DELETE FROM fieldsight_forms")
    protected abstract void deleteAll();

    @Query("SELECT * from fieldsight_forms WHERE formType=:type AND (formDeployedProjectId=:projectId OR formDeployedSiteId=:siteId)")
    abstract LiveData<List<FieldSightForm>> getFormByType(String type, String projectId, String siteId);
}
