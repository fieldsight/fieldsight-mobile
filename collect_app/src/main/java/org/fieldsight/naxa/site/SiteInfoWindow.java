package org.fieldsight.naxa.site;

import android.widget.Button;
import android.widget.TextView;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.login.model.Site;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class SiteInfoWindow extends InfoWindow {


    public SiteInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }


    @Override
    public void onOpen(Object item) {

        SiteMarker marker = (SiteMarker) item;
        Site site = marker.getSite();

        Button btnMoreInfo = mView.findViewById(R.id.bubble_moreinfo);
        TextView txtTitle = mView.findViewById(R.id.bubble_title);
        TextView txtDescription = mView.findViewById(R.id.bubble_description);
        TextView txtSubdescription = mView.findViewById(R.id.bubble_subdescription);

        txtTitle.setText(site.getName());
        txtDescription.setText(site.getIdentifier());
        txtSubdescription.setText(site.getAddress());

        btnMoreInfo.setOnClickListener(v -> FragmentHostActivity.start(getView().getContext(),site, false));

    }

    @Override
    public void onClose() {

    }


}
