package org.odk.collect.naxa.database.site;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {

    private SiteRepository mSiteRepository;
    private List<SiteModel> mAllSites;

    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.mSiteRepository = new SiteRepository(application);
        this.mAllSites = mSiteRepository.getAllSites();
    }

    public List<SiteModel> getmAllSites() {
        return mAllSites;
    }

    public void insert(SiteModel siteModel) {
        mSiteRepository.insert(siteModel);
    }
}
