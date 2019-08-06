package org.bcss.collect.naxa.generalforms.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class GeneralFormDAO implements BaseDaoFieldSight<GeneralForm> {

    @Deprecated
    @Query("SELECT * FROM general_forms WHERE projectId =:projectId and isDeployed = 1")
    public abstract LiveData<List<GeneralForm>> getProjectGeneralForms(String projectId);

    @Deprecated
    @Query("SELECT * FROM general_forms WHERE (siteId =:siteId OR projectId =:projectId) and isDeployed = 1")
    public abstract LiveData<List<GeneralForm>> getSiteGeneralForms(String siteId, String projectId);


    @Query("select * from general_forms " +
            "left join submission_detail " +
            "on general_forms.fsFormId = submission_detail.projectFsFormId" +
            " WHERE general_forms.projectId =:projectId")
    public abstract LiveData<List<GeneralFormAndSubmission>> getProjectGeneralFormAndSubmission(String projectId);

    @Query("select * from general_forms " +
            "left join submission_detail " +
            "on general_forms.fsFormId = submission_detail.siteFsFormId" +
            " WHERE general_forms.siteId =:siteId OR general_forms.projectId =:projectId")
    public abstract LiveData<List<GeneralFormAndSubmission>> getSiteGeneralFormAndSubmission(String siteId, String projectId);

    @Query("DELETE FROM general_forms WHERE projectId=:projectId OR siteId=:siteId")
    public abstract void deleteAllById(String projectId, String siteId);

    @Transaction
    public void updateAll(ArrayList<GeneralForm> items) {
        insert(items.toArray(new GeneralForm[0]));
    }


    @Query("SELECT * FROM general_forms")
    public abstract LiveData<List<GeneralForm>> getAll();

    @Query("SELECT * FROM general_forms WHERE fsFormId =:fsFormId")
    public abstract LiveData<List<GeneralForm>> getById(String fsFormId);

}
