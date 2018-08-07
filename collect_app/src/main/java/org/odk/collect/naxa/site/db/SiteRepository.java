package org.odk.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.common.BaseRepository;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteRepository implements BaseRepository<GeneralForm> {

    private static SiteRepository INSTANCE = null;
    private final SiteLocalSource localSource;
    private final SiteRemoteSource remoteSource;



    public static SiteRepository getInstance(SiteLocalSource localSource, SiteRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (SiteRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SiteRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private SiteRepository(@NonNull SiteLocalSource localSource, @NonNull SiteRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }

    public List<Site> searchSites(String searchQuery) {
        return localSource.searchSites(searchQuery);
    }

    public LiveData<List<Site>> getAllSites() {
        return localSource.getAll();
    }

    public LiveData<List<Site>> getSiteByProjectId(String projectID) {
        return localSource.getById(projectID);
    }

    public void saveSitesAsVerified(Site site, Project project) {
        site.setIsSiteVerified(Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED);
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
         site.setIsSiteVerified(Constant.SiteStatus.IS_UNVERIFIED_SITE);
        site.setProject(project.getId());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                localSource.save(site);
            }
        });

    }
}
