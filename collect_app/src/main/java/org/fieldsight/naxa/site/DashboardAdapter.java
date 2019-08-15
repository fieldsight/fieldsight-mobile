package org.fieldsight.naxa.site;


/**
 * Created on 12/13/17
 * by nishon.tan@gmail.com
 */


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class DashboardAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public DashboardAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragmentList(ArrayList<Fragment> fragmentList) {
        this.fragmentList = fragmentList;

    }


    @Override
    public Fragment getItem(int position) {

        return (fragmentList.get(position));
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}

