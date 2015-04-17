package com.soco.SoCoClient.view.ui.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class SingleProjectTabsPagerAdapter extends FragmentPagerAdapter {

    String tag = "SingleProjectTabsPagerAdapter";

    public SingleProjectTabsPagerAdapter(FragmentManager fm) {
        super(fm);
        Log.i(tag, "adapter init");
    }

    @Override
    public Fragment getItem(int index) {
        Log.d(tag, "get item from index " + index);
        switch (index) {
            case 0:
                return new ProjectDetailsFragment();
            case 1:
                return new ProjectUpdatesFragment();
            case 2:
                return new ProjectMembersFragment();
            case 3:
                return new ProjectResourcesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }

}