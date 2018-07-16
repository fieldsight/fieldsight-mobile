package org.odk.collect.naxa.onboarding;

/**
 * Created on 11/19/17
 * by nishon.tan@gmail.com
 */

public class XMLForm {
    private String formCreatorsId;
    private boolean isCreatedFromProject;
    private String downloadUrl;
    private String title;

    public String getTitle() {
        return title;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    private String formType;


    public XMLForm(String formCreatorsId, boolean isCreatedFromProject, String downloadUrl, String formType, String title) {
        this.formCreatorsId = formCreatorsId;
        this.isCreatedFromProject = isCreatedFromProject;
        this.downloadUrl = downloadUrl;
        this.formType = formType;
        this.title = title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getFormCreatorsId() {
        return formCreatorsId;
    }

    public boolean isCreatedFromProject() {
        return isCreatedFromProject;
    }

    public static String toNumeralString(Boolean input) {
        if (input == null) {
            return "null";
        } else {
            return input ? "1" : "0";
        }
    }

    public Boolean isCreatedFromSite() {
        return !isCreatedFromProject();
    }
}
