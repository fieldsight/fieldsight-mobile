package org.bcss.collect.naxa.stages.data;

import java.util.ArrayList;

public class StageBuilder {
    private String id;
    private Integer projectStageId;
    private ArrayList<SubStage> subStage;
    private String name;
    private String description;
    private Integer order;
    private String dateCreated;
    private String dateModified;
    private Integer site;
    private Integer project;
    private String mockStages;

    public StageBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public StageBuilder setProjectStageId(Integer projectStageId) {
        this.projectStageId = projectStageId;
        return this;
    }

    public StageBuilder setSubStage(ArrayList<SubStage> subStage) {
        this.subStage = subStage;
        return this;
    }

    public StageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public StageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public StageBuilder setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public StageBuilder setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public StageBuilder setDateModified(String dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public StageBuilder setSite(Integer site) {
        this.site = site;
        return this;
    }

    public StageBuilder setProject(Integer project) {
        this.project = project;
        return this;
    }

    public StageBuilder setMockStages(String mockStages) {
        this.mockStages = mockStages;
        return this;
    }

    public Stage createStageNEWAPI() {
        return new Stage(id, projectStageId, subStage, name, description, order, dateCreated, dateModified, site, project, mockStages);
    }
}