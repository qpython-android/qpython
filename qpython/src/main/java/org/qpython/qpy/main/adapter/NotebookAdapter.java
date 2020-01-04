package org.qpython.qpy.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.qpython.qpy.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotebookAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private File mFile;
    private List<File> mFileList;
    private OnItemClickCallback mOnItemClickCallback;

    public NotebookAdapter(Context context, File file) {
        mContext = context;
        getFileList(file);
    }

    private void getFileList(File file) {
        mFile = file;
        File[] files = mFile.listFiles();
        if (files == null){
            mFileList = new ArrayList<>();
        }else {
            mFileList = Arrays.asList(files);
        }
    }

    public void setFile(File file) {
        getFileList(file);
        notifyDataSetChanged();
    }



    public File getFile() {
        return mFile;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        mOnItemClickCallback = onItemClickCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notebook, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        File file = mFileList.get(position);
        TextView tvFileName = holder.itemView.findViewById(R.id.file_name_tv);
        tvFileName.setText(file.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnItemClickCallback != null) {
                    if (file.isDirectory()) {
                        setFile(file);
                        mOnItemClickCallback.onClick(file.getAbsolutePath(), true);
                    }else {
                        mOnItemClickCallback.onClick(file.getAbsolutePath(), false);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public interface OnItemClickCallback {
        void onClick(String filePath, boolean isDir);
    }
}