package org.qpython.qpy.plugin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import org.qpython.qpy.R;

import org.qpython.qpy.main.app.App;
import org.qpython.qpy.plugin.fragment.CloudPluginFragment;
import org.qpython.qpy.plugin.fragment.LocalPluginFragment;

import java.util.ArrayList;
import java.util.List;

public class PluginPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles;
    private List<Fragment> mFragments;


    public PluginPagerAdapter(FragmentManager fm) {
        super(fm);

        mTitles = App.getContext().getResources().getStringArray(R.array.plugin_tab);
        mFragments = new ArrayList<>();
        mFragments.add(LocalPluginFragment.newInstance());
        mFragments.add(CloudPluginFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
