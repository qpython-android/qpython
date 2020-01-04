package org.qpython.qpy.main.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quseit.util.ImageDownLoader;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.Gist;
import org.qpython.qpy.databinding.ItemCommentBinding;

import java.util.List;


public class CodeCommentAdapter extends RecyclerView.Adapter<MyViewHolder<ItemCommentBinding>> {
    private List<Gist.CommentBean> dataList;
    private Reply                  callback;
    private Context                context;

    public CodeCommentAdapter(List<Gist.CommentBean> dataList) {
        this.dataList = dataList;
    }

    public void setItemClickListener(Reply callback) {
        this.callback = callback;
    }

    @Override
    public MyViewHolder<ItemCommentBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemCommentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_comment, parent, false);
        MyViewHolder<ItemCommentBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemCommentBinding> holder, int position) {
        ItemCommentBinding binding = holder.getBinding();
        if (position == dataList.size()) {
            binding.avatar.setImageResource(0);
            binding.reComment.setVisibility(View.GONE);
            binding.line.setVisibility(View.GONE);
            return;
        }
        Gist.CommentBean item = dataList.get(position);
        binding.date.setText(item.getData());
        binding.name.setText(item.getFrom());
        if (item.getAvatar() != null) {
            ImageDownLoader.setImageFromUrl(context, binding.avatar, item.getAvatar());
        }
        if (TextUtils.isEmpty(item.getRe())) {
            binding.fromContent.setText(item.getFrom_content());
            binding.reComment.setVisibility(View.GONE);
        } else {
            binding.reComment.setVisibility(View.VISIBLE);
            binding.fromContent.setText(Html.fromHtml(context.getString(R.string.re_format, item.getFrom(), item.getFrom_content())));
            binding.reComment.setText(Html.fromHtml(context.getString(R.string.replied_format, item.getRe(), item.getRe_content())));
        }

        binding.getRoot().setOnClickListener(v -> callback.reply(item.getFrom(), item.getFrom_content()));
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size() + 1;
    }

    public interface Reply {
        void reply(String to, String reComment);
    }
}
