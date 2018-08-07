package org.bcss.collect.naxa.site.db;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {

    private SiteRepository mSiteRepository;
    private List<Site> mAllSites;

    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.mSiteRepository = SiteRepository.getInstance(SiteLocalSource.getInstance(), SiteRemoteSource.getInstance());

    }

    public List<Site> searchSites(String searchQuery) {
        return mSiteRepository.searchSites(searchQuery);
    }

    public LiveData<List<Site>> getSiteByProject(Project project) {
        return mSiteRepository.getSiteByProjectId(project.getId());
    }


    public void insertSitesAsVerified(Site site, Project project) {
        mSiteRepository.saveSitesAsVerified(site, project);
    }
}
