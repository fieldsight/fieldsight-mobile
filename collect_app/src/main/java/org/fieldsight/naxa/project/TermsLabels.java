package org.fieldsight.naxa.project;

import org.json.JSONObject;

public class TermsLabels {
    public String region_supervisor,
            region,
            site,
            siteSupervisor,
            siteReviewer,
            regionReviewer,
            donor;

    private TermsLabels(String region_supervisor, String region, String site, String site_supervisor, String site_reviewer, String region_reviewer, String donor) {
        this.region_supervisor = region_supervisor;
        this.region = region;
        this.site = site;
        this.siteSupervisor = site_supervisor;
        this.siteReviewer = site_reviewer;
        this.regionReviewer = region_reviewer;
        this.donor = donor;
    }

    public static TermsLabels fromJSON(JSONObject jsonObject) {
        return new TermsLabels(
                jsonObject.optString("region_supervisor"),
                jsonObject.optString("region"),
                jsonObject.optString("site"),
                jsonObject.optString("siteSupervisor"),
                jsonObject.optString("siteReviewer"),
                jsonObject.optString("regionReviewer"),
                jsonObject.optString("donor")
        );
    }
}
