package org.fieldsight.naxa.educational;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by susan on 7/7/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;
    /**
     * @param fm
     * @param fragments
     */
    public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    /* (non-Javadoc)
     * @see androidx.fragment.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    /* (non-Javadoc)
     * @see androidx.viewpager.widget.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }
}