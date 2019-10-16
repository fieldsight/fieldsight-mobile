package org.fieldsight.naxa.project;

import org.json.JSONObject;

public class TermsLabels {
    public final String regionSupervisor,
            region,
            site,
            siteSupervisor,
            siteReviewer,
            regionReviewer,
            donor;

    private TermsLabels(String regionSupervisor, String region, String site, String siteSupervisor, String siteReviewer, String regionReviewer, String donor) {
        this.regionSupervisor = regionSupervisor;
        this.region = region;
        this.site = site;
        this.siteSupervisor = siteSupervisor;
        this.siteReviewer = siteReviewer;
        this.regionReviewer = regionReviewer;
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
