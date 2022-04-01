package org.qpython.qpy.main.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quseit.util.FileHelper;
import com.quseit.util.ImageUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.ShareCodeUtil;
import org.qpython.qpy.codeshare.pojo.CloudFile;
import org.qpython.qpy.databinding.FragmentExplorerBinding;
import org.qpython.qpy.main.activity.NotebookActivity;
import org.qpython.qpy.main.activity.SettingActivity;
import org.qpython.qpy.main.activity.SignInActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.texteditor.EditorActivity;
import org.qpython.qpy.texteditor.TedLocalActivity;
import org.qpython.qpy.texteditor.common.CommonEnums;
import org.qpython.qpy.texteditor.common.RecentFiles;
import org.qpython.qpy.texteditor.ui.adapter.FolderAdapter;
import org.qpython.qpy.texteditor.ui.adapter.bean.FolderBean;
import org.qpython.qpy.texteditor.ui.view.EnterDialog;
import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;
import org.qpython.qpy.utils.FileUtils;
import org.qpython.qpy.utils.NotebookUtil;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.quseit.util.FolderUtils.sortTypeByName;

public class ExplorerFragment extends Fragment {
    private static final int REQUEST_SAVE_AS = 107;
    private static final int REQUEST_HOME_PAGE = 109;
    private static final int REQUEST_RECENT = 111;
    private static final int LOGIN_REQUEST = 2741;

    private static final String TYPE = "type";

    private int WIDTH = (int) ImageUtil.dp2px(60);

    private FragmentExplorerBinding binding;
    private List<FolderBean> folderList;
    private FolderAdapter adapter;
    private Map<String, Boolean> cloudedMap = new HashMap<>();

    private boolean openable = true; // 是否可打开文件
    private boolean uploadable;

    private int type;
    private String curPath;

    public static ExplorerFragment newInstance(int type) {
        ExplorerFragment myFragment = new ExplorerFragment();

        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explorer, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        type = getArguments().getInt(TYPE);
        initView();
        initListener();
        switch (type) {
            case REQUEST_RECENT:
                binding.rlPath.setVisibility(View.GONE);
                uploadable = false;
                break;
            case REQUEST_SAVE_AS:
                binding.ivNewFolder.setVisibility(View.VISIBLE);
                uploadable = false;
                break;
            case REQUEST_HOME_PAGE:
                binding.ivNewFolder.setVisibility(View.VISIBLE);
                uploadable = true;
                break;
        }
    }

    private void initView() {
        SwipeMenuCreator swipeMenuCreator = (leftMenu, rightMenu, viewType) -> {
//            SwipeMenuItem uploadItem = new SwipeMenuItem(getContext())
//                    .setBackgroundColor(Color.parseColor("#FF4798F3"))
//                    .setImage(R.drawable.ic_cloud_upload)
//                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
//                    .setWidth(WIDTH);

            SwipeMenuItem renameItem = new SwipeMenuItem(getContext())
                    .setBackgroundColor(Color.parseColor("#FF4BAC07"))
                    .setImage(R.drawable.ic_file_rename)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(WIDTH);

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                    .setBackgroundColor(Color.parseColor("#FFD14136"))
                    .setImage(R.drawable.ic_editor_filetree_close)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(WIDTH);

            switch (type) {
                case REQUEST_RECENT:
                    rightMenu.addMenuItem(deleteItem);
                    break;
                case REQUEST_SAVE_AS:
                    rightMenu.addMenuItem(deleteItem);
                    break;
                case REQUEST_HOME_PAGE:
                    //rightMenu.addMenuItem(uploadItem);
                    rightMenu.addMenuItem(renameItem);
                    rightMenu.addMenuItem(deleteItem);
                    break;
            }
        };

        folderList = new ArrayList<>();
        adapter = new FolderAdapter(folderList, getArguments().getInt(TYPE) == REQUEST_RECENT);
        adapter.setCloudMap(cloudedMap);
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeList.setSwipeMenuCreator(swipeMenuCreator);
        openDir(CONF.ABSOLUTE_PATH);
    }

    private void initListener() {
        binding.ivNewFolder.setOnClickListener(v -> doNewDir());
        binding.prevFolder.setOnClickListener(v -> {
            try {
                //采用Environment来获取sdcard路径
                String parentPath = new File(curPath).getParent();
                String rootPath = QPyConstants.ABSOLUTE_PATH;

                if (parentPath.length() >= rootPath.length()) {
                    openDir(parentPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.swipeList.setSwipeMenuItemClickListener(menuBridge -> {
            binding.swipeList.smoothCloseMenu();
            switch (menuBridge.getPosition()) {
                case 0:
//                    if (uploadable) {
//                        uploadFile(menuBridge.getAdapterPosition());
//                    } else {
//                        deleteFile(menuBridge.getAdapterPosition());
//                    }
//                    break;
//                case 1:
                    renameFile(menuBridge.getAdapterPosition());
                    break;
                case 1:
                    deleteFile(menuBridge.getAdapterPosition());
                    break;
            }
        });
        adapter.setClickListener(new FolderAdapter.Click() {
            @Override
            public void onItemClick(int position) {
                FolderBean item = folderList.get(position);
                if (item.getType().equals(CommonEnums.FileType.FILE)) {
                    if (!openable) {
                        Toast.makeText(getActivity(), R.string.cant_open, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //判断文件类型
                    int lastDot = item.getName().lastIndexOf(".");
                    if (lastDot != -1) {
                        String ext = item.getName().substring(lastDot + 1);
                        openFile(item.getFile(), ext);
                    }
                } else {
                    openDir(item.getPath());
                }
            }

            @Override
            public void onLongClick(int position) {
                binding.swipeList.smoothOpenRightMenu(position);
            }
        });

        binding.swipeList.setAdapter(adapter);
    }

    private void gotoSetting() {
        SettingActivity.startActivity(getActivity());
    }

    private void openFile(File file, String ext) {
        List<String> textExts = Arrays.asList(getContext().getResources().getStringArray(R.array.text_ext));
        if (textExts.contains(ext)) {
            EditorActivity.start(getContext(), Uri.fromFile(file));
        } else if (ext.equals("ipynb")) {
            boolean notebookenable = NotebookUtil.isNotebookEnable(getActivity());
            if (notebookenable) {
                NotebookActivity.start(getActivity(), file.getAbsolutePath(), false);
            } else {

                new AlertDialog.Builder(getActivity(), R.style.MyDialog)
                        .setTitle(R.string.dialog_alert)
                        .setMessage(getString(R.string.ennable_notebook_first))
                        .setNegativeButton(R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                        .setPositiveButton(R.string.ok, (dialog1, which) -> gotoSetting())
                        .create()
                        .show();

                //Toast.makeText(getActivity(), R.string.ennable_notebook_first, Toast.LENGTH_SHORT).show();
            }
        } else {
            FileUtils.openFile(getContext(), file);
        }
    }

    private void uploadFile(int adapterPosition) {
        File file = folderList.get(adapterPosition).getFile();

        // only support type in <R.array.support_file_ext>
        String ext = "";
        if (file.getName().lastIndexOf(".") > 0) {
            ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        }
        boolean isSupport = false;
        if (!file.isDirectory()) {
            for (String s : getResources().getStringArray(R.array.support_file_ext)) {
                if (s.equals(ext)) {
                    isSupport = true;
                    break;
                }
            }
        } else {
            isSupport = true;
        }

        if (!isSupport) {
            Toast.makeText(getContext(), R.string.not_support_type_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        // only available for already login user
        if (App.getUser() == null) {
            new AlertDialog.Builder(getActivity(), R.style.MyDialog)
                    .setTitle(R.string.need_login)
                    .setMessage(R.string.upload_login_hint)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(getString(R.string.login_now), (dialog, which) ->
                            startActivityForResult(new Intent(getActivity(), SignInActivity.class), LOGIN_REQUEST)
                    )
                    .create()
                    .show();
            return;
        }

        // upload
        folderList.get(adapterPosition).setUploading(true);
        adapter.notifyItemChanged(adapterPosition);
    }

    private void renameFile(int adapterPosition) {
        new EnterDialog(getContext())
                .setTitle(getString(R.string.rename))
                .setConfirmListener(name -> {
                    File oldFile = folderList.get(adapterPosition).getFile();
                    File newFile = new File(oldFile.getParent(), name);
                    boolean renameSuc = oldFile.renameTo(newFile);
                    if (renameSuc) {
                        folderList.set(adapterPosition, new FolderBean(newFile));
                        adapter.notifyItemChanged(adapterPosition);
                        return true;
                    } else {
                        Toast.makeText(getActivity(), R.string.rename_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .setText(folderList.get(adapterPosition).getName())
                .show();
    }

    private void deleteFile(int adapterPosition) {
        switch (type) {
            case REQUEST_RECENT:
                RecentFiles.removePath(folderList.get(adapterPosition).getPath());
                folderList.remove(adapterPosition);
                adapter.notifyDataSetChanged();
                break;
            case REQUEST_HOME_PAGE:
            case REQUEST_SAVE_AS:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialog);
                builder.setTitle(R.string.warning)
                        .setMessage(R.string.delete_file_hint)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            String dir = folderList.get(adapterPosition).getFile().getParent();
                            FileHelper.clearDir(folderList.get(adapterPosition).getFile().getAbsolutePath(), 0, true);
                            openDir(dir);

//                            folderList.remove(adapterPosition);
//                            adapter.notifyItemRemoved(adapterPosition);
                        })
                        .show();
                break;
        }
    }

    private void openDir(String dirPath) {
        if (type == REQUEST_RECENT) {
            folderList.clear();
            for (String path : RecentFiles.getRecentFiles()) {
                folderList.add(new FolderBean(new File(path)));
            }

            if (folderList.size() == 0) {
                binding.emptyHint.setVisibility(View.VISIBLE);
            } else {
                binding.emptyHint.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        } else {
            binding.tvPath.setText(dirPath);
            curPath = dirPath;
            File dir = new File(dirPath);
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    Arrays.sort(files, sortTypeByName);
                    folderList.clear();
                    adapter.notifyDataSetChanged();
                    for (File file : files) {
                        if (!dir.getName().equals(CONF.BASE_PATH)) {
                            if (!file.getName().startsWith(".")) {
                                folderList.add(new FolderBean(file));
                            }

                        } else {
                            folderList.add(new FolderBean(file));

                        }

                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(getContext(), R.string.file_not_exists, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doNewDir() {
        new EnterDialog(getContext())
                .setTitle(getString(R.string.new_folder))
                .setHint(getString(R.string.folder_name))
                .setConfirmListener(name -> {
                    File dirN = new File(curPath, name.equals("") ? getString(R.string.untitled_folder) : name);
                    if (dirN.exists()) {
                        Crouton.makeText(getActivity(), getString(R.string.toast_folder_exist), Style.ALERT).show();
                        return false;
                    } else {
                        if (dirN.mkdirs()) {
                            openDir(curPath + "/" + name);
                        } else {
                            Toast.makeText(getContext(), R.string.mkdir_fail, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                })
                .show();
    }

    public void backToPrev() {
        Log.d("ExplorerFragment", "backToPrev:" + curPath);
        String qpyDir = getContext().getExternalFilesDir(null).getAbsolutePath() + "/qpython";
        if (curPath == null || qpyDir.equals(curPath) || getContext().getExternalFilesDir(null).getAbsolutePath().equals(curPath)) {
            getActivity().finish();
        } else {
            String parentPath = new File(curPath).getParent();
            if (!TextUtils.isEmpty(parentPath)) openDir(parentPath);
        }
    }

    private void updateClouded(File file) {
        if (file.getParent().contains("projects")) {
            String parent = file.getParent() + "/";
            String subPath = file.getPath().substring(file.getPath().indexOf(parent) + parent.length());
            String projName = subPath.contains("/") ? subPath.substring(0, subPath.indexOf("/")) : "/" + subPath;
            cloudedMap.put(CONF.ABSOLUTE_PATH + projName, true);
        }
        if (file.isDirectory()) {
            for (File file1 : FileHelper.filterExt(file, getResources().getStringArray(R.array.support_file_ext))) {
                cloudedMap.put(file1.getPath(), true);
            }
        } else {
            cloudedMap.put(file.getPath(), true);
        }
    }

    public String getCurPath() {
        return curPath;
    }


    public void deleteCloudedMap(String absolutePath) {
        if (cloudedMap.containsKey(absolutePath)) {
            cloudedMap.remove(absolutePath);
            adapter.notifyDataSetChanged();
        }
    }

    public void updateCloudedFiles(Map<String, Boolean> map) {
        if (map != null) {
            cloudedMap.putAll(map);
        }
        adapter.notifyDataSetChanged();
    }
}
