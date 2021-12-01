package org.qpython.qpy.main.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quseit.util.FileHelper;
import com.quseit.util.FileUtils;
import com.quseit.util.ImageUtil;
import com.quseit.util.NAction;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.FragmentRefreshRvBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.texteditor.TedLocalActivity;
import org.qpython.qpy.texteditor.ui.adapter.FolderAdapter;
import org.qpython.qpy.texteditor.ui.adapter.bean.FolderBean;
import org.qpython.qpy.texteditor.ui.view.EnterDialog;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFragment extends Fragment {
    private int WIDTH = (int) ImageUtil.dp2px(60);

    private FragmentRefreshRvBinding binding;

    private static final String TYPE    = "type";
    public static final  String PROJECT = "projects";
    public static final  String SCRIPT  = "scripts";

    public static final  String PROJECT3 = "projects3";
    public static final  String SCRIPT3  = "scripts3";

    private static final String PROJECT_PATH = FileUtils.getAbsolutePath(App.getContext()) + "/" + PROJECT;
    private static final String SCRIPT_PATH  = FileUtils.getAbsolutePath(App.getContext()) + "/" + SCRIPT;

    private static final String PROJECT_PATH3 = FileUtils.getAbsolutePath(App.getContext()) + "/" + PROJECT3;
    private static final String SCRIPT_PATH3  = FileUtils.getAbsolutePath(App.getContext()) + "/" + SCRIPT3;

    private FolderAdapter    adapter;
    private List<FolderBean> dataList;
    private String           curPath;

    public static FileFragment newInstance(String type) {
        FileFragment myFragment = new FileFragment();

        Bundle args = new Bundle();
        args.putString(TYPE, type);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataList = new ArrayList<>();
        adapter = new FolderAdapter(dataList, false);
        adapter.setClickListener(new FolderAdapter.Click() {
            @Override
            public void onItemClick(int position) {
                switch (dataList.get(position).getFolder()) {
                    case PROJECT:
                        ((TedLocalActivity) getActivity()).finishForOpen(dataList.get(position).getPath(), true);
                        break;
                    case SCRIPT:
                        ((TedLocalActivity) getActivity()).finishForOpen(dataList.get(position).getPath(), false);
                        break;
                }
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        return inflater.inflate(R.layout.fragment_refresh_rv, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        boolean isQPy = NAction.isQPy3(getContext());
        initView();
        initListener();
        String type = getArguments().getString(TYPE);
        switch (type != null ? type : SCRIPT) {
            case SCRIPT:
                curPath = isQPy?SCRIPT_PATH3:SCRIPT_PATH;
                break;
            case PROJECT:
                curPath = isQPy?PROJECT_PATH3:PROJECT_PATH;
                break;
        }

        File[] files = new File(curPath).listFiles();
        boolean skip = false;
        if (files != null){
            for (File file : files) {
                skip = true;
                if (!file.getName().startsWith("."))
                    if (type == SCRIPT) {
                        if (file.isFile()) {
                            skip = false;
                        }
                    } else {
                        if (file.isDirectory()) {
                            if (new File(file.getAbsoluteFile()+"/main.py").exists()) {
                                skip = false;

                            }
                        }

                    }
                    if (!skip) {
                        dataList.add(new FolderBean(file));
                    }
            }
        }
        Collections.sort(dataList, (FolderBean o1, FolderBean o2) -> {
            String o1Up = o1.getName().toUpperCase();
            String o2Up = o2.getName().toUpperCase();
            return o1Up.compareTo(o2Up);
        });
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        binding.progressBar.setVisibility(View.GONE);

        SwipeMenuCreator swipeMenuCreator = (leftMenu, rightMenu, viewType) -> {
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
            rightMenu.addMenuItem(renameItem);
            rightMenu.addMenuItem(deleteItem);
        };

        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeList.setSwipeMenuCreator(swipeMenuCreator);
    }

    private void initListener() {
        binding.swipeList.setSwipeMenuItemClickListener(menuBridge -> {
            menuBridge.closeMenu();
            switch (menuBridge.getPosition()) {
                case 0:
                    // rename
                    new EnterDialog(getContext())
                            .setTitle(getString(R.string.rename))
                            .setConfirmListener(name -> {
                                File oldFile = dataList.get(menuBridge.getAdapterPosition()).getFile();
                                File newFile = new File(oldFile.getParent(), name);
                                boolean renameSuc = oldFile.renameTo(newFile);
                                if (renameSuc) {
                                    dataList.set(menuBridge.getAdapterPosition(), new FolderBean(newFile));
                                    adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                                    return true;
                                } else {
                                    Toast.makeText(getActivity(), R.string.rename_fail, Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            })
                            .setText(dataList.get(menuBridge.getAdapterPosition()).getName())
                            .show();
                    break;
                case 1:
                    // delete
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialog);
                    builder.setTitle(R.string.warning)
                            .setMessage(R.string.delete_file_hint)
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                FileHelper.clearDir(dataList.get(menuBridge.getAdapterPosition()).getFile().getAbsolutePath(), 0, true);
                                dataList.remove(menuBridge.getAdapterPosition());
                                adapter.notifyItemRemoved(menuBridge.getAdapterPosition());
                            })
                            .show();
                    break;
            }
        });
        binding.swipeList.setAdapter(adapter);
    }
}
