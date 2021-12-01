package org.qpython.qpy.main.fragment;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.quseit.common.updater.downloader.Downloader;
import com.quseit.util.ACache;
import com.quseit.util.FileUtils;
import com.quseit.util.ImageUtil;
import com.quseit.util.NAction;
import com.quseit.util.NetStateUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.console.TermActivity;
import org.qpython.qpy.databinding.FragmentRefreshRvBinding;
import org.qpython.qpy.main.adapter.LibListAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.server.CacheKey;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.LibModel;
import org.qpython.qpy.main.utils.Utils;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Lib/Component list Fragment
 * Created by Hmei on 2017-05-27.
 */

public class LibProjectFragment extends RefreshFragment {
    private static final int SCRIPT_CONSOLE_CODE = 1237;
    private static String SCRIPT_DIR;       //   = QPyConstants.ABSOLUTE_PATH + "/" + QPyConstants.DFROM_QPY2 + "/";
    private static String PROJECT_DIR;      //  = QPyConstants.ABSOLUTE_PATH + "/" + QPyConstants.DFROM_PRJ2 + "/";

    private List<LibModel> dataList;
    private LibListAdapter<LibModel> adapter;
    private FragmentRefreshRvBinding binding;
    private TextView header;

    private int WIDTH = (int) ImageUtil.dp2px(60);

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_rv, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);

        SCRIPT_DIR = FileUtils.getAbsolutePath(App.getContext()) + "/" + (NAction.isQPy3(getActivity()) ? QPyConstants.DFROM_QPY3 : QPyConstants.DFROM_QPY2) + "/";
        PROJECT_DIR = FileUtils.getAbsolutePath(App.getContext()) + "/" + (NAction.isQPy3(getActivity()) ? QPyConstants.DFROM_PRJ3 : QPyConstants.DFROM_PRJ2) + "/";

        initDataList();
        initView();
        initListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initDataList() {
        boolean isSaverOn = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.key_saver), true);
        dataList = new ArrayList<>();
        adapter = new LibListAdapter<>(dataList);
        refresh(!isSaverOn);
    }

    @Override
    public void refresh(boolean forceRefresh) {
        dataList.clear();
        adapter.notifyDataSetChanged();
        binding.netError.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        App.getService().getLibs(forceRefresh, new MySubscriber<List<LibModel>>() {
            @Override
            public void onCompleted() {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeList.setVisibility(View.VISIBLE);
                if (header != null) {
                    header.setText(getString(R.string.last_refresh, ACache.get(getContext()).getAsString(CacheKey.LIB_LAST_REFRESH)));
                }
            }

            @Override
            public void onError(Throwable e) {
                binding.swipeList.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.netError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<LibModel> libModels) {
                for (LibModel lib : libModels) {
                    if (new File(SCRIPT_DIR + lib.getSmodule()).exists() || new File(PROJECT_DIR + lib.getSmodule()).exists()) {
                        lib.setInstalled(true);
                    }
                }
                ACache.get(getContext()).put(CacheKey.LIB, tostring(libModels));
                dataList.addAll(libModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        binding.swipeList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.swipeList.setSwipeMenuCreator(getMenu());

        View installView = getActivity().getLayoutInflater().inflate(R.layout.header_lib_install, binding.swipeList, false);
        installView.setOnClickListener(v -> {
            if (NetStateUtil.isConnected(getContext())) {
                openPip();
            } else {
                Toast.makeText(getContext(), R.string.pip_no_net, Toast.LENGTH_SHORT).show();
            }
        });
        binding.swipeList.addHeaderView(installView);

        binding.progressBar.getIndeterminateDrawable().setColorFilter(0xFF4BAC07, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void openPip() {
        File pip = new File(getContext().getApplicationContext().getFilesDir() + "/bin/qpypi.py");
        if (pip.exists()) {
            String[] args = {getContext().getApplicationContext().getFilesDir() + "/bin/qpypi.py", getContext().getApplicationContext().getFilesDir().toString()};
            Intent intent = new Intent(getContext(), TermActivity.class);
            intent.putExtra(TermActivity.ARGS, args);
            startActivity(intent);
        }
    }

    private SwipeMenuCreator getMenu() {
        SwipeMenuItem detail = new SwipeMenuItem(getContext())
                .setBackgroundColor(Color.parseColor("#FF4A4A4A"))
                .setImage(R.drawable.ic_library_detail)
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(WIDTH);

        return (swipeLeftMenu, swipeRightMenu, viewType) -> {
            switch (viewType) {
                case LibListAdapter.INSTALLED:
                    SwipeMenuItem reInstall = new SwipeMenuItem(getContext())
                            .setBackgroundColor(Color.parseColor("#FF595959"))
                            .setImage(R.drawable.ic_library_run)
                            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                            .setWidth(WIDTH);

                    SwipeMenuItem delete = new SwipeMenuItem(getContext())
                            .setBackgroundColor(Color.parseColor("#FFD2483D"))
                            .setImage(R.drawable.ic_library_delete)
                            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                            .setWidth(WIDTH);

                    swipeRightMenu.addMenuItem(reInstall);
                    swipeRightMenu.addMenuItem(detail);
                    swipeRightMenu.addMenuItem(delete);
                    break;
                case LibListAdapter.UN_INSTALLED:
                    SwipeMenuItem download = new SwipeMenuItem(getContext())
                            .setBackgroundColor(Color.parseColor("#FFECCD00"))
                            .setImage(R.drawable.ic_library_download)
                            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                            .setWidth(WIDTH);
                    swipeRightMenu.addMenuItem(detail);
                    swipeRightMenu.addMenuItem(download);
                    break;
            }
        };
    }


    private void installTool(LibModel item) {
        String downloadDir = null;
        if (item.getCat().equals("script")) {
            downloadDir = "qpython/" + (NAction.isQPy3(getActivity()) ? QPyConstants.DFROM_QPY3 : QPyConstants.DFROM_QPY2);
        } else if (item.getCat().equals("user")) {
            downloadDir = "qpython/" + (NAction.isQPy3(getActivity()) ? QPyConstants.DFROM_QPY3 : QPyConstants.DFROM_QPY2);
        }

        // Download
        App.getDownloader().download(item.getTitle(), item.getLink(), FileUtils.getPath(App.getContext()) + "/" + downloadDir + "/" + item.getSmodule(),
                new Downloader.Callback() {
                    @Override
                    public void pending(String name) {

                    }

                    @Override
                    public void complete(String name, File installer) {
                        refresh(true);
                    }

                    @Override
                    public void error(String err) {

                    }
                });
    }

    private void initListener() {
        adapter.setClick(position -> binding.swipeList.smoothOpenRightMenu(position));
        binding.swipeList.setSwipeMenuItemClickListener(menuBridge -> {
            menuBridge.closeMenu();
            LibModel item = dataList.get(menuBridge.getAdapterPosition());
            if (!dataList.get(menuBridge.getAdapterPosition()).isInstalled()) {
                // UN INSTALL
                switch (menuBridge.getPosition()) {
                    case 0:
                        // detail
                        if (item.getSrc().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            AlertDialog alertDialog = builder.setTitle(R.string.location)
                                    .setMessage(CONF.qpypiPath() + "/" + item.getTitle())
                                    .setPositiveButton(R.string.confirm, null)
                                    .create();
                            alertDialog.show();
                        } else {
                            Utils.startWebActivityWithUrl(getActivity(), item.getTitle(), item.getSrc());

                        }
                        break;
                    case 1:
                        // download
                        installTool(item);
                        break;
                }
            } else {
                // INSTALLED
                switch (menuBridge.getPosition()) {
                    case 0:
                        // run
                        String path = "";
                        if (item.getCat().equals("script")) {
                            path = SCRIPT_DIR + dataList.get(menuBridge.getAdapterPosition()).getSmodule();
                        } else if (item.getCat().equals("user")) {
                            path = PROJECT_DIR + dataList.get(menuBridge.getAdapterPosition()).getSmodule();
                        }
                        ScriptExec.getInstance().playScript(getContext(), path, "", true);
                        break;
                    case 1:
                        // detail
                        if (item.getSrc().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            AlertDialog alertDialog = builder.setTitle(R.string.location)
                                    .setMessage(CONF.qpypiPath() + "/" + item.getTitle())
                                    .setPositiveButton(R.string.confirm, null)
                                    .create();
                            alertDialog.show();
                        } else {
                            Utils.startWebActivityWithUrl(getActivity(), item.getTitle(), item.getSrc());

                        }
                        break;
                    case 2:
                        if (new File(SCRIPT_DIR + "/" + item.getSmodule()).delete()) {
                            dataList.get(menuBridge.getAdapterPosition()).setInstalled(false);
                            adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                        } else {
                            Toast.makeText(getContext(), R.string.delete_fail, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        binding.swipeList.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CreateLibFinishEvent event) {
        for (LibModel libModel : dataList) {
            if (libModel.getSmodule().equals(event.fileName)) {
                libModel.setInstalled(true);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void execPyInConsole(String[] args) {
        Intent intent = new Intent(getContext(), TermActivity.class);
        intent.putExtra("PYTHONARGS", args);
        startActivityForResult(intent, SCRIPT_CONSOLE_CODE);
    }

    public static class CreateLibFinishEvent {
        public String fileName;
    }
}