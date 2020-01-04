package org.qpython.qpy.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quseit.util.DateTimeHelper;
import com.quseit.util.ImageDownLoader;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemCommentBinding;
import org.qpython.qpy.databinding.RvItemNewsHeadBinding;
import org.qpython.qpy.main.activity.GistEditActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.response.CommentBean;
import org.qpython.qpy.main.server.gist.response.GistBean;

import java.util.List;

import static org.qpython.qpy.main.server.gist.GistEvent.CODE_REVIEW_EVENT;
import static org.qpython.qpy.main.server.gist.GistEvent.COMMENT_EVENT;
import static org.qpython.qpy.main.server.gist.GistEvent.DIVIDER;
import static org.qpython.qpy.main.server.gist.GistEvent.FAVORITE_REQUEST;
import static org.qpython.qpy.main.server.gist.GistEvent.FORK;
import static org.qpython.qpy.main.server.gist.GistEvent.REPLY_EVENT;
import static org.qpython.qpy.main.server.gist.GistEvent.RUN_NOTEBOOK;
import static org.qpython.qpy.main.server.gist.GistEvent.RUN_SCRIPT;

/**
 * 文 件 名: NewsDetailAdapter
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/26 11:25
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class NewsDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private GistBean mGistBean;
    private Context  mContext;

    public NewsDetailAdapter(Context context) {
        mContext = context;
    }

    public void setData(GistBean bean) {
        mGistBean = bean;
        notifyDataSetChanged();
    }

    public void addComment(CommentBean bean) {
        mGistBean.getComments().add(0, bean);
        mGistBean.addCommentNum();
        notifyDataSetChanged();
    }

    public void loadMoreComment(List<CommentBean> list) {
        mGistBean.getComments().addAll(list);
        notifyDataSetChanged();
    }

    public void favorite(boolean is) {
        mGistBean.setFavorite(is);
        mGistBean.changeStar(is);
        notifyItemChanged(0);
    }

    public void startEdit() {
        GistEditActivity.start(mContext, mGistBean.getId(), mGistBean.getTitle(), mGistBean
                .getDescription(), mGistBean.getSource());
        ((Activity)mContext).finish();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            RvItemNewsHeadBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rv_item_news_head, parent, false);
            MyViewHolder<RvItemNewsHeadBinding> holder = new MyViewHolder<>(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        } else {
            ItemCommentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent
                    .getContext()), R.layout.item_comment, parent, false);
            MyViewHolder<ItemCommentBinding> holder = new MyViewHolder<>(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            RvItemNewsHeadBinding binding = (RvItemNewsHeadBinding) holder.getBinding();
            initHead(binding);
        } else {
            ItemCommentBinding binding = (ItemCommentBinding) holder.getBinding();
            initComments(position - 1, binding);
        }
    }

    private void initComments(int position, ItemCommentBinding binding) {
        CommentBean bean = mGistBean.getComments().get(position);
//        名字
        binding.name.setText(bean.getUserName());
        //头像
        ImageDownLoader.setImageFromUrl(mContext, binding.avatar, bean.getUserAvatar());
        //时间
        binding.date.setText(DateTimeHelper.converTime(bean.getCreateAt(), mContext.getResources()
                .getStringArray(R.array.time_label)));
        //评论
        binding.fromContent.setText(bean.getComment());

        if (bean.getReplies() != null) {
            String htmlContent = mContext.getString(R.string.replied_format, bean.getReplies
                    ().getUser_name(), bean.getReplies().getComment_content());
            binding.reComment.setText(Html.fromHtml(htmlContent));
            binding.reComment.setVisibility(View.VISIBLE);
        } else {
            binding.reComment.setVisibility(View.GONE);
        }
        //回复评论
        binding.getRoot().setOnClickListener(v -> EventBus.getDefault().post(new GistEvent
                (REPLY_EVENT, bean.getId() + "," + bean.getUserName())));
    }

    private void initHead(RvItemNewsHeadBinding binding) {
        //头像
        ImageDownLoader.setImageFromUrl(mContext, binding.userAvatarIv, mGistBean.getUser().getAvatar());
        //名字
        binding.userNameTv.setText(mGistBean.getUser().getUName());
        //时间
        binding.commitTimeTv.setText(DateTimeHelper.converTime(mGistBean.getCreateAt(), mContext.getResources()
                .getStringArray(R.array.time_label)));
        //收藏数
        binding.starCountTv.setText(String.valueOf(mGistBean.getStar()));
        binding.starCountLayout.setSelected(mGistBean.isFavorite());
        binding.starCountLayout.setOnClickListener(v -> EventBus.getDefault().post(new GistEvent
                (FAVORITE_REQUEST)));
        //评论数
        binding.commentCountTv.setText(String.valueOf(mGistBean.getComment()));
        binding.commentCountLayout.setOnClickListener(v -> EventBus.getDefault().post(new GistEvent(COMMENT_EVENT)));
        //空评论
        binding.emptyTv.setOnClickListener(v -> EventBus.getDefault().post(new GistEvent(COMMENT_EVENT)));
        if (mGistBean.getComments().size() > 0) binding.emptyTv.setVisibility(View.GONE);
//        //标题
//        binding.newsTitleTv.setText(mGistBean.getTitle());
        //描述
        binding.newsContentTv.setText(mGistBean.getDescription());
        //代码
        binding.codeContentTv.setText(mGistBean.getSource());
        binding.codeContentTv.setOnClickListener(v -> EventBus.getDefault().post(new GistEvent(CODE_REVIEW_EVENT)));
        //运行
        binding.btnRun.setOnClickListener(v -> {
            if (mGistBean.getSourceType().equals("notebook")) {
                EventBus.getDefault().post(new GistEvent(RUN_NOTEBOOK, mGistBean.getSource().toString()));
            } else if (mGistBean.getSourceType().equals("script")) {
                EventBus.getDefault().post(new GistEvent(RUN_SCRIPT, mGistBean.getTitle() +
                        DIVIDER + mGistBean.getSource()));
            }
        });
        //fork
        if (App.getUser() == null || mGistBean.getUser().getUserName().equals(App.getUser()
                .getUserName())) {
            binding.btnFork.setVisibility(View.GONE);
        } else {
            binding.btnFork.setVisibility(View.VISIBLE);
            binding.btnFork.setOnClickListener(view -> EventBus.getDefault().post(new GistEvent(FORK)));
        }
    }

    @Override
    public int getItemCount() {
        return mGistBean == null ?
                0 :
                mGistBean.getComments() == null ?
                        1 :
                        mGistBean.getComments().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}
