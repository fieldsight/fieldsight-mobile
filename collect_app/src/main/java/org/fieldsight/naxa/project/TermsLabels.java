package org.fieldsight.naxa.project;

import org.json.JSONObject;

public class TermsLabels {
    public String region_supervisor,
            region,
            site,
            site_supervisor,
            site_reviewer,
            region_reviewer,
            donor;

    private TermsLabels(String region_supervisor, String region, String site, String site_supervisor, String site_reviewer, String region_reviewer, String donor) {
        this.region_supervisor = region_supervisor;
        this.region = region;
        this.site = site;
        this.site_supervisor = site_supervisor;
        this.site_reviewer = site_reviewer;
        this.region_reviewer = region_reviewer;
        this.donor = donor;
    }

    public static TermsLabels fromJSON(JSONObject jsonObject) {
        return new TermsLabels(
                jsonObject.optString("region_supervisor"),
                jsonObject.optString("region"),
                jsonObject.optString("site"),
                jsonObject.optString("site_supervisor"),
                jsonObject.optString("site_reviewer"),
                jsonObject.optString("region_reviewer"),
                jsonObject.optString("donor")
        );
    }
}
