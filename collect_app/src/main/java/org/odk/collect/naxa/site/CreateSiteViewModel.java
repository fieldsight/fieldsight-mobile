package org.odk.collect.naxa.site;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.SingleLiveEvent;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.db.SiteRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CreateSiteViewModel extends ViewModel {

    private SiteRepository siteRepository;
    private SingleLiveEvent<CreateSiteFormStatus> formStatus = new SingleLiveEvent<CreateSiteFormStatus>();
    private MutableLiveData<Boolean> showSiteType = new MutableLiveData<Boolean>();
    private MutableLiveData<Boolean> showSiteCluster = new MutableLiveData<Boolean>();
    private MutableLiveData<Boolean> showProgress = new MutableLiveData<Boolean>();
    private MutableLiveData<Site> siteMutableLiveData = new MutableLiveData<Site>();
    private MutableLiveData<Project> projectMutableLiveData = new MutableLiveData<Project>();

    public MutableLiveData<Boolean> getShowSiteType() {
        return showSiteType;
    }

    public MutableLiveData<Boolean> getShowSiteCluster() {
        return showSiteCluster;
    }

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }

    public CreateSiteViewModel(SiteRepository siteRepository) {

        this.siteRepository = siteRepository;
    }

    public SingleLiveEvent<CreateSiteFormStatus> getFormStatus() {
        return formStatus;
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
        if (validateData()) {
            siteRepository.saveSiteAsOffline(siteMutableLiveData.getValue(), projectMutableLiveData.getValue());
            ToastUtils.showShortToastInMiddle("Saving Site");
            //todo check if saving site is faliling

        }
    }


    private boolean validateData() {

        if (siteMutableLiveData.getValue() == null) {
            return false;
        }

        String identifier = siteMutableLiveData.getValue().getIdentifier();
        String name = siteMutableLiveData.getValue().getName();
        String lat = siteMutableLiveData.getValue().getLatitude();

        if (identifier == null || identifier.length() <= 0) {
            formStatus.setValue(CreateSiteFormStatus.EMPTY_SITE_IDENTIFIER);
            return false;
        }


        if (name == null || name.length() <= 0) {
            formStatus.setValue(CreateSiteFormStatus.EMPTY_SITE_NAME);
            return false;
        }

        if (lat == null || lat.length() <= 0) {
            formStatus.setValue(CreateSiteFormStatus.EMPTY_SITE_LOCATION);
            return false;
        }

        formStatus.setValue(CreateSiteFormStatus.VALIDATED);

        return true;

    }

    public MutableLiveData<Site> getSite() {
        return siteMutableLiveData;
    }

    public MutableLiveData<Project> getProjectMutableLiveData() {
        return projectMutableLiveData;
    }


    public void setIdentifier(String identifier) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setIdentifier(identifier);
    }

    public void setSiteName(String name) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setName(name);
    }

    public void setSitePhone(String text) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setPhone(text);

    }

    public void setSiteAddress(String text) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setAddress(text);
    }

    public void setSitePublicDesc(String text) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setPublicDesc(text);

    }

    public void setLocation(String lat, String lon) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }
        siteMutableLiveData.getValue().setLatitude(lat);
        siteMutableLiveData.getValue().setLongitude(lon);

    }

    public void setPhoto(String path) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setLogo(path);
    }

    public void setId(String siteId) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new Site());
        }

        siteMutableLiveData.getValue().setId(siteId);
    }


}
