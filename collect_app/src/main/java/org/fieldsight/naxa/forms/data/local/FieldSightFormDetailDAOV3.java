package org.fieldsight.naxa.forms.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

@Dao
public abstract class FieldSightFormDetailDAOV3 implements BaseDaoFieldSight<FieldsightFormDetailsv3> {
    @Transaction
    public void updateAll(FieldsightFormDetailsv3... fieldSightForms) {
        deleteAll();
        insert(fieldSightForms);
    }

    @Query("DELETE FROM fieldsight_formv3")
    protected abstract void deleteAll();

    @Query("SELECT * from fieldsight_formv3 WHERE type=:type AND (project=:projectId OR site_project_id=:siteId)")
    abstract LiveData<List<FieldsightFormDetailsv3>> getFormByType(String type, String projectId, String siteId);

    @Query("SELECT em from fieldsight_formv3 WHERE project=:projectId OR site_project_id=:projectId AND em IS NOT NULL")
    public abstract List<String> getEducationMaterailByProjectIds(String projectId);
}
