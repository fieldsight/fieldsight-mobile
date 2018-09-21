package org.bcss.collect.naxa.substages.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

@Dao
public abstract class SubStageDAO implements BaseDaoFieldSight<SubStage> {

    @Query("SELECT * FROM SubStage")
    public abstract LiveData<List<SubStage>> getAllSubStages();


    @Query("DELETE FROM SubStage")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<SubStage> items) {
        deleteAll();
        insert(items);
    }


    @Query("SELECT * FROM substage WHERE stageId= :stageId")
    public abstract LiveData<List<SubStage>> getByStageId(String stageId);

    @Query("SELECT * FROM substage WHERE stageId= :stageId")
    public abstract Maybe<List<SubStage>> getByStageIdMaybe(String stageId);

    @Deprecated
    @Query("SELECT * FROM substage left join submission_detail on substage.fsFormId = submission_detail.siteFsFormId WHERE stageId =:stageId ")
    public abstract LiveData<List<SubStage>> getByStageIdAsLiveData(String stageId);

    @Deprecated
    @Query("SELECT * FROM substage left join submission_detail on substage.fsFormId = submission_detail.siteFsFormId WHERE stageId =:stageId ")
    public abstract Maybe<List<SubStage>> getByStageIdAsMaybe(String stageId);

}
