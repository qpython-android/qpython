package org.qpython.qpy.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.databinding.ItemFolderBinding;
import org.qpython.qpy.texteditor.ui.adapter.MyViewHolder;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.util.List;


public class CloudScriptAdapter extends RecyclerView.Adapter<MyViewHolder<ItemFolderBinding>> {
    private List<CloudFile> dataList;
    private Callback        callback;

    public CloudScriptAdapter(List<CloudFile> dataList) {
        this.dataList = dataList;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public MyViewHolder<ItemFolderBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, null);
        return new MyViewHolder<>(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemFolderBinding> holder, int position) {
        ItemFolderBinding binding = holder.getBinding();
        CloudFile cloudFile = dataList.get(position);
        binding.tvPath.setText(cloudFile.getUploadTime());
        binding.tvFileName.setText(cloudFile.getName());
        binding.ivFileIcon.setImageResource(R.drawable.ic_editor_file);
        binding.tvPath.setText(cloudFile.getPath());
        binding.tvPath.setVisibility(View.VISIBLE);
        binding.getRoot().setOnClickListener(v -> callback.onClick(position));

        if (cloudFile.isUploading()) {
            binding.uploading.setVisibility(View.VISIBLE);
        } else {
            binding.uploading.setVisibility(View.GONE);
        }
        if (new File(QPyConstants.ABSOLUTE_PATH + cloudFile.getPath()).exists()) {
            binding.uploaded.setImageResource(R.drawable.ic_check_circle);
            binding.uploaded.setVisibility(View.VISIBLE);
        } else {
            binding.uploaded.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public interface Callback {
        void onClick(int position);
    }
}
