package org.fieldsight.naxa.survey;

import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseRepository;
import org.fieldsight.naxa.site.db.SiteRepository;

import java.util.ArrayList;
import java.util.List;

public class SurveyFormRepository implements BaseRepository<SurveyForm> {

    private static SurveyFormRepository surveyFormRepository;
    private final SurveyFormLocalSource localSource;

    public SurveyFormRepository(SurveyFormLocalSource localSource) {
        this.localSource = localSource;
    }


    public static SurveyFormRepository getInstance(SurveyFormLocalSource localSource) {
        if (surveyFormRepository == null) {
            synchronized (SiteRepository.class) {
                if (surveyFormRepository == null) {
                    surveyFormRepository = new SurveyFormRepository(localSource);
                }
            }
        }
        return surveyFormRepository;
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
