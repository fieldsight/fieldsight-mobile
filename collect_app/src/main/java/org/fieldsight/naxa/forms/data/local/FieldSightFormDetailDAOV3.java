package org.fieldsight.naxa.forms.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@Dao
public abstract class FieldSightFormDetailDAOV3 implements BaseDaoFieldSight<FieldsightFormDetailsv3> {
    @Transaction
    public void updateAll(String[] projectIds, FieldsightFormDetailsv3... fieldSightForms) {
        deleteByIds(projectIds);
        insert(fieldSightForms);
    }

    @Query("DELETE FROM fieldsight_formv3 WHERE project IN (:projectIds)")
    protected abstract void deleteByIds(String[] projectIds);

    @Query("DELETE FROM fieldsight_formv3")
    protected abstract void deleteAll();

    @Query("SELECT * from fieldsight_formv3 WHERE type=:type AND (project=:projectId OR site_project_id=:projectId) OR site=:siteId")
    abstract LiveData<List<FieldsightFormDetailsv3>> getFormByType(String type, String projectId, String siteId);

    @Query("SELECT * from fieldsight_formv3 WHERE project=:projectId OR site_project_id=:projectId AND em IS NOT NULL AND em !='null'")
    public abstract List<FieldsightFormDetailsv3> getEducationMaterailByProjectIds(String projectId);

    @Query("SELECT * FROM fieldsight_formv3 WHERE id=:fsFormId")
    abstract FieldsightFormDetailsv3 getByFsFormId(String fsFormId);
}
