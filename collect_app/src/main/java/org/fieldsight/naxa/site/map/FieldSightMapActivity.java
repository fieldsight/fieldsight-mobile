/*
 * Copyright (C) 2011 University of Washington
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

package org.fieldsight.naxa.site.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.activities.BaseGeoMapActivity;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.geo.MapFragment;
import org.odk.collect.android.geo.MapPoint;
import org.odk.collect.android.geo.MapProvider;
import org.odk.collect.android.preferences.MapsPreferences;
import org.odk.collect.android.utilities.GeoUtils;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.widgets.GeoPointWidget;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.android.utilities.PermissionUtils.areLocationPermissionsGranted;

/**
 * A modified version of org.odk.collect.android.activities.GeoPointMapActivity
 */
public class FieldSightMapActivity extends BaseGeoMapActivity {
    public static final String MAP_CENTER_KEY = "map_center";
    public static final String MAP_ZOOM_KEY = "map_zoom";
    public static final String POINT_KEY = "point";

    public static final String IS_DRAGGED_KEY = "is_dragged";
    public static final String CAPTURE_LOCATION_KEY = "capture_location";
    public static final String FOUND_FIRST_LOCATION_KEY = "found_first_location";
    public static final String SET_CLEAR_KEY = "set_clear";
    public static final String POINT_FROM_INTENT_KEY = "point_from_intent";
    public static final String INTENT_READ_ONLY_KEY = "intent_read_only";
    public static final String INTENT_DRAGGABLE_KEY = "intent_draggable";
    public static final String IS_POINT_LOCKED_KEY = "is_point_locked";

    public static final String PLACE_MARKER_BUTTON_ENABLED_KEY = "place_marker_button_enabled";
    public static final String ZOOM_BUTTON_ENABLED_KEY = "zoom_button_enabled";


    private MapFragment map;
    private int featureId = -1;  // will be a positive featureId once map is ready


    private MapPoint location;


    private boolean isDragged;

    private ImageButton zoomButton;


    private boolean captureLocation;
    private boolean foundFirstLocation;

    /**
     * True if a tap on the clear button removed an existing marker and
     * no new marker has been placed.
     */
    private boolean setClear;

    /**
     * True if the current point came from the intent.
     */
    private boolean pointFromIntent;

    /**
     * True if the intent requested for the point to be read-only.
     */
    private boolean intentReadOnly;

    /**
     * True if the intent requested for the marker to be draggable.
     */
    private boolean intentDraggable;

    /**
     * While true, the point cannot be moved by dragging or long-pressing.
     */
    private boolean isPointLocked;

    private final HashMap<String, Site> featureIdAndSite = new HashMap<>();

    public static void start(Context context, Site loadedSite) {


        Intent intent = new Intent(context, FieldSightMapActivity.class);
        intent.putExtra(EXTRA_OBJECT, loadedSite);
        context.startActivity(intent);

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!areLocationPermissionsGranted(this)) {
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            setContentView(R.layout.fieldsight_map_activity);
        } catch (NoClassDefFoundError e) {
            Timber.e(e, "Google maps not accessible due to: %s ", e.getMessage());
            ToastUtils.showShortToast(R.string.google_play_services_error_occured);
            finish();
            return;
        }


        zoomButton = findViewById(R.id.zoom);

        Context context = getApplicationContext();
        MapProvider.createMapFragment(context)
                .addTo(this, R.id.map_container, this::initMap, this::finish);


    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (map == null) {
            // initMap() is called asynchronously, so map can be null if the activity
            // is stopped (e.g. by screen rotation) before initMap() gets to run.
            // In this case, preserve any provided instance state.
            if (previousState != null) {
                state.putAll(previousState);
            }
            return;
        }
        state.putParcelable(MAP_CENTER_KEY, map.getCenter());
        state.putDouble(MAP_ZOOM_KEY, map.getZoom());
        state.putParcelable(POINT_KEY, map.getMarkerPoint(featureId));

        // Flags
        state.putBoolean(IS_DRAGGED_KEY, isDragged);
        state.putBoolean(CAPTURE_LOCATION_KEY, captureLocation);
        state.putBoolean(FOUND_FIRST_LOCATION_KEY, foundFirstLocation);
        state.putBoolean(SET_CLEAR_KEY, setClear);
        state.putBoolean(POINT_FROM_INTENT_KEY, pointFromIntent);
        state.putBoolean(INTENT_READ_ONLY_KEY, intentReadOnly);
        state.putBoolean(INTENT_DRAGGABLE_KEY, intentDraggable);
        state.putBoolean(IS_POINT_LOCKED_KEY, isPointLocked);

        // UI state

        state.putBoolean(ZOOM_BUTTON_ENABLED_KEY, zoomButton.isEnabled());


    }

    public void returnLocation() {
        String result = null;

        if (setClear || (intentReadOnly && featureId == -1)) {
            result = "";
        } else if (isDragged || intentReadOnly || pointFromIntent) {
            result = formatResult(map.getMarkerPoint(featureId));
        } else if (location != null) {
            result = formatResult(location);
        }

        if (result != null) {
            setResult(RESULT_OK, new Intent().putExtra(FormEntryActivity.LOCATION_RESULT, result));
        }
        finish();
    }

    @SuppressLint("MissingPermission") // Permission handled in Constructor
    public void initMap(MapFragment newMapFragment) {
        map = newMapFragment;
        map.setDragEndListener(this::onDragEnd);


        // Focuses on marked location
        zoomButton.setEnabled(false);
        zoomButton.setOnClickListener(v -> map.zoomToPoint(map.getGpsLocation(), true));

        // Menu Layer Toggle
        findViewById(R.id.layer_menu).setOnClickListener(v -> {
            MapsPreferences.showReferenceLayerDialog(this);
        });


        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            intentDraggable = intent.getBooleanExtra(GeoPointWidget.DRAGGABLE_ONLY, false);
            if (!intentDraggable) {
                // Not Draggable, set text for Map else leave as placement-map text

            }

            intentReadOnly = intent.getBooleanExtra(GeoPointWidget.READ_ONLY, false);
            if (intentReadOnly) {
                captureLocation = true;

            }


        }

        map.setGpsLocationListener(this::onLocationChanged);
        map.setGpsLocationEnabled(true);

        if (previousState != null) {
            restoreFromInstanceState(previousState);
        }

        loadSites("183");
        map.setClickListener(new MapFragment.PointListener() {
            @Override
            public void onPoint(@NonNull MapPoint point) {
                String index = point.lat + ":" + point.lon;
                ToastUtils.showLongToast(featureIdAndSite.get(index).getName());
            }
        });

    }

    private void loadSites(String projectId) {
        SiteLocalSource.getInstance().getByIdAsSingle(projectId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        featureIdAndSite.clear();
                    }
                })
                .flattenAsObservable(new Function<List<Site>, Iterable<Site>>() {
                    @Override
                    public Iterable<Site> apply(List<Site> sites) throws Exception {
                        return sites;
                    }
                })
                .filter(site -> !TextUtils.isEmpty(site.getLatitude())
                        && !TextUtils.isEmpty(site.getLatitude()))
                .flatMap(new Function<Site, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Site site) throws Exception {
                        MapPoint mapPoint = new MapPoint(Double.parseDouble(site.getLatitude()),
                                Double.parseDouble(site.getLongitude()));
                        return Observable.just(mapPoint)
                                .doOnNext(new Consumer<MapPoint>() {
                                    @Override
                                    public void accept(MapPoint mapPoint) throws Exception {
                                        featureId = placeMarker(mapPoint);
                                        featureIdAndSite.put(mapPoint.lat + ":" + mapPoint.lon, site);
                                    }
                                });
                    }
                })
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected void restoreFromInstanceState(Bundle state) {
        isDragged = state.getBoolean(IS_DRAGGED_KEY, false);
        captureLocation = state.getBoolean(CAPTURE_LOCATION_KEY, false);
        foundFirstLocation = state.getBoolean(FOUND_FIRST_LOCATION_KEY, false);
        setClear = state.getBoolean(SET_CLEAR_KEY, false);
        pointFromIntent = state.getBoolean(POINT_FROM_INTENT_KEY, false);
        intentReadOnly = state.getBoolean(INTENT_READ_ONLY_KEY, false);
        intentDraggable = state.getBoolean(INTENT_DRAGGABLE_KEY, false);
        isPointLocked = state.getBoolean(IS_POINT_LOCKED_KEY, false);

        // Restore the marker and dialog after the flags, because they use some of them.
        MapPoint point = state.getParcelable(POINT_KEY);
        if (point != null) {
            placeMarker(point);
        } else {
            clear();
        }

        // Restore the flags again, because placeMarker() and clear() modify some of them.
        isDragged = state.getBoolean(IS_DRAGGED_KEY, false);
        captureLocation = state.getBoolean(CAPTURE_LOCATION_KEY, false);
        foundFirstLocation = state.getBoolean(FOUND_FIRST_LOCATION_KEY, false);
        setClear = state.getBoolean(SET_CLEAR_KEY, false);
        pointFromIntent = state.getBoolean(POINT_FROM_INTENT_KEY, false);
        intentReadOnly = state.getBoolean(INTENT_READ_ONLY_KEY, false);
        intentDraggable = state.getBoolean(INTENT_DRAGGABLE_KEY, false);
        isPointLocked = state.getBoolean(IS_POINT_LOCKED_KEY, false);

        // Restore the rest of the UI state.
        MapPoint mapCenter = state.getParcelable(MAP_CENTER_KEY);
        Double mapZoom = state.getDouble(MAP_ZOOM_KEY);
        if (mapCenter != null) {
            map.zoomToPoint(mapCenter, mapZoom, false);
        }


        zoomButton.setEnabled(state.getBoolean(ZOOM_BUTTON_ENABLED_KEY, false));


    }

    public void onLocationChanged(MapPoint point) {


        MapPoint previousLocation = this.location;
        this.location = point;

        if (point != null) {
            if (previousLocation != null) {
                enableZoomButton(true);

                if (!captureLocation && !setClear) {
                    placeMarker(point);

                }

                if (!foundFirstLocation) {
                    map.zoomToPoint(map.getGpsLocation(), true);
                    foundFirstLocation = true;
                }


            }
        }
    }

    public String formatResult(MapPoint point) {
        return String.format("%s %s %s %s", point.lat, point.lon, point.alt, point.sd);
    }

    public String formatLocationStatus(String provider, double sd) {
        return getString(
                R.string.location_provider_accuracy,
                GeoUtils.capitalizeGps(provider),
                new DecimalFormat("#.##").format(sd)
        );
    }

    public void onDragEnd(int draggedFeatureId) {
        if (draggedFeatureId == featureId) {
            isDragged = true;
            captureLocation = true;
            setClear = false;
            map.setCenter(map.getMarkerPoint(featureId), true);
        }
    }


    private void enableZoomButton(boolean shouldEnable) {
        if (zoomButton != null) {
            zoomButton.setEnabled(shouldEnable);
        }
    }

    public void zoomToMarker(boolean animate) {
        map.zoomToPoint(map.getMarkerPoint(featureId), animate);
    }

    private void clear() {
        map.clearFeatures();
        featureId = -1;

        isPointLocked = false;
        isDragged = false;
        captureLocation = false;
        setClear = true;
    }

    private int placeMarker(MapPoint point) {
        featureId = map.addMarker(point, intentDraggable && !intentReadOnly && !isPointLocked);
        return featureId;
    }

    public void setCaptureLocation(boolean captureLocation) {
        this.captureLocation = captureLocation;
    }


    @VisibleForTesting
    public MapFragment getMapFragment() {
        return map;
    }
}
