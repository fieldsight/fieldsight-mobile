package org.bcss.collect.naxa.profile;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends CollectAbstractActivity {
    @BindView(R.id.map_toolbar)
    MapView mapToolbar;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.root)
    ConstraintLayout root;

    boolean set = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
        ButterKnife.bind(this);

        mapToolbar.setTileSource(TileSourceFactory.MAPNIK);
        mapToolbar.setBuiltInZoomControls(true);
        IMapController mapController = mapToolbar.getController();
        mapController.setZoom(10);
        mapController.setCenter(new GeoPoint(27.7, 85.33));
        addAnimationOperations();

    }

    private void addAnimationOperations() {
        ConstraintSet constraintSet1 = new ConstraintSet();
        constraintSet1.clone(root);

        ConstraintSet constraintSet2 = new ConstraintSet();
        constraintSet2.clone(this, R.layout.user_profile_activity_alt);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(root);
                    ConstraintSet constraintSet;
                    if (set) {
                        constraintSet = constraintSet1;
                    } else {
                        constraintSet = constraintSet2;
                    }
                    constraintSet.applyTo(root);
                    set = !set;
                }

            }
        });
    }
}
