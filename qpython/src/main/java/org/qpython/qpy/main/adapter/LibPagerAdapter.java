package org.qpython.qpy.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.qpython.qpy.main.fragment.LibAIPyFragment;
import org.qpython.qpy.main.fragment.LibQPyPiFragment;
import org.qpython.qpy.main.fragment.LibProjectFragment;


public class LibPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private LibProjectFragment programFragment = new LibProjectFragment();
    private LibQPyPiFragment   qPyPiFragment   = new LibQPyPiFragment();
    private LibAIPyFragment    aiPyFragment    = new LibAIPyFragment();

    public LibPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return programFragment;
            case 1:
                return qPyPiFragment;
            case 2:
                return aiPyFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
