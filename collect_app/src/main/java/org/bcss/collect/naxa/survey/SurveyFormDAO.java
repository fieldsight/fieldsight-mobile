package org.bcss.collect.naxa.survey;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class SurveyFormDAO implements BaseDaoFieldSight<SurveyForm> {
    @Query("SELECT * FROM survey_forms")
    public abstract LiveData<List<SurveyForm>> getAllSurveyForms();


    @Query("DELETE FROM survey_forms")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<SurveyForm> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM survey_forms WHERE projectId= :projectId")
    public abstract LiveData<List<SurveyForm>> getByProjectId(String projectId);
}
