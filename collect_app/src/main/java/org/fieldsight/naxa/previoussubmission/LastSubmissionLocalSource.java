package org.fieldsight.naxa.previoussubmission;

import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

public class LastSubmissionLocalSource implements BaseLocalDataSource<SubmissionDetail> {

    private static LastSubmissionLocalSource lastSubmissionLocalSource;
    private final SubmissionDetailDAO dao;


    private LastSubmissionLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSubmissionDetailDAO();
    }


    public synchronized static LastSubmissionLocalSource getInstance() {
        if (lastSubmissionLocalSource == null) {
            lastSubmissionLocalSource = new LastSubmissionLocalSource();
        }
        return lastSubmissionLocalSource;
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

    public Maybe<SubmissionDetail> getBySiteFsId(String fsFormId){
        return dao.getBySiteFsId("gibberish");
    }

    public Maybe<SubmissionDetail> getByProjectFsId(String fsFormId){
        return dao.getByProjectFsId("gibberish");
    }


    @Override
    public void updateAll(ArrayList<SubmissionDetail> items) {

    }

    public void deleteAll() {
        AsyncTask.execute(() -> dao.deleteAll());
    }

}
