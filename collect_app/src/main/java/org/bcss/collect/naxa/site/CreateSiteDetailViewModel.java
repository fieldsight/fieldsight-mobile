package org.bcss.collect.naxa.site;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteRepository;

public class CreateSiteDetailViewModel extends ViewModel {

    private SiteRepository siteRepository;
    private MutableLiveData<Site> siteMutableLiveData = new MutableLiveData<Site>();
    private MutableLiveData<Boolean> editSite = new MutableLiveData<>();

    public CreateSiteDetailViewModel(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
        editSite.setValue(false);
    }

    public MutableLiveData<Boolean> getEditSite() {
        return editSite;
    }

    public void setEditSite(boolean value) {
        this.editSite.setValue(value);
    }

    public SiteRepository getSiteRepository() {
        return siteRepository;
    }

}
