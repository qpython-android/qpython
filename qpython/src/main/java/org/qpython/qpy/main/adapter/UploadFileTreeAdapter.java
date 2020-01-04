package org.qpython.qpy.main.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.qpython.qpy.databinding.ItemFolderLittleBinding;

import java.util.ArrayList;
import java.util.List;

public class UploadFileTreeAdapter extends FileTreeAdapter {
    private Handler handler = new Handler();
    private List<String> checkedList;
    private RecyclerView mRecyclerView;

    public UploadFileTreeAdapter(String rootPath) {
        super(rootPath);
        checkedList = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemFolderLittleBinding> holder, int position) {
        super.onBindViewHolder(holder, position);
        ItemFolderLittleBinding binding = holder.getBinding();
        FileTreeBean file = dataList.get(position);

        binding.checkbox.setVisibility(View.VISIBLE);
        binding.checkbox.setOnCheckedChangeListener(null);

        binding.checkbox.setChecked(file.isCheck);

        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (file.isDir()) {
                reverseCheck(file, isChecked);
                return;
            }
            if (isChecked) {
                checkedList.add(file.path);
            } else {
                checkedList.remove(file);
            }
        });
    }

    public List<String> getCheckedList() {
        return checkedList;
    }

    private void reverseCheck(FileTreeBean file, boolean isChecked) {
        file.isCheck = isChecked;
        if (file.isDir()) {
            for (FileTreeBean fileTreeBean : file.childList) {
                fileTreeBean.isCheck = isChecked;
                if (fileTreeBean.isDir()) {
                    reverseCheck(fileTreeBean, isChecked);
                }
                if (isChecked) {
                    checkedList.add(fileTreeBean.path);
                } else {
                    checkedList.remove(fileTreeBean.path);
                }
            }
            postAndNotifyAdapter();
        }
    }

    private void postAndNotifyAdapter() {
        handler.post(() -> {
            if (!mRecyclerView.isComputingLayout()) {
                notifyDataSetChanged();
            } else {
                postAndNotifyAdapter();
            }
        });
    }

    @Override
    void itemClick(ItemFolderLittleBinding binding, FileTreeBean folderBean) {
        binding.checkbox.setChecked(!binding.checkbox.isChecked());
    }
}
