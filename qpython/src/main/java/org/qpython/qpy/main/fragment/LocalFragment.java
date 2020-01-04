package org.qpython.qpy.main.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentLocalBinding;
import org.qpython.qpy.main.adapter.StatePagerAdapter;


public class LocalFragment extends Fragment {
    private FragmentLocalBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.project));
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.script));
        binding.tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.colorAccent));
        binding.tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        StatePagerAdapter adapter = new StatePagerAdapter(getActivity().getSupportFragmentManager(), binding.tabs.getTabCount());
        binding.vp.setAdapter(adapter);
        initListener();
    }


    public void initListener() {
        binding.vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
