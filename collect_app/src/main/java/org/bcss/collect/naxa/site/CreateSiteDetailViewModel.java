package org.bcss.collect.naxa.site;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteBuilder;
import org.bcss.collect.naxa.site.db.SiteRepository;

import java.io.File;

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


    public void setSiteMutableLiveData(Site site) {
        this.siteMutableLiveData.setValue(site);
    }

    public void setIdentifier(String identifier) {
        siteMutableLiveData.getValue().setIdentifier(identifier);
    }

    public void setSiteName(String name) {
        siteMutableLiveData.getValue().setName(name);
    }

    public void setSitePhone(String text) {
        siteMutableLiveData.getValue().setPhone(text);
    }

    public void setSiteAddress(String text) {
        siteMutableLiveData.getValue().setAddress(text);
    }

    public void setPhoto(String path) {
        siteMutableLiveData.getValue().setLogo(path);
    }

    public void setLocation(String lat, String lon) {
        siteMutableLiveData.getValue().setLatitude(lat);
        siteMutableLiveData.getValue().setLongitude(lon);
    }

    public File generateImageFile(String imageName) {

        String path = Collect.SITES_PATH +
                File.separator +
                imageName +
                ".jpg";

        int i = 2;
        File f = new File(path);
        while (f.exists()) {

            path = Collect.SITES_PATH +
                    File.separator +
                    imageName +
                    "_" +
                    i +
                    ".jpg";


            f = new File(path);
            i++;
        }

        return f;
    }

    public void saveSite() {
//        if (validateData()) {
            siteRepository.saveSiteModified(siteMutableLiveData.getValue());
//            formStatus.setValue(CreateSiteFormStatus.SUCCESS);
            //todo check if saving site is faliling

//        }
    }

}
