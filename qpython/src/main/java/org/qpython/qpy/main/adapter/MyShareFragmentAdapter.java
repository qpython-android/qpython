package org.qpython.qpy.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.qpython.qpy.main.fragment.MyGistFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名: MyShareFragmentAdapter
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/26 14:58
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class MyShareFragmentAdapter extends FragmentPagerAdapter {

    public static final String[] titles     = new String[]{"My Gists","My Stars"};
    public List<MyGistFragment>  mFragments = new ArrayList<>();

    public MyShareFragmentAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        for (int i=0;i<titles.length;i++){
            mFragments.add(MyGistFragment.newInstance(i));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
