package org.bcss.collect.naxa.survey;

import android.arch.lifecycle.LiveData;

import org.bcss.collect.naxa.common.BaseRepository;
import org.bcss.collect.naxa.site.db.SiteRepository;

import java.util.ArrayList;
import java.util.List;

public class SurveyFormRepository implements BaseRepository<SurveyForm> {

    private static SurveyFormRepository INSTANCE = null;
    private final SurveyFormLocalSource localSource;

    public SurveyFormRepository(SurveyFormLocalSource localSource) {
        this.localSource = localSource;
    }


    public static SurveyFormRepository getInstance(SurveyFormLocalSource localSource) {
        if (INSTANCE == null) {
            synchronized (SiteRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SurveyFormRepository(localSource);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<SurveyForm>> getAll(boolean forceUpdate) {
        return localSource.getAll();
    }

    @Override
    public void save(SurveyForm... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<SurveyForm> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<SurveyForm> items) {
        localSource.updateAll(items);
    }

    public LiveData<List<SurveyForm>> getByProjectId(String projectId) {
        return localSource.getByProjectId(projectId);
    }
}
