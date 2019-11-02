package org.fieldsight.naxa.site.map;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.SiteInfoWindow;
import org.fieldsight.naxa.site.SiteMarker;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.fragments.OsmMapFragment;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class ProjectMapFragment extends OsmMapFragment {

    private Project loadedProject;
    private MapView map;
    private Site loadedSite;

    private final Handler handler = new Handler();


    public static ProjectMapFragment newInstance(Project project) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, project);
        ProjectMapFragment siteListFragment = new ProjectMapFragment();
        siteListFragment.setArguments(bundle);
        return siteListFragment;
    }


    public static ProjectMapFragment newInstance(Site site) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, site);
        ProjectMapFragment siteListFragment = new ProjectMapFragment();
        siteListFragment.setArguments(bundle);
        return siteListFragment;
    }

    private void loadSites(String projectId) {
        SiteLocalSource.getInstance().getById(projectId).observe(requireActivity(), sites -> {
            Timber.i("Plotting %d sites on map", sites.size());
            Observable.just(sites)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
                    .filter(site -> {
                        boolean cantParseLocation = TextUtils.isEmpty(site.getLatitude())
                                && TextUtils.isEmpty(site.getLatitude());
                        boolean isBadValue = TextUtils.equals(site.getLatitude(), "0") &&
                                TextUtils.equals(site.getLongitude(), "0");
                        return !cantParseLocation && !isBadValue;
                    })
                    .map(new Function<Site, IGeoPoint>() {
                        @Override
                        public IGeoPoint apply(Site site) {
                            return new SiteGeoPoint(Double.parseDouble(site.getLatitude()),
                                    Double.parseDouble(site.getLongitude())
                                    , site.getName(), site);
                        }
                    })
                    .toList()
                    .subscribe(new SingleObserver<List<IGeoPoint>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<IGeoPoint> points) {

                            SimplePointTheme pt = new SimplePointTheme(points, true);

                            Paint textStyle = new Paint();
                            textStyle.setStyle(Paint.Style.FILL);
                            textStyle.setColor(Color.parseColor("#0000ff"));
                            textStyle.setTextAlign(Paint.Align.CENTER);
                            textStyle.setTextSize(24);

                            SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                                    .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                                    .setMaxNShownLabels(2)
                                    .setMinZoomShowLabels(18)
                                    .setRadius(7)
                                    .setIsClickable(true)
                                    .setCellSize(15)
                                    .setTextStyle(textStyle);


                            final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);

                            map.zoomToBoundingBox(sfpo.getBoundingBox(), true);

                            sfpo.setOnClickListener((points1, point) -> {
                                Site site = ((SiteGeoPoint) points1.get(point)).getSite();
                                infoWindowOnFastOverlay(site);

                            });
                            map.getOverlays().add(sfpo);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

        });
    }

    private void setupInfoDialogSettings() {

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                InfoWindow.closeAllInfoWindowsOn(map);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });

        map.getOverlays().add(0, mapEventsOverlay);

    }

    private void infoWindowOnFastOverlay(Site site) {
        GeoPoint geoPoint = new GeoPoint(Double.parseDouble(site.getLatitude()), Double.parseDouble(site.getLongitude()));
        SiteMarker marker = getMarker(geoPoint, site.getName(), site.getAddress(), null);
        InfoWindow infoWindow = new SiteInfoWindow(R.layout.site_bubble, map);
        marker.setSite(site);
        marker.setInfoWindow(infoWindow);
        marker.setSubDescription(site.getId());

        InfoWindow.closeAllInfoWindowsOn(map);

        marker.showInfoWindow();
        map.getController().animateTo(marker.getPosition());
        handler.postDelayed(marker::showInfoWindow, 500);
    }

    private SiteMarker mapSiteToMarker(Site site) {
        GeoPoint geoPoint = new GeoPoint(Double.parseDouble(site.getLatitude()), Double.parseDouble(site.getLongitude()));
        SiteMarker marker = getMarker(geoPoint, site.getName(), site.getAddress(), ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_blue));
        InfoWindow infoWindow = new SiteInfoWindow(R.layout.site_bubble, map);
        marker.setSite(site);
        marker.setInfoWindow(infoWindow);
        marker.setSubDescription(site.getId());
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            InfoWindow.closeAllInfoWindowsOn(map);
            mapView.getController().animateTo(marker1.getPosition());
            handler.postDelayed(marker1::showInfoWindow, 500);
            return true;
        });

        return marker;

    }

    private SiteMarker getMarker(GeoPoint geoPoint, String title, String snippet, Drawable icon) {
        SiteMarker marker = new SiteMarker(map);
        marker.setSnippet(snippet);
        marker.setTitle(title);
        if(icon != null){
            marker.setIcon(icon);
        }
        marker.setPosition(geoPoint);
        return marker;
    }

    private void setupCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Object obj = getArguments().getParcelable(EXTRA_OBJECT);
        if (obj instanceof Project) {
            loadedProject = (Project) obj;
        } else if (obj instanceof Site) {
            loadedSite = (Site) obj;
        }


        getMapAsync(map -> {
            this.map = map;
            setupInfoDialogSettings();
            setupCompass();
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            if (loadedProject != null) {
                String projectId = loadedProject.getId();
                loadSites(projectId);

            } else if (loadedSite != null) {
                SiteMarker marker = mapSiteToMarker(loadedSite);
                map.getOverlays().add(marker);
                map.getController().zoomTo(15);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        map.getController().animateTo(marker.getPosition());

                    }
                }, 500);


            }

        });

        return super.onCreateView(inflater, container, savedInstanceState);

    }
}
