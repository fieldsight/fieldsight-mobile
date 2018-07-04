package org.odk.collect.naxa.site.db;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.login.model.Site;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {

    private SiteRepository mSiteRepository;
    private List<Site> mAllSites;

    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.mSiteRepository = new SiteRepository(application);

    }

    public LiveData<List<Site>> getmAllSites() {
        return mSiteRepository.getAllSites();
    }

    public void insert(Site siteModel) {
        mSiteRepository.insert(siteModel);
    }
}
