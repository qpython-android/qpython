package org.qpython.qpy.main.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.quseit.util.FileHelper;
import com.quseit.util.ImageUtil;
import com.quseit.util.NAction;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;

import org.qpython.qpy.R;
import org.qpython.qpy.console.TermActivity;
import org.qpython.qpy.main.adapter.LibListAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.CONF;
import org.qpython.qpy.main.server.model.BaseLibModel;
import org.qpython.qpy.main.utils.Utils;

import java.util.List;


public abstract class RefreshFragment extends Fragment {
    private static final int SCRIPT_CONSOLE_CODE = 1237;
    private              int WIDTH               = (int) ImageUtil.dp2px(60);
    public SwipeMenuItemClickListener listener;
    public int installPosition = -1; // For update download state

    public abstract void refresh(boolean forceRefresh);

    String tostring(Object obj) {
        return App.getGson().toJson(obj);
    }

    public String getDownloadPath(String sModule) {
        return CONF.qpypiPath() +
                sModule;
    }

    public SwipeMenuCreator getTypeMenu() {
        SwipeMenuItem detail = new SwipeMenuItem(getContext())
                .setBackgroundColor(Color.parseColor("#FF4A4A4A"))
                .setImage(R.drawable.ic_library_detail)
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(WIDTH);
        SwipeMenuItem delete = new SwipeMenuItem(getContext())
                .setBackgroundColor(Color.parseColor("#FFD2483D"))
                .setImage(R.drawable.ic_library_delete)
                .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                .setWidth(WIDTH);

        return (swipeLeftMenu, swipeRightMenu, viewType) -> {
            switch (viewType) {
                case LibListAdapter.INSTALLED:
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
                case LibListAdapter.UPGRADE:
                    SwipeMenuItem upgrade = new SwipeMenuItem(getContext())
                            .setBackgroundColor(Color.parseColor("#FF595959"))
                            .setImage(R.drawable.ic_library_upgrade)
                            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                            .setWidth(WIDTH);
                    swipeRightMenu.addMenuItem(upgrade);
                    swipeRightMenu.addMenuItem(detail);
                    swipeRightMenu.addMenuItem(delete);
                    break;
            }
        };
    }

    public SwipeMenuItemClickListener getListener(List<? extends BaseLibModel> dataList, RecyclerView.Adapter adapter) {
        return listener = menuBridge -> {
            menuBridge.closeMenu();
            BaseLibModel item = dataList.get(menuBridge.getAdapterPosition());
            switch (adapter.getItemViewType(menuBridge.getAdapterPosition())) {
                case LibListAdapter.INSTALLED:
                    switch (menuBridge.getPosition()) {
                        case 0:
                            // detail
                            openDetail(item);
                            break;
                        case 1:
                            // delete
                            FileHelper.clearDir(getDownloadPath(item.getTmodule()), 0, true);
                            item.setInstalled(false);
                            adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                            break;
                    }
                    break;
                case LibListAdapter.UPGRADE:
                    switch (menuBridge.getPosition()) {
                        case 0:
                            // upgrade
                            FileHelper.clearDir(getDownloadPath(item.getTmodule()), 0, true);
                            item.setInstalled(false);
                            adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                            installPosition = menuBridge.getAdapterPosition();
                            downloadLib(item);
                            item.setInstalled(true);
                            adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                            break;
                        case 1:
                            // detail
                            openDetail(item);
                            break;
                        case 2:
                            // delete
                            FileHelper.clearDir(getDownloadPath(item.getSmodule()), 0, true);
                            item.setInstalled(false);
                            adapter.notifyItemChanged(menuBridge.getAdapterPosition());
                    }
                    break;
                case LibListAdapter.UN_INSTALLED:
                    // UN INSTALL
                    switch (menuBridge.getPosition()) {
                        case 0:
                            // detail
                            openDetail(item);
                            break;
                        case 1:
                            // download
                            installPosition = menuBridge.getAdapterPosition();
                            downloadLib(item);
                            break;
                    }
                    break;
            }

        };
    }

    private void openDetail(BaseLibModel item) {
        if (item.getSrc().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            AlertDialog alertDialog = builder.setTitle(R.string.location)
                    .setMessage(CONF.qpypiPath() + "/" + item.getTitle())
                    .setPositiveButton(R.string.confirm, null)
                    .create();
            alertDialog.show();
        } else {

            Utils.startWebActivityWithUrl(getActivity(), item.getTitle(), item.getSrc());

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getSrc()));
//            startActivity(browserIntent);
        }
    }

    private void downloadLib(BaseLibModel item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String QPyPi = sharedPreferences.getString(getString(R.string.key_qpypi), CONF.QPYPI_URL);
        String DIR = getContext().getApplicationContext().getFilesDir().getAbsolutePath();
        String[] args = {DIR+"/bin/qpypi.py", "install", item.getSmodule()};

//        String[] args = {getContext().getApplicationContext().getFilesDir() + ("/bin/pip"+(
//                NAction.isQPy3(getContext())?"3":"")),"install", "-i", item
//                .getLink(), "--extra-index-url", QPyPi,  "--trusted-host", "qpypi"+(NAction.isQPy3(getContext())?"3":"")+".qpython.org","-U",  item.getSmodule()};
        execPyInConsole(args);
    }

    public void execPyInConsole(String[] args) {
        Intent intent = new Intent(getContext(), TermActivity.class);
        intent.putExtra(TermActivity.ARGS, args);
        startActivityForResult(intent, SCRIPT_CONSOLE_CODE);
    }
}
