package org.fieldsight.naxa.site.db;

import android.os.AsyncTask;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.common.BaseRepository;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;

public class SiteRepository implements BaseRepository<GeneralForm> {

    private static SiteRepository instance;
    private final SiteLocalSource localSource;


    public synchronized static SiteRepository getInstance(SiteLocalSource localSource) {
        if (instance == null) {
            instance = new SiteRepository(localSource);
        }
        return instance;
    }


    private SiteRepository(@NonNull SiteLocalSource localSource) {
        this.localSource = localSource;

    }

    public List<Site> searchSites(String searchQuery, String projectId) {
        return localSource.searchSites(searchQuery, projectId);
    }

    public LiveData<Site> getSiteById(String id) {
        return localSource.getBySiteId(id);
    }

    public LiveData<List<Site>> getAllSites() {
        return localSource.getAll();
    }

    public LiveData<List<Site>> getSiteByProjectId(String projectID) {
        return localSource.getById(projectID);
    }

    public void saveSitesAsVerified(Site site, Project project) {
        site.setIsSiteVerified(Constant.SiteStatus.IS_ONLINE);
        site.setProject(project.getId());
        AsyncTask.execute(() -> localSource.save(site));
    }


    @Override
    public LiveData<List<GeneralForm>> getAll(boolean forceUpdate) {
        return null;
    }

    @Override
    public void save(GeneralForm... items) {

    }

    @Override
    public void save(ArrayList<GeneralForm> items) {

    }

    @Override
    public void updateAll(ArrayList<GeneralForm> items) {

    }

    public void saveSiteAsOffline(Site site, Project project) {
        site.setIsSiteVerified(Constant.SiteStatus.IS_OFFLINE);
        site.setProject(project.getId());
        site.setGeneralFormDeployedFrom(PROJECT);
        site.setScheduleFormDeployedForm(PROJECT);
        site.setStagedFormDeployedFrom(PROJECT);
        AsyncTask.execute(() -> localSource.save(site));
    }

    public Completable saveSiteModified(Site site, Project project) {
        if (site.getIsSiteVerified() == Constant.SiteStatus.IS_ONLINE) {
            site.setIsSiteVerified(Constant.SiteStatus.IS_EDITED);
        }

        site.setProject(project.getId());
        return localSource.saveAsCompletable(site);


    }


    public void deleteSyncedSitesAsync() {
        localSource.deleteSyncedSitesAsync();
    }
}
