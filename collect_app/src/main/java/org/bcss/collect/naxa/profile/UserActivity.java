package org.bcss.collect.naxa.profile;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;

public class UserActivity extends CollectAbstractActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if(savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new UserProfileFragment(), "MainFragment");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }
    }
}
