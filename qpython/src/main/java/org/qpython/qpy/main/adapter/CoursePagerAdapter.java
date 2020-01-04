package org.qpython.qpy.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.qpython.qpy.main.fragment.CourseFragment;
import org.qpython.qpy.main.fragment.LibAIPyFragment;
import org.qpython.qpy.main.fragment.LibProjectFragment;
import org.qpython.qpy.main.fragment.LibQPyPiFragment;


public class CoursePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    private CourseFragment recommend = new CourseFragment();
    private CourseFragment latest    = new CourseFragment();
    private CourseFragment my        = new CourseFragment();

    public CoursePagerAdapter(FragmentManager fm) {
        super(fm);
        this.mNumOfTabs = 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return recommend;
            case 1:
                return latest;
            case 2:
                return my;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
