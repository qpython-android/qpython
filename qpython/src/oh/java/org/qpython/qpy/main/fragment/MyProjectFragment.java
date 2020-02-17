package org.qpython.qpy.main.fragment;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.quseit.util.DateTimeHelper;
import com.quseit.util.ImageUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.ShareCodeUtil;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.databinding.FragmentRefreshRvBinding;
import org.qpython.qpy.main.adapter.CloudScriptAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.event.ShareCodeCallback;
import org.qpython.qpy.texteditor.TedLocalActivity;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.content.Context.MODE_PRIVATE;
import static org.qpython.qpy.codeshare.CONSTANT.CLOUDED_MAP;
import static org.qpython.qpy.codeshare.CONSTANT.IS_UPLOAD_INIT;

public class MyProjectFragment extends Fragment {
    private int WIDTH = (int) ImageUtil.dp2px(60);
    private FragmentRefreshRvBinding binding;
    private CloudScriptAdapter adapter;
    private List<CloudFile> scriptList = new ArrayList<>();
    private boolean isSaverOn;
    public boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.bind(LayoutInflater.from(getContext()).inflate(R.layout.fragment_refresh_rv, null));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scriptList = new ArrayList<>();
        adapter = new CloudScriptAdapter(scriptList);
        isSaverOn = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.key_saver), true);
        initView();
        initListener();
        retry(!isSaverOn);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locatedCloud(scriptList);
    }

    public void retry(boolean forceRefresh) {
        if (scriptList != null && adapter != null) {
            scriptList.clear();
            adapter.notifyDataSetChanged();
        }
        startProgressBar();
        isLoading = true;
        // TODO: 2017/12/4 获取云端脚本

    }

    public void notifyDataSetChange(){
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 保存云端文件目录到本地
     */
    private void locatedCloud(List<CloudFile> cloudFiles) {
        if (cloudFiles.size() > 0) {
            HashMap<String, Boolean> clouded = new HashMap<>();
            for (CloudFile cloudFile : cloudFiles) {
                if (cloudFile.getPath().contains("/projects/")) {
                    clouded.put(CONF.ABSOLUTE_PATH + "/projects/" + cloudFile.getProjectName(), true);
                }
                clouded.put(CONF.ABSOLUTE_PATH + cloudFile.getPath(), true);
            }
            SharedPreferences sp = getActivity().getPreferences(MODE_PRIVATE);
            Type type = new TypeToken<HashMap<String, Boolean>>() {
            }.getType();
            sp.edit().putBoolean(IS_UPLOAD_INIT, true)
                    .putString(CLOUDED_MAP, App.getGson().toJson(clouded, type)).apply();
        }
    }

    private void startProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.netError.setVisibility(View.GONE);
        Observable.just(null)
                .delay(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> showNetErrorText())
                .subscribe();
    }

    private void showEmpty() {
        if (binding.progressBar.getVisibility() == View.VISIBLE) {
            binding.progressBar.setVisibility(View.GONE);
            binding.netError.setText(R.string.cloud_empty_hint);
            binding.netError.setVisibility(View.VISIBLE);
        }
    }

    private void showNetErrorText() {
        if (binding.progressBar.getVisibility() == View.VISIBLE) {
            binding.progressBar.setVisibility(View.GONE);
            binding.netError.setText(R.string.net_lagging);
            binding.netError.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        SwipeMenuCreator swipeMenuCreator = (leftMenu, rightMenu, viewType) -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                    .setBackgroundColor(Color.parseColor("#FFD14136"))
                    .setImage(R.drawable.ic_editor_filetree_close)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(WIDTH);

            SwipeMenuItem downloadItem = new SwipeMenuItem(getContext())
                    .setBackgroundColor(Color.parseColor("#FF4798F3"))
                    .setImage(R.drawable.ic_cloud_download)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(WIDTH);
            rightMenu.addMenuItem(downloadItem);
            rightMenu.addMenuItem(deleteItem);
        };
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeList.setSwipeMenuCreator(swipeMenuCreator);
    }

    private void initListener() {
        binding.netError.setOnClickListener(v -> retry(true));
        binding.swipeList.setSwipeMenuItemClickListener(menuBridge -> {
            menuBridge.closeMenu();
            switch (menuBridge.getPosition()) {
                case 0:
                    CloudFile cloudFile = scriptList.get(menuBridge.getAdapterPosition());
                    String path;
                    path = QPyConstants.ABSOLUTE_PATH + cloudFile.getPath();
                    File file = new File(path);
                    if (file.exists()) {
//                        new AlertDialog.Builder(getContext(), R.style.MyDialog)
//                                .setTitle(R.string.override_hint)
//                                .setMessage(Html.fromHtml(getString(R.string.conflict_hint,
//                                        cloudFile.getUploadTime(),
//                                        DateTimeHelper.AGO_FULL_DATE_FORMATTER.format(new Date(file.lastModified())))))
//                                .setNegativeButton(R.string.no, null)
//                                .setPositiveButton(R.string.yes, (dialog, which) -> writeFile(file, cloudFile, menuBridge.getAdapterPosition()))
//                                .create()
//                                .show();
                    } else {
                        //create file
                        File dir = new File(path.substring(0, path.lastIndexOf("/")));
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        writeFile(file, cloudFile, menuBridge.getAdapterPosition());
                    }
                    break;
                case 1:
                    // delete
                    scriptList.get(menuBridge.getAdapterPosition()).setUploading(true);
                    adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                    // TODO: 2017/12/4 删除上传的脚本文件
//                    ShareCodeUtil.getInstance().deleteUploadScript(scriptList.get(menuBridge.getAdapterPosition()), new LeancloudStorage.SaveFileCallback() {
//                        @Override
//                        public void onSuccess(int code) {
//                            Toast.makeText(getContext(), R.string.delete_remote_suc, Toast.LENGTH_SHORT).show();
//                            adapter.notifyItemRemoved(menuBridge.getAdapterPosition());
//                            ((TedLocalActivity) getActivity()).deleteCloudFile(scriptList.get(menuBridge.getAdapterPosition()).getAbsolutePath());
//                            scriptList.remove(menuBridge.getAdapterPosition());
//                        }
//
//                        @Override
//                        public void onFail(String msg) {
//                            Toast.makeText(getContext(), "删除出错", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    break;
            }
        });
        adapter.setCallback((position) -> binding.swipeList.smoothOpenRightMenu(position));
        binding.swipeList.setAdapter(adapter);
    }

    private void writeFile(File file, CloudFile cloudFile, int adapterPosition) {
        cloudFile.setUploading(true);
        adapter.notifyDataSetChanged();
//        ShareCodeUtil.getInstance().downloadFile(cloudFile.getFileObject().getFileId(), new LeancloudStorage.DownloadCallback() {
//            @Override
//            public void onSuccess(String content) {
//                try {
//                    FileWriter writer = new FileWriter(file, false);
//                    writer.write(content);
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    cloudFile.setUploading(false);
//                    adapter.notifyDataSetChanged();
//                    Toast.makeText(getContext(), R.string.override_fail_hint, Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(getContext(), R.string.file_downloaded, Toast.LENGTH_SHORT).show();
//                cloudFile.setUploading(false);
//                adapter.notifyItemChanged(adapterPosition);
//            }
//
//            @Override
//            public void onFail(String msg) {
//
//            }
//
//            @Override
//            public void onProgress(int progress) {
//
//            }
//        });
    }

    public void needRefresh(boolean isNewUpload) {
        if (binding == null) {
            // not init yet
            return;
        }
        if (isNewUpload) {
            retry(!isSaverOn);
        }
    }
}
