package org.bcss.collect.naxa.project;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.spatial.MapHelper;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteViewModel;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class MapFragment extends Fragment implements IRegisterReceiver {

    @BindView(R.id.omap)
    MapView map;
    Unbinder unbinder;


    public MyLocationNewOverlay myLocationOverlay;
    private Project loadedProject;


    private MapHelper helper;
    private final Handler handler = new Handler();
    private Marker marker;
    private GeoPoint latLng;
    private ArrayList<GeoPoint> plottedSites = new ArrayList<>(0);

    public static MapFragment getInstance(Project loadedProject) {
        MapFragment frag = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedProject);
        frag.setArguments(bundle);
        return frag;
    }


    private Marker getMarker(GeoPoint geoPoint, String title, String snippet) {
        Marker marker = new Marker(map);
        marker.setSnippet(snippet);
        marker.setTitle(title);
        marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_place));
        marker.setPosition(geoPoint);
        return marker;
    }

    private void plotSite(Site site) {
        GeoPoint geoPoint = new GeoPoint(Double.parseDouble(site.getLatitude()), Double.parseDouble(site.getLongitude()));
        Marker marker = getMarker(geoPoint, site.getName(), site.getAddress());
        plottedSites.add(geoPoint);
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

        marker = new Marker(map);
        marker.setSnippet(loadedProject.getName());
        marker.setTitle(loadedProject.getOrganizationName());

        marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.circle_blue));
        latLng = new GeoPoint(Double.parseDouble(loadedProject.getLat()), Double.parseDouble(loadedProject.getLon()));
        marker.setPosition(latLng);

        map.getOverlays().add(marker);
        zoomToPoint();


        if (helper == null) {
            // For testing:
            helper = new MapHelper(this.getContext(), map, this);

            map.setMultiTouchControls(true);
            map.setBuiltInZoomControls(true);
            map.setTilesScaledToDpi(true);
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                GeoPoint point = new GeoPoint(34.08145, -39.85007);
                map.getController().setZoom(4);
                map.getController().setCenter(point);
            }
        }, 100);


        myLocationOverlay = new MyLocationNewOverlay(map);

        LiveData<List<Site>> livedata = new SiteViewModel(Collect.getInstance())
                .getSiteByProject(loadedProject);

        Publisher<List<Site>> pub = LiveDataReactiveStreams.toPublisher(this, livedata);
        io.reactivex.Observable.fromPublisher(pub)
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites -> sites)
                .subscribe(new DisposableObserver<Site>() {
                    @Override
                    public void onNext(Site site) {
                        plotSite(site);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

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
        helper.showLayersDialog(this.getContext());
    }

    private void zoomToPoint() {
        if (latLng != null) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    map.getController().setZoom(16);
                    map.getController().setCenter(latLng);
                    map.invalidate();
                }
            }, 200);
        }

    }


}
