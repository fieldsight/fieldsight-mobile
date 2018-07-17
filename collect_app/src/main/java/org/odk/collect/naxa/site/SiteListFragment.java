package org.odk.collect.naxa.site;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;
import org.odk.collect.naxa.login.model.Project;

public class SiteListFragment extends Fragment {

    public static SiteListFragment getInstance(String projectId, Project loadedProject) {
        return new SiteListFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);
        bindUI(view);

        return view;
    }

    private void bindUI(View view) {

    }
}
