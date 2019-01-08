package org.bcss.collect.naxa.project;/*
 * Copyright (C) 2016 GeoODK
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.application.Collect;
import org.odk.collect.android.location.client.LocationClient;
import org.odk.collect.android.location.client.LocationClients;
import org.bcss.collect.android.spatial.MapHelper;
import org.odk.collect.android.utilities.GeoPointUtils;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.widgets.GeoPointWidget;
import org.bcss.collect.naxa.generalforms.data.FormResponse;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.ParseException;

import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

/**
 * Version of the GeoPointMapActivity that uses the new OSMDDroid
 *
 * @author jonnordling@gmail.com
 */
public class MapActivity extends CollectAbstractActivity implements LocationListener,
        Marker.OnMarkerDragListener, MapEventsReceiver, IRegisterReceiver,
        LocationClient.LocationClientListener {

    private static final String LOCATION_COUNT = "locationCount";

    //private GoogleMap map;
    private MapView map;

    private final Handler handler = new Handler();
    private Marker marker;

    private GeoPoint latLng;

    private TextView locationStatus;

    private LocationClient locationClient;

    private Location location;


    private boolean captureLocation;
    private boolean setClear;
    private boolean isDragged;
    private ImageButton showLocationButton;

    private int locationCount = 0;

    private MapHelper helper;

    private AlertDialog zoomDialog;
    private View zoomDialogView;

    private Button zoomPointButton;
    private Button zoomLocationButton;

    public MyLocationNewOverlay myLocationOverlay;

    private boolean readOnly;
    private boolean draggable;
    private boolean intentDraggable;
    private boolean locationFromIntent;
    private int locationCountNum = 0;
    private boolean foundFirstLocation;
    private Site loadedSite;
    private Toolbar toolbar;
    private GeoPoint siteGeoPoint;


    public static void start(Context context, Site loadedSite) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_OBJECT, loadedSite);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (savedInstanceState != null) {
            locationCount = savedInstanceState.getInt(LOCATION_COUNT);
        }

        try {
            setContentView(R.layout.activity_map);
        } catch (NoClassDefFoundError e) {
            ToastUtils.showShortToast(R.string.google_play_services_error_occured);
            finish();
            return;
        }

        map = findViewById(R.id.omap);
        if (helper == null) {
            // For testing:
            helper = new MapHelper(this, map, this);

            map.setMultiTouchControls(true);
            map.setBuiltInZoomControls(true);
            map.setTilesScaledToDpi(true);
        }

        marker = new Marker(map);

        toolbar = findViewById(R.id.toolbar);
        setupToolbar();
        setSiteMarkerOrFail();


        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_place));
        myLocationOverlay = new MyLocationNewOverlay(map);


        locationStatus = findViewById(R.id.location_status);
        locationStatus.setText(getString(R.string.please_wait_long));


        locationClient = LocationClients.clientForContext(this);
        locationClient.setListener(this);



        // Focuses on marked location
        showLocationButton = findViewById(R.id.show_location);
        showLocationButton.setVisibility(View.VISIBLE);
        showLocationButton.setEnabled(false);
        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showZoomDialog();
            }
        });

        // not clickable until we have a marker set....
        showLocationButton.setClickable(false);

        // Menu Layer Toggle
        ImageButton layersButton = findViewById(R.id.layer_menu);
        layersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.showLayersDialog();

            }
        });


        zoomDialogView = getLayoutInflater().inflate(R.layout.geo_zoom_dialog, null);

        zoomLocationButton = zoomDialogView.findViewById(R.id.zoom_location);
        zoomLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomToLocation();
                map.invalidate();
                zoomDialog.dismiss();
            }
        });

        zoomPointButton = zoomDialogView.findViewById(R.id.zoom_saved_location);
        zoomPointButton.setText("Zoom to site");
        zoomPointButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                zoomToPoint();
                map.invalidate();
                zoomDialog.dismiss();
            }
        });


        if (latLng != null) {
            marker.setPosition(latLng);
            map.getOverlays().add(marker);
            map.invalidate();
            captureLocation = true;
            foundFirstLocation = true;
            zoomToPoint();
        }
    }

    private void setSiteMarkerOrFail() {
        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            loadedSite = bundle.getParcelable(EXTRA_OBJECT);
            getSupportActionBar().setTitle(loadedSite.getName());
            handler.postDelayed(new Runnable() {
                public void run() {

                    try {
                         siteGeoPoint = new GeoPoint(Double.parseDouble(loadedSite.getLatitude()), Double.parseDouble(loadedSite.getLongitude()));
                        map.getController().setZoom(4);
                        map.getController().setCenter(siteGeoPoint);
                        marker.setTitle(loadedSite.getName());
                        marker.setSnippet(loadedSite.getAddress());
                        marker.setPosition(siteGeoPoint);
                        map.getOverlays().add(marker);

                    } catch (NumberFormatException e) {
                        ToastUtils.showShortToastInMiddle("Failed to load site marker");
                    }
                }
            }, 100);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.projects);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LOCATION_COUNT, locationCount);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            helper.setBasemap();
        }

    }

    @Override
    protected void onStop() {
        locationClient.stop();
        super.onStop();
    }


    private void upMyLocationOverlayLayers() {
        showLocationButton.setClickable(marker != null);

        // make sure we have a good location provider before continuing
        locationClient.requestLocationUpdates(this);

        if (!locationClient.isLocationAvailable()) {
            showGPSDisabledAlertToUser();

        } else {
            overlayMyLocationLayers();
        }
    }

    private void overlayMyLocationLayers() {
        map.getOverlays().add(myLocationOverlay);
        if (draggable && !readOnly) {
            if (marker != null) {
                marker.setOnMarkerDragListener(this);
                marker.setDraggable(true);
            }

            MapEventsOverlay overlayEvents = new MapEventsOverlay(this);
            map.getOverlays().add(overlayEvents);
        }

        myLocationOverlay.setEnabled(true);
        myLocationOverlay.enableMyLocation();
    }

    private void zoomToPoint() {
        if (siteGeoPoint != null) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    map.getController().setZoom(16);
                    map.getController().setCenter(siteGeoPoint);
                    map.invalidate();
                }
            }, 200);
        }

    }


    private String truncateFloat(float f) {
        return new DecimalFormat("#.##").format(f);
    }


    @Override
    public void onLocationChanged(Location location) {

        this.location = location;

        if (this.location != null) {
            int locationCountFoundLimit = 1;
            if (locationCountNum >= locationCountFoundLimit) {
                showLocationButton.setEnabled(true);
                if (!captureLocation & !setClear) {
                    latLng = new GeoPoint(this.location.getLatitude(), this.location.getLongitude());
                    captureLocation = true;
                }
                if (!foundFirstLocation) {
                    // zoomToPoint();
                    showZoomDialog();
                    foundFirstLocation = true;
                }
                locationStatus.setText(
                        getString(R.string.location_provider_accuracy, GeoPointUtils.capitalizeGps(this.location.getProvider()),
                                truncateFloat(this.location.getAccuracy())));
            } else {
                // Prevent from forever increasing
                if (locationCountNum <= 100) {
                    locationCountNum++;
                }
            }


            //if (location.getLatitude() != marker.getPosition().getLatitude() & location
            // .getLongitude() != marker.getPosition().getLongitude()) {
            //reloadLocationButton.setEnabled(true);
            //}
            //
            //If location is accurate enough, stop updating position and make the marker
            // draggable
            //if (location.getAccuracy() <= mLocationAccuracy) {
            //stopGeolocating();
            //}

        } else {
            Timber.i("onLocationChanged(%d) null location", locationCount);
        }
    }

    @Override
    public void onMarkerDrag(Marker arg0) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latLng = marker.getPosition();
        isDragged = true;
        captureLocation = true;
        setClear = false;
        map.getController().animateTo(latLng);
        map.getController().setZoom(map.getZoomLevel());
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        //stopGeolocating();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        if (marker == null) {
            marker = new Marker(map);

        }
        showLocationButton.setEnabled(true);
        map.invalidate();
        marker.setPosition(geoPoint);
        marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_place_black));
        marker.setDraggable(true);
        latLng = geoPoint;
        isDragged = true;
        setClear = false;
        captureLocation = true;
        map.getOverlays().add(marker);
        return false;
    }

    public void showZoomDialog() {

        if (zoomDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.zoom_to_where));
            builder.setView(zoomDialogView)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.cancel();
                            zoomDialog.dismiss();
                        }
                    });
            zoomDialog = builder.create();
        }
        //If feature enable zoom to button else disable
        if (myLocationOverlay.getMyLocation() != null) {
            zoomLocationButton.setEnabled(true);
            zoomLocationButton.setBackgroundColor(Color.parseColor("#50cccccc"));
            zoomLocationButton.setTextColor(themeUtils.getPrimaryTextColor());
        } else {
            zoomLocationButton.setEnabled(false);
            zoomLocationButton.setBackgroundColor(Color.parseColor("#50e2e2e2"));
            zoomLocationButton.setTextColor(Color.parseColor("#FF979797"));
        }

        if (latLng != null & !setClear) {
            zoomPointButton.setEnabled(true);
            zoomPointButton.setBackgroundColor(Color.parseColor("#50cccccc"));
            zoomPointButton.setTextColor(themeUtils.getPrimaryTextColor());
        } else {
            zoomPointButton.setEnabled(false);
            zoomPointButton.setBackgroundColor(Color.parseColor("#50e2e2e2"));
            zoomPointButton.setTextColor(Color.parseColor("#FF979797"));
        }
        zoomDialog.show();
    }

    private void zoomToLocation() {
        if (myLocationOverlay.getMyLocation() != null) {
            final GeoPoint location = new GeoPoint(this.location.getLatitude(),
                    this.location.getLongitude());
            handler.postDelayed(new Runnable() {
                public void run() {
                    map.getController().setZoom(16);
                    map.getController().setCenter(location);
                    map.invalidate();
                }
            }, 200);
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.gps_enable_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable_gps),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(
                                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onClientStart() {
        upMyLocationOverlayLayers();
    }

    @Override
    public void onClientStartFailure() {
        showGPSDisabledAlertToUser();
    }

    @Override
    public void onClientStop() {

    }

    public AlertDialog getZoomDialog() {
        return zoomDialog;
    }

    /**
     * For testing purposes.
     *
     * @param helper The MapHelper to set.
     */
    public void setHelper(MapHelper helper) {
        this.helper = helper;
    }
}
