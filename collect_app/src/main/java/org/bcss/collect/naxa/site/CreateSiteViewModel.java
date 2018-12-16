package org.bcss.collect.naxa.site;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.SingleLiveEvent;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteBuilder;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.site.db.SiteRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateSiteViewModel extends ViewModel {

    private SiteRepository siteRepository;
    private SingleLiveEvent<CreateSiteFormStatus> formStatus = new SingleLiveEvent<CreateSiteFormStatus>();
    private MutableLiveData<Site> siteMutableLiveData = new MutableLiveData<Site>();
    private MutableLiveData<Project> projectMutableLiveData = new MutableLiveData<Project>();

    private MutableLiveData<List<SiteMetaAttribute>> metaAttributes = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> metaAttributesViewIds = new MutableLiveData<>();
    private MutableLiveData<String> metaAttributesAnswer = new MutableLiveData<>();

    private MutableLiveData<ArrayList<SiteRegion>> siteClusterMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SiteType>> siteTypesMutableLiveData = new MutableLiveData<>();


    public CreateSiteViewModel(SiteRepository siteRepository) {

        this.siteRepository = siteRepository;
        this.metaAttributesViewIds.setValue(new ArrayList<>(0));
    }


    public MutableLiveData<List<SiteType>> getSiteTypesMutableLiveData() {
        return siteTypesMutableLiveData;
    }

    public void setSiteTypes(List<SiteType> siteTypes) {
        siteTypesMutableLiveData.setValue(siteTypes);
    }

    public MutableLiveData<ArrayList<SiteRegion>> getSiteClusterMutableLiveData() {
        return siteClusterMutableLiveData;
    }

    public void setSiteClusterMutableLiveData(ArrayList<SiteRegion> siteRegion) {
        this.siteClusterMutableLiveData.setValue(siteRegion);
    }


    public MutableLiveData<ArrayList<Integer>> getMetaAttributesViewIds() {
        return metaAttributesViewIds;
    }

    public void appendMetaAttributeViewIds(Integer integer) {
        this.metaAttributesViewIds.getValue().add(integer);
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
            formStatus.setValue(CreateSiteFormStatus.SUCCESS);
            //todo check if saving site is faliling

        }
    }

    void updateSite() {


        if (validateData()) {
            siteRepository.saveSiteModified(siteMutableLiveData.getValue(), projectMutableLiveData.getValue())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            formStatus.setValue(CreateSiteFormStatus.UPDATE_SUCESS);
                        }

                        @Override
                        public void onError(Throwable e) {
                            formStatus.setValue(CreateSiteFormStatus.ERROR);
                        }
                    });


        }
    }


    public void setMetaAttributes(List<SiteMetaAttribute> siteMetaAttributes) {
        metaAttributes.setValue(siteMetaAttributes);
    }

    public MutableLiveData<List<SiteMetaAttribute>> getMetaAttributesMutableLiveData() {
        return metaAttributes;
    }

    private boolean validateData() {

        if (siteMutableLiveData.getValue() == null) {
            return false;
        }

        String identifier = siteMutableLiveData.getValue().getIdentifier();
        String name = siteMutableLiveData.getValue().getName();
        String lat = siteMutableLiveData.getValue().getLatitude();
        String selectedRegion = siteMutableLiveData.getValue().getRegionId();


        if (identifier == null || identifier.length() <= 0) {
            formStatus.setValue(CreateSiteFormStatus.EMPTY_SITE_IDENTIFIER);
            return false;
        }


        if (name == null || name.length() <= 0) {
            formStatus.setValue(CreateSiteFormStatus.EMPTY_SITE_NAME);
            return false;
        }

        if (selectedRegion == null) {
            formStatus.setValue(CreateSiteFormStatus.REGION_NOT_SELECTED);
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
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setIdentifier(identifier);
    }

    public void setIsSiteVerified(int siteStatus) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setIsSiteVerified(siteStatus);
    }

    public void setSiteName(String name) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setName(name);
    }


    public void setFormDeployedFrom(String stagedDeployedForm, String generalDeployedForm, String scheduledDeployedForm) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }
        siteMutableLiveData.getValue().setStagedFormDeployedFrom(stagedDeployedForm);
        siteMutableLiveData.getValue().setGeneralFormDeployedFrom(generalDeployedForm);
        siteMutableLiveData.getValue().setScheduleFormDeployedForm(scheduledDeployedForm);

    }

    public void setSitePhone(String text) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setPhone(text);

    }

    public void setSiteType(String typeId, String typeLabel) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setTypeId(typeId);
        siteMutableLiveData.getValue().setTypeLabel(typeLabel);

    }


    public void setSiteRegion(String regionLabel, String regionId) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setRegionId(regionId);

    }

    public void setSiteAddress(String text) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setAddress(text);
    }

    public void setSitePublicDesc(String text) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setPublicDesc(text);

    }

    public void setLocation(String lat, String lon) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }
        siteMutableLiveData.getValue().setLatitude(lat);
        siteMutableLiveData.getValue().setLongitude(lon);

        formStatus.setValue(CreateSiteFormStatus.LOCATION_RECORDED);
    }

    public void setPhoto(String path) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setLogo(path);

        formStatus.setValue(CreateSiteFormStatus.PHOTO_TAKEN);
    }

    public void setId(String siteId) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setId(siteId);
    }


    public void setMetaAttributesAnswer(String metaAttributesAnswer) {
        if (siteMutableLiveData.getValue() == null) {
            siteMutableLiveData.setValue(new SiteBuilder().createSite());
        }

        siteMutableLiveData.getValue().setMetaAttributes(metaAttributesAnswer);

    }

    public String getMetaAttributesAnswer() {
        return metaAttributesAnswer.getValue();
    }
}
