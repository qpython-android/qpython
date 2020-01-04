package org.qpython.qpy.main.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quseit.util.DateTimeHelper;
import com.quseit.util.ImageDownLoader;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.RvItemNewsBinding;
import org.qpython.qpy.main.activity.GistDetailActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.fragment.MyGistFragment;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.response.GistBean;

import java.util.List;
import java.util.Objects;

import static org.qpython.qpy.main.server.gist.GistEvent.DELETE;


/**
 * 文 件 名: CommunityAdapter
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/25 17:21
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class GistAdapter extends RecyclerView.Adapter<MyViewHolder<RvItemNewsBinding>> {

    private boolean mShowDelete;

    private List<GistBean> mGistBeans;
    private Context        mContext;
    private MyGistFragment fragment;

    public GistAdapter(Context context) {
        mContext = context;
    }

    public GistAdapter(Context context, MyGistFragment fragment) {
        mContext = context;
        this.fragment = fragment;
    }

    public void setData(List<GistBean> list) {
        mGistBeans = list;
        notifyDataSetChanged();
    }

    public void loadMore(List<GistBean> list) {
        mGistBeans.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder<RvItemNewsBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        RvItemNewsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.rv_item_news, parent, false);
        MyViewHolder<RvItemNewsBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder<RvItemNewsBinding> holder, int position) {
        RvItemNewsBinding binding = holder.getBinding();
        GistBean bean = mGistBeans.get(position);
        //点击跳转详情
        binding.rootLayout.setOnClickListener(view -> GistDetailActivity.startNewsDetail(mContext, bean.getId()));

        //名字
        binding.userNameTv.setText(bean.getUser().getUName());
        //头像
        ImageDownLoader.setImageFromUrl(mContext, binding.userAvatarIv, bean.getUser().getAvatar());
        //时间
        binding.commitTimeTv.setText(DateTimeHelper.converTime(bean.getCreateAt(), mContext.getResources()
                .getStringArray(R.array.time_label)));
        //标题
        binding.contentTv.setText(bean.getDescription());
        //收藏数
        binding.starCountTv.setText(String.valueOf(bean.getStar()));
        if (App.getFavorites().contains(bean.getId())) {
            binding.starCountLayout.setSelected(true);
        } else {
            binding.starCountLayout.setSelected(false);
        }
        //评论数
        binding.commentCountTv.setText(String.valueOf(bean.getComment()));

        //删除
        if (mShowDelete) {
            binding.deleteIv.setVisibility(View.VISIBLE);
            binding.deleteIv.setOnClickListener(v -> new AlertDialog.Builder(mContext, R.style.MyDialog)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.delete_gist_hint)
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton(R.string.yes, (dialogInterface, i) ->
                            EventBus.getDefault().post(new GistEvent(DELETE, bean.getId())))
                    .create().show());
        } else {
            binding.deleteIv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mGistBeans == null ?
                0 :
                mGistBeans.size();

    }

    public void setShowDelete(boolean showDelete) {
        mShowDelete = showDelete;
    }


    public void addCommentNum(String id) {
        int position = getPositionById(id);
        if (position != -1) {
            mGistBeans.get(position).addCommentNum();
            notifyItemChanged(position);
        }
    }

    public void addFav(String id, boolean isAdd) {
        int position = getPositionById(id);
        if (position != -1) {
            mGistBeans.get(position).changeStar(isAdd);
            notifyItemChanged(position);
        }
    }

    public void delete(String id) {
        int position = getPositionById(id);
        if (position != -1) {
            notifyItemRemoved(position);
            mGistBeans.remove(position);
        }
        if (mGistBeans.size() == 0 && fragment != null) {
            fragment.showHint(true);
        }
    }

    private int getPositionById(String id) {
        GistBean posBean = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            posBean = mGistBeans.stream().filter(bean -> Objects.equals(id, bean.getId()))
                    .findFirst()
                    .orElse(null);
        } else {
            for (GistBean mGistBean : mGistBeans) {
                if (id.equals(mGistBean.getId())) {
                    posBean = mGistBean;
                }
            }
        }
        return posBean == null ? -1 : mGistBeans.indexOf(posBean);
    }
}
