package org.odk.collect.naxa.site;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.SingleLiveEvent;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.db.SiteRepository;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.util.Objects;

public class CreateSiteViewModel extends ViewModel {

    private SiteRepository siteRepository;
    private SingleLiveEvent<CreateSiteFormStatus> status = new SingleLiveEvent<CreateSiteFormStatus>();
    private MutableLiveData<FormState> state = new MutableLiveData<FormState>();
    private MutableLiveData<Site> siteMutableLiveData = new MutableLiveData<Site>();
    private MutableLiveData<Project> projectMutableLiveData = new MutableLiveData<Project>();

    public CreateSiteViewModel(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public SingleLiveEvent<CreateSiteFormStatus> getStatus() {
        return status;
    }

    public MutableLiveData<FormState> getState() {
        return state;
    }

    public void setProgressIndicator(Boolean isProgressShow) {
        state.getValue().setProgressIndicatorShown(isProgressShow);
    }

    public void setSiteType(Boolean show) {
        state.getValue().setSiteTypeShown(show);
    }

    public void setSiteCluster(Boolean show) {
        state.getValue().setSiteClusterShown(show);
    }


    public void saveSite() {
        if (validateData()) {
            //save site here
            siteRepository.saveSiteAsOffline(siteMutableLiveData.getValue(), projectMutableLiveData.getValue());
            ToastUtils.showShortToastInMiddle("Saving Site");

        }
    }


    private boolean validateData() {

        if (siteMutableLiveData.getValue() == null) {
            return false;
        }

        String identifier = siteMutableLiveData.getValue().getIdentifier();
        String name = siteMutableLiveData.getValue().getName();
        String siteId = siteMutableLiveData.getValue().getId();

        if (identifier == null || identifier.length() <= 0) {
            return false;
        }


        if (siteId == null || siteId.length() <= 0) {
            return false;
        }

        if (name == null || name.length() <= 0) {
            return false;
        }

        return true;

    }

    public MutableLiveData<Site> getSite() {
        return siteMutableLiveData;
    }

    public MutableLiveData<Project> getProjectMutableLiveData() {
        return projectMutableLiveData;
    }

    public void setSite(Site site) {
        siteMutableLiveData.setValue(site);
    }
}
