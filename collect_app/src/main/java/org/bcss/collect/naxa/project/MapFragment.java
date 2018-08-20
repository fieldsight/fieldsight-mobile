package org.bcss.collect.naxa.project;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import org.bcss.collect.android.spatial.MapHelper;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    public static MapFragment getInstance(Project loadedProject) {
        MapFragment frag = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedProject);
        frag.setArguments(bundle);
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_map, container, false);

        unbinder = ButterKnife.bind(this, view);
        loadedProject = getArguments().getParcelable(EXTRA_OBJECT);

        marker = new Marker(map);
        marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_place_black));
        map.getOverlays().add(marker);
        latLng = new GeoPoint(Double.parseDouble(loadedProject.getLat()), Double.parseDouble(loadedProject.getLon()));
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
