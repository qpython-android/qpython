package org.qpython.qpy.texteditor.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemFolderBinding;
import org.qpython.qpy.texteditor.ui.adapter.bean.FolderBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A File List Adapter
 *
 * @author Hmei
 */
public class PathListAdapter extends RecyclerView.Adapter<PathListAdapter.MyViewHolder> {
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    HashMap<FolderBean, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private List<FolderBean> dataList;
    private List<FolderBean> itemsPendingRemoval;
    private Context          context;
    private boolean showPath;
    private Callback callback;
    private Handler handler = new Handler(); // hanlder for running delayed runnables


    public PathListAdapter(List<FolderBean> dataList, Context context, boolean showPath, Callback callback) {
        this.dataList = dataList;
        this.context = context;
        this.showPath = showPath;
        this.callback = callback;
        this.itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFolderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_folder, parent, false);
        MyViewHolder holder = new MyViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FolderBean item = dataList.get(position);
        ItemFolderBinding binding = holder.getBinding();
        if (itemsPendingRemoval.contains(item)) {
            // undo state
            holder.itemView.setBackgroundColor(Color.parseColor("#FFD14136"));
            binding.ivFileIcon.setVisibility(View.GONE);
            binding.tvFileName.setVisibility(View.GONE);
//            binding.btnUndo.setVisibility(View.VISIBLE);
//            binding.btnUndo.setOnClickListener(v -> {
//                Runnable pendingRemovalRunnable = pendingRunnables.get(item);
//                pendingRunnables.remove(item);
//                if (pendingRemovalRunnable != null)
//                    handler.removeCallbacks(pendingRemovalRunnable);
//                itemsPendingRemoval.remove(item);
//                // this will rebind the row in "normal" state
//                notifyItemChanged(dataList.indexOf(item));
//            });
        } else {
//            binding.btnUndo.setVisibility(View.GONE);
            binding.tvFileName.setText(item.getName());
            if (showPath) {
                binding.tvPath.setVisibility(View.VISIBLE);
                binding.tvPath.setText(item.getPath().substring(item.getPath().indexOf("/qpython"), item.getPath().length()));
            }
            switch (item.getType()) {
                case FILE:
                    binding.ivFileIcon.setImageResource(R.drawable.ic_editor_file);
                    break;
                case FOLDER:
                    binding.ivFileIcon.setImageResource(R.drawable.ic_editor_folder);
                    break;
            }

            binding.llFolderItem.setOnClickListener(v -> callback.click(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : (dataList.size() == 0 ? 0 : dataList.size());
    }

    public void pendingRemoval(int position) {
        final FolderBean item = dataList.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(dataList.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        FolderBean item = dataList.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (dataList.contains(item)) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        FolderBean item = dataList.get(position);
        return itemsPendingRemoval.contains(item);
    }


    public interface Callback {
        void click(int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemFolderBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);
        }

        public ItemFolderBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemFolderBinding binding) {
            this.binding = binding;
        }
    }

}

