package org.bcss.collect.naxa.onboarding;

public class XMLFormBuilder {
    private String formCreatorsId;
    private boolean isCreatedFromProject;
    private String downloadUrl;
    private String formType;
    private String title;

    public XMLFormBuilder setFormCreatorsId(String formCreatorsId) {
        this.formCreatorsId = formCreatorsId;
        return this;
    }

    public XMLFormBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public XMLFormBuilder setIsCreatedFromProject(boolean isCreatedFromProject) {
        this.isCreatedFromProject = isCreatedFromProject;
        return this;
    }

    public XMLFormBuilder setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public XMLFormBuilder setFormType(String formType) {
        this.formType = formType;
        return this;
    }

    public XMLForm createXMLForm() {
        return new XMLForm(formCreatorsId, isCreatedFromProject, downloadUrl, formType, title);
    }
}