/*
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

package org.odk.collect.android.dto;


import androidx.annotation.NonNull;

/**
 * This class represents a single row from the instances table which is located in
 * {@link org.odk.collect.android.provider.InstanceProvider#DATABASE_NAME}
 * For more information about this pattern go to https://en.wikipedia.org/wiki/Data_transfer_object
 * Objects of this class are created using builder pattern: https://en.wikipedia.org/wiki/Builder_pattern
 */
public final class Instance {
    private final String displayName;
    private final String submissionUri;
    private final String canEditWhenComplete;
    private final String instanceFilePath;
    private final String jrFormId;
    private final String jrVersion;
    private final String status;
    private final Long lastStatusChangeDate;
    private final Long deletedDate;
    private final String fieldSightSiteId;
    private final String fieldSightInstanceId;


    private final Long databaseId;

    private Instance(Builder builder) {
        displayName = builder.displayName;
        submissionUri = builder.submissionUri;
        canEditWhenComplete = builder.canEditWhenComplete;
        instanceFilePath = builder.instanceFilePath;
        jrFormId = builder.jrFormId;
        jrVersion = builder.jrVersion;
        status = builder.status;
        lastStatusChangeDate = builder.lastStatusChangeDate;
        deletedDate = builder.deletedDate;
        fieldSightSiteId = builder.fieldSightSiteId;
        fieldSightInstanceId = builder.fieldSightInstanceId;
        databaseId = builder.databaseId;

    }

    public static class Builder {
        private String displayName;
        private String submissionUri;
        private String canEditWhenComplete;
        private String instanceFilePath;
        private String jrFormId;
        private String jrVersion;
        private String status;
        private Long lastStatusChangeDate;
        private Long deletedDate;
        private String fieldSightSiteId;
        private String fieldSightInstanceId;

        private Long databaseId;

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder submissionUri(String submissionUri) {
            this.submissionUri = submissionUri;
            return this;
        }

        public Builder canEditWhenComplete(String canEditWhenComplete) {
            this.canEditWhenComplete = canEditWhenComplete;
            return this;
        }

        public Builder instanceFilePath(String instanceFilePath) {
            this.instanceFilePath = instanceFilePath;
            return this;
        }

        public Builder jrFormId(String jrFormId) {
            this.jrFormId = jrFormId;
            return this;
        }

        public Builder jrVersion(String jrVersion) {
            this.jrVersion = jrVersion;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder lastStatusChangeDate(Long lastStatusChangeDate) {
            this.lastStatusChangeDate = lastStatusChangeDate;
            return this;
        }

        public Builder deletedDate(Long deletedDate) {
            this.deletedDate = deletedDate;
            return this;
        }


        public Builder fieldSightSiteId(String fieldSightSiteId) {
            this.fieldSightSiteId = fieldSightSiteId;
            return this;
        }

        public Builder databaseId(Long databaseId) {
            this.databaseId = databaseId;
            return this;
        }

        public Builder fieldSightInstanceId(String fieldSightInstanceId) {
            this.fieldSightInstanceId = fieldSightInstanceId;
            return this;
        }

        public Instance build() {
            return new Instance(this);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSubmissionUri() {
        return submissionUri;
    }

    public String getCanEditWhenComplete() {
        return canEditWhenComplete;
    }

    public String getInstanceFilePath() {
        return instanceFilePath;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public String getJrVersion() {
        return jrVersion;
    }

    public String getStatus() {
        return status;
    }

    public Long getLastStatusChangeDate() {
        return lastStatusChangeDate;
    }

    public Long getDeletedDate() {
        return deletedDate;
    }


    public String getFieldSightSiteId() {
        return fieldSightSiteId;
    }

    public String getFieldSightInstanceId() {
        return fieldSightInstanceId;
    }

    public Long getDatabaseId() {
        return databaseId;

    }

    @NonNull
    @Override
    public String toString() {
        return "Instance{" +
                "displayName='" + displayName + '\'' + '\n' +
                ", submissionUri='" + submissionUri + '\'' + '\n' +
                ", canEditWhenComplete='" + canEditWhenComplete + '\'' + '\n' +
                ", instanceFilePath='" + instanceFilePath + '\'' + '\n' +
                ", jrFormId='" + jrFormId + '\'' + '\n' +
                ", jrVersion='" + jrVersion + '\'' + '\n' +
                ", status='" + status + '\'' + '\n' +
                ", lastStatusChangeDate=" + lastStatusChangeDate + '\n' +
                ", deletedDate=" + deletedDate + '\n' +
                ", fieldSightSiteId='" + fieldSightSiteId + '\'' + '\n' +
                ", fieldSightInstanceId='" + fieldSightInstanceId + '\'' + '\n' +
                ", databaseId=" + databaseId + '\n' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Instance
                && this.instanceFilePath.equals(((Instance) other).instanceFilePath);
    }

    @Override
    public int hashCode() {
        return instanceFilePath.hashCode();
    }
}
