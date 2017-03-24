package org.qpython.qpy.plugin.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.qpython.qpy.R;

import org.qpython.qpy.plugin.LocalPluginManager;
import org.qpython.qpy.plugin.PluginDetailDialogFragment;
import org.qpython.qpy.plugin.model.LocalPluginBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalPluginAdapter extends RecyclerView.Adapter<LocalPluginAdapter.PluginHolder> {
    private Context mContext;
    private List<LocalPluginBean> mList;

    public LocalPluginAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public PluginHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_plugin_local, null);
        return new PluginHolder(v);
    }

    @Override
    public void onBindViewHolder(PluginHolder holder, int position) {
        LocalPluginBean pluginInfo = mList.get(position);
        holder.name.setText(pluginInfo.getName());
        holder.description.setText(pluginInfo.getTitle());
        PopupMenu menu = new PopupMenu(mContext, holder.menu);
        menu.inflate(R.menu.plugin_installed_menu);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.detail:
                    showDetail(pluginInfo);
                    break;
                case R.id.uninstall:
                    uninstall(pluginInfo);
                    break;
            }

            return true;
        });

        holder.itemView.setOnClickListener(v -> menu.show());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private void showDetail(LocalPluginBean plugin) {
        FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        PluginDetailDialogFragment.newInstance(plugin.getName(), LocalPluginManager.getCompletePath(plugin))
                .show(manager, PluginDetailDialogFragment.TAG);
    }

    private void uninstall(LocalPluginBean plugin) {
        mList.remove(plugin);
        LocalPluginManager.uninstall(plugin);
    }

    public void setData(List<LocalPluginBean> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public static class PluginHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.plugin_name)
        TextView name;

        @BindView(R.id.plugin_description)
        TextView description;

        @BindView(R.id.plugin_menu)
        ImageView menu;

        public PluginHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
