package org.bcss.collect.naxa.educational;/*
 * Copyright 2017 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * This class represents a single row from the forms table which is located in
 * For more information about this pattern go to https://en.wikipedia.org/wiki/Data_transfer_object
 * Objects of this class are created using builder pattern: https://en.wikipedia.org/wiki/Builder_pattern
 */

public class EducationalMaterial {
    @PrimaryKey
    @NonNull
    private String fsFormId;

    private String title;
    private String description;
    private String pdfFilePath;
    private String imageFilesPaths;
    private String siteId;
    private String formType;

    public EducationalMaterial(){

    }

    @Ignore
    private EducationalMaterial(EducationalMaterial.Builder builder) {
        title = builder.title;
        description = builder.description;
        fsFormId = builder.fsFormId;
        pdfFilePath = builder.pdfFilePath;
        imageFilesPaths = builder.imageFilesPaths;
        siteId = builder.siteId;
        formType = builder.formType;
    }

    public static class Builder {
        private String title;
        private String description;
        private String fsFormId;
        private String pdfFilePath;
        private String imageFilesPaths;
        private String siteId;
        private String formType;

        public Builder title(String displayName) {
            this.title = displayName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder fsFormId(String jrFormId) {
            this.fsFormId = jrFormId;
            return this;
        }

        public Builder pdfFilePath(String jrVersion) {
            this.pdfFilePath = jrVersion;
            return this;
        }


        public Builder imagesFilePath(String formFilePath) {
            this.imageFilesPaths = formFilePath;
            return this;
        }

        public Builder projectId(String submissionUri) {
            this.siteId = submissionUri;
            return this;
        }

        public Builder formType(String formType) {
            this.formType = formType;
            return this;
        }


        public EducationalMaterial build() {
            return new EducationalMaterial(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFsFormId() {
        return fsFormId;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public String getImageFilesPaths() {
        return imageFilesPaths;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public void setImageFilesPaths(String imageFilesPaths) {
        this.imageFilesPaths = imageFilesPaths;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getFormType(){

        return formType;
    }

  }
