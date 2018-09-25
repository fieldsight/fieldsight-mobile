package org.bcss.collect.naxa.site;

import android.content.Context;

import org.bcss.collect.naxa.login.model.Site;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class SiteMarker extends Marker {

    private Site site;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public SiteMarker(MapView mapView) {
        super(mapView);
    }

    public SiteMarker(MapView mapView, Context resourceProxy) {
        super(mapView, resourceProxy);
    }
}
