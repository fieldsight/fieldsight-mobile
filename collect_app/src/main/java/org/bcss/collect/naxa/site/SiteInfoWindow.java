package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;

public class SiteInfoWindow extends InfoWindow {


    public SiteInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }


    @Override
    public void onOpen(Object item) {

        SiteMarker marker = (SiteMarker) item;
        Site site = marker.getSite();

        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.bubble_layout);
        Button btnMoreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
        TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
        TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
        TextView txtSubdescription = (TextView) mView.findViewById(R.id.bubble_subdescription);

        txtTitle.setText(site.getName());
        txtDescription.setText(site.getIdentifier());
        txtSubdescription.setText(site.getAddress());

        btnMoreInfo.setOnClickListener(v -> FragmentHostActivity.start(Collect.getInstance().getApplicationContext(),site));

    }

    @Override
    public void onClose() {

    }


}
