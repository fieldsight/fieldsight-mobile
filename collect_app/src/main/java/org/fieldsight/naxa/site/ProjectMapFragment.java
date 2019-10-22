package org.fieldsight.naxa.site;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.fragments.OsmMapFragment;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class ProjectMapFragment extends OsmMapFragment {

    private Project loadedProject;
    private MapView map;

    public static ProjectMapFragment newInstance(Project project) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, project);
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
                    .filter(site -> !TextUtils.isEmpty(site.getLatitude())
                            && !TextUtils.isEmpty(site.getLatitude()))
                    .doOnNext(site -> map.getOverlays().add(mapSiteToMarker(site)))
                    .map(site -> new GeoPoint(Double.parseDouble(site.getLatitude()),
                            Double.parseDouble(site.getLongitude())))
                    .toList()
                    .map(BoundingBox::fromGeoPoints)
                    .subscribe(new SingleObserver<BoundingBox>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(BoundingBox boundingBox) {
                            map.zoomToBoundingBox(boundingBox, false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e);
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

    private SiteMarker mapSiteToMarker(Site site) {
        GeoPoint geoPoint = new GeoPoint(Double.parseDouble(site.getLatitude()), Double.parseDouble(site.getLongitude()));
        SiteMarker marker = getMarker(geoPoint, site.getName(), site.getAddress());
        InfoWindow infoWindow = new SiteInfoWindow(R.layout.site_bubble, map);
        marker.setSite(site);
        marker.setInfoWindow(infoWindow);
        marker.setSubDescription(site.getId());

       return marker;

    }

    private SiteMarker getMarker(GeoPoint geoPoint, String title, String snippet) {
        SiteMarker marker = new SiteMarker(map);
        marker.setSnippet(snippet);
        marker.setTitle(title);
        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_black));
        marker.setPosition(geoPoint);
        return marker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadedProject = getArguments().getParcelable(EXTRA_OBJECT);

        getMapAsync(map -> {
            ProjectMapFragment.this.map = map;
            setupInfoDialogSettings();
            String projectId = loadedProject.getId();
            loadSites(projectId);
        });

        return super.onCreateView(inflater, container, savedInstanceState);

    }
}
