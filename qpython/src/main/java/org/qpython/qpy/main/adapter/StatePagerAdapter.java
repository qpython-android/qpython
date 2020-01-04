package org.qpython.qpy.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.qpython.qpy.main.fragment.FileFragment;


public class StatePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private FileFragment projectFiles = FileFragment.newInstance(FileFragment.PROJECT);
    private FileFragment scriptFiles  = FileFragment.newInstance(FileFragment.SCRIPT);

    public StatePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return projectFiles;
            case 1:
                return scriptFiles;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
