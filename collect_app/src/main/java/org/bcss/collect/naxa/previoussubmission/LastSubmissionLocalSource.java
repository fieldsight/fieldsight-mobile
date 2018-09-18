package org.bcss.collect.naxa.previoussubmission;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;

import java.util.ArrayList;
import java.util.List;

public class LastSubmissionLocalSource implements BaseLocalDataSource<SubmissionDetail> {

    private static LastSubmissionLocalSource INSTANCE;
    private SubmissionDetailDAO dao;


    private LastSubmissionLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSubmissionDetailDAO();
    }


    public static LastSubmissionLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LastSubmissionLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<SubmissionDetail>> getAll() {
        return null;
    }

    @Override
    public void save(SubmissionDetail... items) {
        AsyncTask.execute(() -> {
            dao.insert(items);
        });
    }

    @Override
    public void save(ArrayList<SubmissionDetail> items) {
        AsyncTask.execute(() -> {
            dao.insert(items);
        });
    }

    @Override
    public void updateAll(ArrayList<SubmissionDetail> items) {

    }


}
