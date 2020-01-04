package org.qpython.qpy.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.google.common.collect.Lists;

import org.qpython.qpy.main.widget.WrapContentHeightViewPager;

import java.util.List;
import java.util.Locale;


public class ViewPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {

    private List<T> fragments;
    private int mCurrentPosition = -1;

    public ViewPagerAdapter(FragmentManager fm, List<T> fragments) {
        super(fm);
//        if (isRTL()) {
//            this.fragments = Lists.reverse(fragments);
//            return;
//        }
        this.fragments = fragments;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            if (container instanceof WrapContentHeightViewPager) {
                WrapContentHeightViewPager pager = (WrapContentHeightViewPager) container;
                if (fragment != null && fragment.getView() != null) {
                    mCurrentPosition = position;
                    pager.measureCurrentView(fragment.getView());
                }
            }
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public T getItem(int position) {
        return fragments.get(position);
    }

//    private boolean isRTL() {
//        final int directionality = Character.getDirectionality(Locale.getDefault().getDisplayName().charAt(0));
//        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
//                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
//    }
}
