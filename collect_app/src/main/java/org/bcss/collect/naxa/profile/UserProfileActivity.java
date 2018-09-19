package org.bcss.collect.naxa.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends CollectAbstractActivity {
    @BindView(R.id.map_toolbar)
    MapView mapToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        ButterKnife.bind(this);

        mapToolbar.setTileSource(TileSourceFactory.MAPNIK);
        mapToolbar.setBuiltInZoomControls(true);
        IMapController mapController = mapToolbar.getController();
        mapController.setZoom(10);
        mapController.setCenter(new GeoPoint(27.7,85.33));

    }
}
