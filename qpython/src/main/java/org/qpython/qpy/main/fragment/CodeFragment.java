package org.qpython.qpy.main.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.GistBase;
import org.qpython.qpy.databinding.FragmentRvBinding;
import org.qpython.qpy.main.adapter.CodeShareAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CodeFragment extends Fragment {
    public static final  String PROGRAM    = "program";
    public static final  String SCRIPT     = "script";
    public static final  String MY_PROGRAM = "my_program";
    public static final  String MY_SCRIPT  = "my_script";
    private static final String TAG        = "TAG";
    private FragmentRvBinding binding;
    private String            tag;

    public static CodeFragment newInstance(String tag) {
        CodeFragment myFragment = new CodeFragment();

        Bundle args = new Bundle();
        args.putString(TAG, tag);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rv, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tag = getArguments().getString(TAG, "");
        initData();
    }

    private void initData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        Action1<List<GistBase>> callback = gistList -> {
            CodeShareAdapter adapter = new CodeShareAdapter(gistList, tag.equals(PROGRAM) || tag.equals(MY_PROGRAM));
            binding.swipeList.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.swipeList.setAdapter(adapter);
            binding.progressBar.setVisibility(View.GONE);
            binding.emptyHint.setVisibility(View.GONE);
        };

        // Dismiss progress bar and show empty msg if no data after 5 seconds
        Observable.just(null)
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> {
                    if (binding.progressBar.getVisibility() == View.VISIBLE) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyHint.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe();

        switch (tag) {
            case PROGRAM:
//                ShareCodeUtil.getInstance().getBaseProjectGistList(callback);
                break;
            case SCRIPT:
//                ShareCodeUtil.getInstance().getBaseScriptGistList(callback);
                break;
            case MY_PROGRAM:
                break;
            case MY_SCRIPT:
                break;
        }
    }
}
