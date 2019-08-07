package org.fieldsight.naxa.site.db;


import android.app.Application;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {

    private SiteRepository mSiteRepository;
    private List<Site> mAllSites;

    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.mSiteRepository = SiteRepository.getInstance(SiteLocalSource.getInstance(), SiteRemoteSource.getInstance());

    }

    public List<Site> searchSites(String searchQuery,String projectId) {
        return mSiteRepository.searchSites(searchQuery,projectId);
    }

    public LiveData<List<Site>> getSiteByProject(Project project) {
        return mSiteRepository.getSiteByProjectId(project.getId());
    }


    public void insertSitesAsVerified(Site site, Project project) {
        mSiteRepository.saveSitesAsVerified(site, project);
    }


}
