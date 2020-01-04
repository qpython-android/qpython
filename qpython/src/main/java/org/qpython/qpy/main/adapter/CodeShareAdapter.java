package org.qpython.qpy.main.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.quseit.util.ImageDownLoader;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.GistBase;
import org.qpython.qpy.databinding.ItemShareCodeBinding;
import org.qpython.qpy.main.activity.CodeDetailActivity;
import org.qpython.qpy.main.activity.CodeShareActivity;

import java.util.List;


public class CodeShareAdapter extends RecyclerView.Adapter<MyViewHolder<ItemShareCodeBinding>> {

    private List<GistBase> dataList;
    private Context        context;
    private boolean        isProj;

    public CodeShareAdapter(List<GistBase> dataList, boolean isProj) {
        this.dataList = dataList;
        this.isProj = isProj;
    }

    @Override
    public MyViewHolder<ItemShareCodeBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemShareCodeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_share_code, parent, false);
        MyViewHolder<ItemShareCodeBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemShareCodeBinding> holder, int position) {
        ItemShareCodeBinding binding = holder.getBinding();
        GistBase item = dataList.get(position);
        ImageDownLoader.setImageFromUrl(context, binding.avatar, item.getAvatar());
        binding.title.setText(item.getTitle());
        binding.name.setText(item.getAuthor());
        binding.date.setText(item.getDate());
        binding.bookmarkCount.setText(item.getBookmaker_count() + "");
        binding.commentCount.setText(item.getComment_count() + "");

        binding.getRoot().setOnClickListener(v -> CodeDetailActivity.start(context, item.getId(), isProj));
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }
}
