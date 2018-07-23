package org.odk.collect.naxa.generalforms.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Em {

    private Integer projectId;
    private String isFromProject;
    private Integer stageOrder;
    private Integer subStageOrder;
    private Integer order;
    private String dateModified;
    private Integer stageId;
    private Integer subStageId;
    private Long siteIdi;
    private Integer emStatus;
    private ArrayList<String> ImagesList;
    private String pdfPath;
    private String pdfName;

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getIsFromProject() {
        return isFromProject;
    }

    public void setIsFromProject(String isFromProject) {
        this.isFromProject = isFromProject;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(Integer stageOrder) {
        this.stageOrder = stageOrder;
    }

    public Integer getSubStageOrder() {
        return subStageOrder;
    }

    public void setSubStageOrder(Integer subStageOrder) {
        this.subStageOrder = subStageOrder;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getEmStatus() {
        return emStatus;
    }

    public void setEmStatus(Integer emStatus) {
        this.emStatus = emStatus;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public ArrayList<String> getImagesList() {
        return ImagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.ImagesList = imagesList;
    }

    public Integer getStageId() {
        return stageId;
    }

    public void setStageId(Integer stageId) {
        this.stageId = stageId;
    }

    public Integer getSubStageId() {
        return subStageId;
    }

    public void setSubStageId(Integer subStageId) {
        this.subStageId = subStageId;
    }

    public Long getSiteIdi() {
        return siteIdi;
    }

    public void setSiteIdi(Long siteIdi) {
        this.siteIdi = siteIdi;
    }

    public void setPdf(Boolean pdf) {
        isPdf = pdf;
    }

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("em_images")
    @Expose
    private List<EmImage> emImages = null;
    @SerializedName("is_pdf")
    @Expose
    private Boolean isPdf;
    @SerializedName("pdf")
    @Expose
    private String pdf;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("fsxf")
    @Expose
    private String fsFormId;

    private String emImagesString = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<EmImage> getEmImages() {
        return emImages;
    }

    public void setEmImages(List<EmImage> emImages) {
        this.emImages = emImages;
    }

    public Boolean getIsPdf() {
        return isPdf;
    }

    public void setIsPdf(Boolean isPdf) {
        this.isPdf = isPdf;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmImagesString() {
        return emImagesString;
    }

    public void setEmImagesString(String emImagesString) {
        this.emImagesString = emImagesString;
    }
}
