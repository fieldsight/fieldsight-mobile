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

public class ProjectViewPagerAdapter extends FragmentStatePagerAdapter {
    static String titles[] = {"Sites", "Users", "Submissions", "Map"};
    Project project;
    public ProjectViewPagerAdapter(FragmentManager fm, Project project) {
       super(fm);
       this.project = project;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
      return SiteListFragment.newInstance(project);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public static String[] getTitles() {
        return titles;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
