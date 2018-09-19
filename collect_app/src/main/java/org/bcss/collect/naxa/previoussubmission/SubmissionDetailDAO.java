package org.bcss.collect.naxa.previoussubmission;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.common.database.SiteOveride;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;

@Dao
public abstract class SubmissionDetailDAO implements BaseDaoFieldSight<SubmissionDetail> {
    @Query("DELETE FROM submission_detail")
    public abstract void deleteAll();
}
