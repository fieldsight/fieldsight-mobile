package org.odk.collect.naxa.database.site;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class SiteViewModel extends AndroidViewModel {

    private SiteRepository mSiteRepository;
    private LiveData<List<SiteModel>> mAllSiteModel;

    public SiteViewModel(@NonNull Application application) {
        super(application);
        this.mSiteRepository = new SiteRepository(application);
        this.mAllSiteModel = mSiteRepository.getAllWords();
    }

    public LiveData<List<SiteModel>> getmAllSiteModel() {
        return mAllSiteModel;
    }

    public void insert(SiteModel siteModel) {
        mSiteRepository.insert(siteModel);
    }
}
