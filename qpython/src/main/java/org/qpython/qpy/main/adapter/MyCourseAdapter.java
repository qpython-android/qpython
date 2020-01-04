package org.qpython.qpy.main.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quseit.util.ImageDownLoader;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemMyCourseBinding;
import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.model.MyCourse;

import java.util.List;

/**
 * Course adapter
 * Created by Hmei on 2017-06-29.
 */

public class MyCourseAdapter extends RecyclerView.Adapter<MyViewHolder<ItemMyCourseBinding>> {

    private List<MyCourse.DataBean> dataList;
    private Activity                         context;

    public MyCourseAdapter(Activity context, List<MyCourse.DataBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder<ItemMyCourseBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMyCourseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_my_course, parent, false);
        MyViewHolder<ItemMyCourseBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemMyCourseBinding> holder, int position) {
        ItemMyCourseBinding binding = holder.getBinding();
        MyCourse.DataBean.InfoBean item = dataList.get(position).getInfo();
        binding.setCourse(item);
        binding.tvLevel.setText(context.getString(R.string.level, item.getLevel() + ""));
        switch (dataList.get(position).getType()) {
            case "free":
                ImageDownLoader.setImageFromUrl(context, binding.ivTheme, item.getLogo());
                binding.status.setVisibility(View.GONE);
                binding.getRoot().setEnabled(true);
                break;
            case "crowdfunding":
                if (item.getOpen() == 1) {
                    ImageDownLoader.setImageFromUrl(context, binding.ivTheme, item.getLogo());
                    binding.status.setVisibility(View.GONE);
                    binding.getRoot().setEnabled(true);
                } else {
                    ImageDownLoader.setBlurImageFromUrl(context, binding.ivTheme, item.getLogo());
                    binding.ivTheme.setAlpha(0.5f);
                    binding.status.setVisibility(View.VISIBLE);
                    binding.getRoot().setEnabled(false);
                }
                break;
        }
        binding.getRoot().setOnClickListener(v -> QWebViewActivity.start(context, item.getTitle(), item.getLink()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
