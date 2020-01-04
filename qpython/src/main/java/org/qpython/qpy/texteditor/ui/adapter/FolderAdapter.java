package org.qpython.qpy.texteditor.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemFolderBinding;
import org.qpython.qpy.texteditor.ui.adapter.bean.FolderBean;

import java.util.List;
import java.util.Map;


public class FolderAdapter extends RecyclerView.Adapter<MyViewHolder<ItemFolderBinding>> {
    private List<FolderBean>     dataList;
    private Map<String, Boolean> cloudMap;
    private boolean              showPath;
    private Click                clickListener;

    public FolderAdapter(List<FolderBean> dataList, boolean showPath) {
        this.dataList = dataList;
        this.showPath = showPath;
    }

    public void setClickListener(Click clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MyViewHolder<ItemFolderBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_folder, null);
        return new MyViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemFolderBinding> holder, final int position) {
        ItemFolderBinding binding = holder.getBinding();
        FolderBean item = dataList.get(position);
        if (showPath) {
            binding.tvPath.setVisibility(View.VISIBLE);
            if (item.getPath().contains("/qpython")) {
                binding.tvPath.setText(item.getPath().substring(item.getPath().indexOf("/qpython"), item.getPath().length()));
            } else {
                binding.tvPath.setText(item.getPath());
            }
        }
        binding.tvFileName.setText(item.getName());
        switch (item.getType()) {
            case FILE:
                binding.ivFileIcon.setImageResource(R.drawable.ic_editor_file);
                break;
            case FOLDER:
                binding.ivFileIcon.setImageResource(R.drawable.ic_editor_folder);
                break;
        }
        if (cloudMap != null) {
            if (cloudMap.containsKey(item.getPath())) {
                if (item.getFile().isDirectory()) {
                    if (item.getFile().getParent().contains("projects"))
                        binding.uploaded.setImageResource(R.drawable.ic_cloud_notdone);
                } else {
                    binding.uploaded.setImageResource(R.drawable.ic_cloud_done);
                }
                binding.uploaded.setVisibility(View.VISIBLE);
            } else {
                binding.uploaded.setVisibility(View.GONE);
            }
        }
        binding.llFolderItem.setOnClickListener(v -> clickListener.onItemClick(position));
        binding.llFolderItem.setOnLongClickListener(v -> {
            clickListener.onLongClick(position);
            return true;
        });
        binding.uploading.setVisibility(item.isUploading() ? View.VISIBLE : View.GONE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void setCloudMap(Map<String, Boolean> cloudMap) {
        this.cloudMap = cloudMap;
    }

    public void setUploadFile(int position) {
        cloudMap.put(dataList.get(position).getPath(), true);
    }

    public interface Click {
        void onItemClick(int position);

        void onLongClick(int position);
    }
}
