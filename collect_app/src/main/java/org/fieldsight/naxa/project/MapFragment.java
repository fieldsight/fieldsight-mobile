package org.fieldsight.naxa.project;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.spatial.MapHelper;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.SiteInfoWindow;
import org.fieldsight.naxa.site.SiteMarker;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.db.SiteViewModel;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class MapFragment extends Fragment implements IRegisterReceiver {

    @BindView(R.id.omap)
    MapView map;
    Unbinder unbinder;


    public MyLocationNewOverlay myLocationOverlay;
    private Project loadedProject;


    private MapHelper helper;
    private final Handler handler = new Handler();

    private ArrayList<GeoPoint> plottedSites = new ArrayList<>(0);

    public static MapFragment getInstance(Project loadedProject) {
        MapFragment frag = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedProject);
        frag.setArguments(bundle);
        return frag;
    }


    private SiteMarker getMarker(GeoPoint geoPoint, String title, String snippet) {
        SiteMarker marker = new SiteMarker(map);
        marker.setSnippet(snippet);
        marker.setTitle(title);
        marker.setIcon(ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.ic_place_black));
        marker.setPosition(geoPoint);
        return marker;
    }

    private void plotSite(Site site) {
        GeoPoint geoPoint = new GeoPoint(Double.parseDouble(site.getLatitude()), Double.parseDouble(site.getLongitude()));
        SiteMarker marker = getMarker(geoPoint, site.getName(), site.getAddress());
        plottedSites.add(geoPoint);
        InfoWindow infoWindow = new SiteInfoWindow(R.layout.site_bubble, map);
        marker.setSite(site);
        marker.setInfoWindow(infoWindow);
        marker.setSubDescription(site.getId());
        map.getOverlays().add(marker);

    }

    private BoundingBox generateSitesBoundingBox(ArrayList<GeoPoint> geoPoints) {
        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLong = Integer.MAX_VALUE;
        double maxLong = Integer.MIN_VALUE;

        for (GeoPoint point : geoPoints) {
            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }

        return new BoundingBox(maxLat, maxLong, minLat, minLong);

    }


    @SuppressLint("CheckResult")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_map, container, false);

        unbinder = ButterKnife.bind(this, view);
        loadedProject = getArguments().getParcelable(EXTRA_OBJECT);


        if (helper == null) {

            helper = new MapHelper(this.getContext(), map, this, 0);
            map.setMultiTouchControls(true);
            map.setBuiltInZoomControls(true);
            map.setTilesScaledToDpi(true);

        }

        handler.postDelayed(() -> map.getController().setZoom(4), 100);


        myLocationOverlay = new MyLocationNewOverlay(map);




        SiteLocalSource.getInstance().getByIdAsSingle(loadedProject.getId())
                .toObservable()
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites -> sites)
                .flatMap((Function<Site, ObservableSource<GeoPoint>>) site -> {
                    GeoPoint geoPoint = new GeoPoint(Double.parseDouble(site.getLatitude()), Double.parseDouble(site.getLongitude()));

                    plotSite(site);
                    return Observable.just(geoPoint);

                })
                .toList()
                .map(geoPoints -> {
                    return generateSitesBoundingBox((ArrayList<GeoPoint>) geoPoints);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<BoundingBox>() {
                    @Override
                    public void onSuccess(BoundingBox boundingBox) {

                        if (map != null) {
                            map.zoomToBoundingBox(boundingBox, true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        SnackBarUtils.showErrorFlashbar(getActivity(), "Failed to plot on map");
                        e.printStackTrace();
                    }
                });


        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), new MapEventsReceiver() {
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


        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {

    }

    @Override
    public void destroy() {

    }

    @OnClick(R.id.layer_menu)
    public void onViewClicked() {
        helper.showLayersDialog();
    }


}
