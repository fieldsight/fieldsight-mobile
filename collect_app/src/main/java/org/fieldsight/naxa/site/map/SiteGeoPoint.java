package org.fieldsight.naxa.site.map;

import org.fieldsight.naxa.login.model.Site;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

public class SiteGeoPoint extends LabelledGeoPoint {
    private final Site site;

    public SiteGeoPoint(double aLatitude, double aLongitude, String aLabel, Site site) {
        super(aLatitude, aLongitude, aLabel);
        this.site = site;
    }

    public Site getSite() {
        return site;
    }
}
