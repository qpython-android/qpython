package org.qpython.qpy.main.adapter;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemFolderLittleBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.quseit.util.FolderUtils.sortTypeByName;


public abstract class FileTreeAdapter extends RecyclerView.Adapter<MyViewHolder<ItemFolderLittleBinding>> {
    private   int                offsetX;
    protected List<FileTreeBean> dataList;

    FileTreeAdapter(String rootPath) {
        dataList = new ArrayList<>();

        File file = new File(rootPath);
        dataList.add(new FileTreeBean(file, 0));

        offsetX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    public MyViewHolder<ItemFolderLittleBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFolderLittleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_folder_little, parent, false);
        MyViewHolder<ItemFolderLittleBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemFolderLittleBinding> holder, int position) {
        ItemFolderLittleBinding binding = holder.getBinding();
        FileTreeBean file = dataList.get(position);

        // initView
        binding.getRoot().setPadding(offsetX * file.level, 0, 0, 0);

        String fileName = file.name;
        binding.item.setText(fileName);

        boolean isDir = file.childList.size() != 0;
        if (!isDir) {
            binding.arrow.setVisibility(View.INVISIBLE);
            binding.item.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_editor_file_little, 0, 0, 0);
        } else {
            binding.arrow.setImageResource(file.isExpanded ? R.drawable.ic_arrow_drop_down_a : R.drawable.ic_arrow_drop_down_b);
            binding.arrow.setVisibility(View.VISIBLE);
            binding.item.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_editor_folder_little, 0, 0, 0);
        }

        // initListener
        binding.expandClickZone.setOnClickListener(v -> {
            if (isDir) {
                file.isExpanded = !file.isExpanded;
                notifyItemChanged(position);
                if (file.isExpanded) {
                    dataList.addAll(position + 1, file.childList);
                    notifyItemRangeInserted(position + 1, file.childList.size());
                } else {
                    notifyItemRangeRemoved(position + 1, file.childList.size());
                    dataList.removeAll(file.childList);
                }
            } else {
                itemClick(binding, file);
            }
        });
    }

    public void addNewFile(String path) {
        File parentFile = new File(path).getParentFile();
        String parentPath = parentFile.getAbsolutePath();
        int level;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).path.equals(parentPath)) {
                level = dataList.get(i).level + 1;
                FileTreeBean fileTreeBean = new FileTreeBean(new File(path), level);
                dataList.get(i).childList.add(fileTreeBean);
                if (dataList.get(i).isExpanded) {
                    dataList.add(i + dataList.get(i).childList.size(), fileTreeBean);
                    notifyItemInserted(i + dataList.get(i).childList.size());
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    abstract void itemClick(ItemFolderLittleBinding binding, FileTreeBean folderBean);

    protected class FileTreeBean {
        protected int    level;
        protected String path;
        protected String name;
        boolean isExpanded, isCheck;
        List<FileTreeBean> childList;

        FileTreeBean(File file, int level) {
            this.level = level;
            this.path = file.getAbsolutePath();
            this.name = file.getName();
            this.childList = new ArrayList<>();
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                Arrays.sort(files, sortTypeByName);
                for (File child : files) {
                    if (!child.getName().startsWith(".")) {
                        childList.add(new FileTreeBean(child, level + 1));
                    }
                }
            }
        }

        protected boolean isDir() {
            return childList.size() > 0;
        }
    }

}
