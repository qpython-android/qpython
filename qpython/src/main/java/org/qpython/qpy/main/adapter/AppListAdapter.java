package org.qpython.qpy.main.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.databinding.ItemAppListBinding;
import org.qpython.qpy.main.activity.AppListActivity;
import org.qpython.qpy.main.model.AppModel;
import org.qpython.qpy.main.model.LocalAppModel;
import org.qpython.qpy.main.model.QPyScriptModel;
import org.qpython.qpy.texteditor.EditorActivity;
import org.qpython.qpy.texteditor.ui.view.EnterDialog;
import android.support.v7.app.AlertDialog;


import java.util.List;

/**
 * App list adapter
 * Created by Hmei on 2017-05-25.
 */

public class AppListAdapter extends RecyclerView.Adapter<MyViewHolder<ItemAppListBinding>> {
    private static final String TYPE_SCRIPT = "script";
    private static final String TAG = "AppListAdapter";

    private List<AppModel> dataList;
    private String         type;
    private Context        context;
    private Callback       callback;

    public AppListAdapter(List<AppModel> dataList, String type, Context context) {
        this.dataList = dataList;
        this.type = type;
        this.context = context;
    }

    @Override
    public MyViewHolder<ItemAppListBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAppListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_app_list, parent, false);
        MyViewHolder<ItemAppListBinding> holder = new MyViewHolder<>(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder<ItemAppListBinding> holder, int position) {
        ItemAppListBinding binding = holder.getBinding();
        if (type.equals(TYPE_SCRIPT) & position == dataList.size()) {
            binding.ciAppIcon.setImageResource(R.drawable.ic_home_qpy_add);
            binding.tvAppName.setText(R.string.add);
        } else {
            binding.ciAppIcon.setImageDrawable(dataList.get(position).getIcon());
            binding.tvAppName.setText(dataList.get(position).getLabel());
        }
        binding.getRoot().setOnClickListener(v -> {
            if (position == dataList.size()) {
                EditorActivity.start(context, "");
            } else if (dataList.get(position) instanceof QPyScriptModel) {
                QPyScriptModel qPyScriptItem = (QPyScriptModel) dataList.get(position);
                if (qPyScriptItem.isProj()) {
                    callback.runProject(qPyScriptItem);
                } else {
                    callback.runScript(qPyScriptItem);
                }
            } else if (dataList.get(position) instanceof LocalAppModel) {
                LocalAppModel localAppItem = (LocalAppModel) dataList.get(position);
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(localAppItem.getApplicationPackageName());
                if (intent != null) {
                    context.startActivity(intent);
                }
            } else {

                Log.d(TAG, "ONCLICK");
                callback.exit();
            }
        });

        binding.getRoot().setOnLongClickListener(v -> {
            if (position != dataList.size()) {
                CharSequence[] chars = new CharSequence[]{context.getString(R.string.create_shortcut), context.getString(R.string.run_with_params), context.getString(R.string.open_with_editor)};

                final boolean[] isConsumed = new boolean[]{true};
                new AlertDialog.Builder(context, R.style.MyDialog)
                        .setTitle(R.string.choose_action)
                        .setItems(chars, (dialog, which) -> {
                            switch (which) {
                                case 0: // Create Shortcut
                                    isConsumed[0] = createShortCut(position);
                                    dialog.dismiss();
                                    break;
                                case 1: // Run with params
                                    runWithParams(position);
                                    dialog.dismiss();
                                    break;
                                case 2:
                                    openToEdit(position);
                                    dialog.dismiss();

                            }
                        }).setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                        .show();
                return isConsumed[0];
            } else {
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : type.equals(TYPE_SCRIPT) ? dataList.size() + 1 : dataList.size();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void runScript(QPyScriptModel item);

        void runProject(QPyScriptModel item);

        void exit();
    }

    private boolean createShortCut(int position) {
        //最后一个为添加script，不用创建快键图标
        if (position == dataList.size()) {
            return false;
        }
        // Create shortcut
        QPyScriptModel qPyScriptModel = (QPyScriptModel) dataList.get(position);
        Intent intent = new Intent();
        intent.setClass(context, AppListActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("type", "script");
        intent.putExtra("path", qPyScriptModel.getPath());
        intent.putExtra("isProj", qPyScriptModel.isProj());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager mShortcutManager = context.getSystemService(ShortcutManager.class);
            if (mShortcutManager.isRequestPinShortcutSupported()) {
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(context, dataList.get(position).getLabel())
                                .setShortLabel(dataList.get(position).getLabel())
                                .setLongLabel(dataList.get(position).getLabel())
                                .setIcon(Icon.createWithResource(context, dataList.get(position).getIconRes()))
                                .setIntent(intent)
                                .build();
                Intent pinnedShortcutCallbackIntent =
                        mShortcutManager.createShortcutResultIntent(pinShortcutInfo);
                PendingIntent successCallback = PendingIntent.getBroadcast(context, 0,
                        pinnedShortcutCallbackIntent, 0);
                mShortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
            }
        } else {
            //Adding shortcut for MainActivity
            //on Home screen
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, dataList.get(position).getLabel());
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(context.getApplicationContext(),
                            dataList.get(position).getIconRes()));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.getApplicationContext().sendBroadcast(addIntent);
            Toast.makeText(context, context.getString(R.string.shortcut_create_suc, dataList.get(position).getLabel()), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void openToEdit(int position) {
        QPyScriptModel model = (QPyScriptModel) dataList.get(position);
        if (model.isProj()) {

            EditorActivity.start(model.getPath(), context);

        } else {
            //Intent intent = new Intent();
            //model.getPath()
            EditorActivity.start(context, Uri.parse("file://"+model.getPath()));
        }
    }

    private void runWithParams(int position) {
        new EnterDialog(context)
                .setTitle(context.getString(R.string.enter_u_params))
                .setHint(context.getString(R.string.params))
                .setConfirmListener(args -> {
                    if (TextUtils.isEmpty(args)) {
                        Toast.makeText(context, R.string.params_emp, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    QPyScriptModel model = (QPyScriptModel) dataList.get(position);
                    if (model.isProj()) {
                        ScriptExec.getInstance().playProject(context, model.getPath(), args, false);
                    } else {
                        ScriptExec.getInstance().playScript(context, model.getPath(),
                                args, false);
                    }
                    return true;
                })
                .show();
    }
}
