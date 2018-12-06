package org.bcss.collect.naxa.site;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.SingleLiveEvent;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.site.db.SiteRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateSiteDetailViewModel extends ViewModel {

    private SiteRepository siteRepository;
    private MutableLiveData<Site> siteMutableLiveData = new MutableLiveData<Site>();
    private SingleLiveEvent<CreateSiteDetailFormStatus> formStatus = new SingleLiveEvent<CreateSiteDetailFormStatus>();
    private MutableLiveData<List<SiteMetaAttribute>> metaAttributes = new MutableLiveData<>();
    private MutableLiveData<Boolean> editSite = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> metaAttributesViewIds = new MutableLiveData<>();
    private MutableLiveData<ArrayList<SiteRegion>> siteClusterMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SiteType>> siteTypesMutableLiveData = new MutableLiveData<>();
    private Site site = null;


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
        this.site = site;
    }

    public MutableLiveData<List<SiteMetaAttribute>> getMetaAttributes() {
        return metaAttributes;
    }

    public void setSiteMutableLiveData(MutableLiveData<Site> siteMutableLiveData) {
        this.siteMutableLiveData = siteMutableLiveData;
    }

    public void setSiteType(String typeId, String typeLabel) {
        siteMutableLiveData.getValue().setTypeId(typeId);
        siteMutableLiveData.getValue().setTypeLabel(typeLabel);
    }

    public MutableLiveData<ArrayList<SiteRegion>> getSiteClusterMutableLiveData() {
        return siteClusterMutableLiveData;
    }

    public void setSiteCluster(ArrayList<SiteRegion> siteRegionArrayList) {
        this.siteClusterMutableLiveData.setValue(siteRegionArrayList);
    }

    public MutableLiveData<List<SiteType>> getSiteTypesMutableLiveData() {
        return siteTypesMutableLiveData;
    }

    public MutableLiveData<ArrayList<Integer>> getMetaAttributesViewIds() {
        return metaAttributesViewIds;
    }

    public void appendMetaAttributeViewIds(Integer integer) {
        this.metaAttributesViewIds.getValue().add(integer);
    }

    public void setSiteTypes(List<SiteType> siteTypeList) {
        this.siteTypesMutableLiveData.setValue(siteTypeList);
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

    public SingleLiveEvent<CreateSiteDetailFormStatus> getFormStatus() {
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

    private boolean validateData() {

        if (siteMutableLiveData.getValue() == null) {
            return false;
        }

        String identifier = siteMutableLiveData.getValue().getIdentifier();
        String name = siteMutableLiveData.getValue().getName();
        String lat = siteMutableLiveData.getValue().getLatitude();
        String cluster = siteMutableLiveData.getValue().getRegionId();

        if (identifier == null || identifier.length() <= 0) {
            formStatus.setValue(CreateSiteDetailFormStatus.EMPTY_SITE_IDENTIFIER);
            return false;
        }

        if (name == null || name.length() <= 0) {
            formStatus.setValue(CreateSiteDetailFormStatus.EMPTY_SITE_NAME);
            return false;
        }

        if (lat == null || lat.length() <= 0) {
            formStatus.setValue(CreateSiteDetailFormStatus.EMPTY_ADDRESS);
            return false;
        }
//        formStatus.setValue(CreateSiteFormStatus.VALIDATED);
        return true;

    }

    void setMetaAttributesAnswer(String metaAttributesAnswer) {
        siteMutableLiveData.getValue().setMetaAttributes(metaAttributesAnswer);
    }

    void saveSite() {
        if (validateData()) {
            siteRepository.saveSiteModified(siteMutableLiveData.getValue())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            setEditSite(false);
                            formStatus.setValue(CreateSiteDetailFormStatus.SITE_SAVED);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }


}
