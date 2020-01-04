package org.qpython.qpy.main.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemLibListBinding;
import org.qpython.qpy.main.server.model.BaseLibModel;
import org.qpython.qpy.main.server.model.LibModel;
import org.qpython.qpy.texteditor.ui.adapter.MyViewHolder;

import java.util.List;
import java.util.Map;

/**
 * lib list adapter
 * Created by Hmei on 2017-05-27.
 */

public class LibListAdapter<T extends BaseLibModel> extends RecyclerView.Adapter<MyViewHolder<ItemLibListBinding>> {
    private static Map<String, String> nameVer;

    public static final int INSTALLED    = 0;
    public static final int UN_INSTALLED = 1;
    public static final int UPGRADE      = 2;

    public static final String TAG = "LibListAdapter";

    private List<T> dataList;
    private Click   click;

    public LibListAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public MyViewHolder<ItemLibListBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        View realContentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lib_list, parent, false);
        MyViewHolder<ItemLibListBinding> holder = new MyViewHolder<>(realContentView);
        holder.setBinding(DataBindingUtil.bind(realContentView));
        return holder;
    }

    @Override
    public void onBindViewHolder(org.qpython.qpy.texteditor.ui.adapter.MyViewHolder<ItemLibListBinding> holder, int position) {
        ItemLibListBinding binding = holder.getBinding();
        T item = dataList.get(position);
        if (!item.isInstalled()) {
            if (item instanceof LibModel) {
                binding.ivIcon.setImageResource(R.drawable.ic_program);
                binding.tvDescription.setText(item.getDescription());
            } else {
                binding.ivIcon.setImageResource(R.drawable.ic_library);
                binding.tvDescription.setText(item.getVer());
            }
        } else {
            if (item instanceof LibModel) {
                binding.ivIcon.setImageResource(R.drawable.ic_program_install);
                binding.tvDescription.setText(item.getDescription());
            } else {
                binding.ivIcon.setImageResource(R.drawable.ic_library_install);
                binding.tvDescription.setText(item.getVer());
            }
        }
        if (item.getDownloads().equals("-1")) {
            binding.tvDownloadCount.setVisibility(View.GONE);
        } else {
            binding.tvDownloadCount.setText(String.valueOf(item.getDownloads()));
        }
        binding.tvName.setText(item.getTitle());
        binding.llItem.setOnClickListener(v -> {
            if (click != null) {
                click.itemClick(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof LibModel) {
            // Project
            return dataList.get(position).isInstalled() ? INSTALLED : UN_INSTALLED;
        } else {
            // QPYPI / AIPY
            if (!dataList.get(position).isInstalled()) {
                return UN_INSTALLED;
            } else {
//                String[] newVer = dataList.get(position).getVer().split("\\.");
//                String[] curVer;
//                String cur = getPackageVersion(dataList.get(position).getSmodule());
//                if (cur != null) {
//                    curVer = cur.split("\\.");
//                } else {
//                    return UN_INSTALLED;
//                }
//                for (int i = 0; i < newVer.length; i++) {
//                    if (Integer.parseInt(newVer[i]) > Integer.parseInt(curVer[i])) {
//                        return UPGRADE;
//                    }
//                }
                return INSTALLED;
            }
        }
    }

    public void setClick(Click click) {
        this.click = click;
    }

    public interface Click {
        void itemClick(int position);
    }

//    private String getPackageVersion(String sModule) {
//        if (nameVer == null) {
//            initNameVer();
//        }
//        return nameVer.get(sModule);
//    }
//
//    private void initNameVer() {
//        nameVer = new HashMap<>();
//        for (File file : new File(org.qpython.qpy.main.app.CONF.qpypiPath()).listFiles()) {
//            LogUtil.d(TAG, file.getAbsolutePath());
//            if (file.getName().endsWith("-py"+ CONF.PY_BRANCH+".egg")) {
//                String name = file.getName();
//                String[] splited = name.split("-");
//                if (splited.length > 2) nameVer.put(splited[0].replace("_","-"), splited[1]);
//            }
//        }
//    }
}