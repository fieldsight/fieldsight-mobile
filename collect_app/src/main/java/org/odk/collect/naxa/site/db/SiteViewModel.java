package org.odk.collect.naxa.site.db;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {

    private SiteRepository mSiteRepository;
    private List<Site> mAllSites;

    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.mSiteRepository = new SiteRepository();

    }

    public List<Site> searchSites(String searchQuery) {
        return mSiteRepository.searchSites(searchQuery);
    }

    public LiveData<List<Site>> getmAllSites() {
        return mSiteRepository.getAllSites();
    }

    public LiveData<List<Site>> getSiteByProject(Project project) {
        return mSiteRepository.getSiteByProjectId(project.getId());
    }


    public void insertSitesAsVerified(Site site, Project project) {
        mSiteRepository.insertSitesAsVerified(site,project);
    }
}
