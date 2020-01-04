package org.qpython.qpy.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quseit.util.ImageDownLoader;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemCourseBinding;
import org.qpython.qpy.main.activity.CourseIndexFundingActivity;
import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpy.main.server.model.CourseModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Course adapter
 * Created by Hmei on 2017-06-29.
 */

public class CourseAdapter extends RecyclerView.Adapter<MyViewHolder<ItemCourseBinding>> {

    private List<CourseModel> dataList;
    private Activity          context;
    private List<View> childViews = new ArrayList<>();

    public CourseAdapter(Activity context, List<CourseModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder<ItemCourseBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_course, parent, false);
        MyViewHolder<ItemCourseBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemCourseBinding> holder, int position) {
        CourseModel item = dataList.get(position);
        ItemCourseBinding binding = holder.getBinding();
        binding.setCourse(item);

        View.OnClickListener listener = v -> {
            if (item.getOpen() == 0) {
                // Open == 0 -> Not release yet
                QWebViewActivity.start(context, item.getTitle(), item.getLink());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    initChildViews((ViewGroup) binding.getRoot());
                    Pair<View, String>[] pairs = new Pair[childViews.size()];
                    for (int i = 0; i < childViews.size(); i++) {
                        pairs[i] = new Pair<>(childViews.get(i), childViews.get(i).getTransitionName());
                    }
                    CourseIndexFundingActivity.start(context, pairs, item, item.getCrowdfunding());
                } else {
                    CourseIndexFundingActivity.start(context, item, item.getCrowdfunding());
                }
            }
        };

        binding.tvLevel.setText(context.getString(R.string.level, item.getLevel() + ""));
        if (item.getOpen() == 0) {
            binding.free.setVisibility(View.GONE);
        } else {
            binding.free.setVisibility(View.VISIBLE);
            if (item.getCrowdfunding() == 0) {
                binding.free.setBackgroundColor(context.getResources().getColor(R.color.theme_green));
                binding.free.setText(R.string.free);
            } else {
                // 众筹中
                binding.free.setBackgroundColor(context.getResources().getColor(R.color.theme_yellow));
                binding.free.setText(R.string.funding);
                binding.free.setOnClickListener(listener);
            }
        }

        ImageDownLoader.setImageFromUrl(context, binding.ivTheme, item.getLogo());
        binding.getRoot().setOnClickListener(listener);
    }

    private void initChildViews(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                initChildViews((ViewGroup) view);
            } else {
                childViews.add(view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
