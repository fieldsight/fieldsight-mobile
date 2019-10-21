package org.fieldsight.naxa.site;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.login.model.Project;
import org.odk.collect.android.geo.MapboxUtils;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class ProjectMapFragment extends Fragment {

    public static ProjectMapFragment newInstance(Project project) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, project);
        ProjectMapFragment siteListFragment = new ProjectMapFragment();
        siteListFragment.setArguments(bundle);
        return siteListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxUtils.initMapbox();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_map, container, false);
        MapView mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

            }
        });
        return view;
    }

}
