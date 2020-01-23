package org.fieldsight.naxa.v3.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.map.ProjectMapFragment;

import java.util.List;

public class ProjectViewPagerAdapter extends FragmentStatePagerAdapter {
    List<ProjectDashboardActivity.ProjectFragment> projectFragmentList;

    public ProjectViewPagerAdapter(FragmentManager fm, List<ProjectDashboardActivity.ProjectFragment> projectFragmentList) {
        super(fm);
        this.projectFragmentList = projectFragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return projectFragmentList.get(position).fragment;
    }

    @Override
    public int getCount() {
        return projectFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return projectFragmentList.get(position).title;
    }
}
