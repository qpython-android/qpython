package org.qpython.qpy.main.dialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.DialogRunProgramBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.event.RunProgramEvent;
import org.qpython.qpy.main.utils.Bus;
import org.qpython.qpy.plugin.SpaceItemDecoration;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunProgramDialog extends DialogFragment {
    public static final String TAG = "RunProgramDialog";

    public static final String PROGRAM_TYPE = "program_type";
    public static final int TYPE_SCRIPT = 0;
    public static final int TYPE_PROJECT = 1;
    private int type;

    private DialogRunProgramBinding mBinding;
    private Adapter mAdapter;

    public static RunProgramDialog newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(PROGRAM_TYPE, type);
        RunProgramDialog fragment = new RunProgramDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_run_program, container, false);
        RecyclerView rv = mBinding.rvProgram;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new SpaceItemDecoration(1));
        mAdapter = new Adapter();
        rv.setAdapter(mAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getInt(PROGRAM_TYPE);
        File dir = null;
        ArrayList<File> v = new ArrayList();

        if (type == TYPE_SCRIPT) {
            mBinding.tvTitle.setText(R.string.dialog_title_script);
            dir = new File(App.getScriptPath());
            File[] xx = dir.listFiles();
            for (int i=0;i<xx.length;i++) {
                if (xx[i].getAbsolutePath().endsWith(".py")) {
                    v.add(xx[i]);
                }
            }
        }
        if (type == TYPE_PROJECT) {
            mBinding.tvTitle.setText(R.string.dialog_title_project);
            dir = new File(App.getProjectPath());
            File[] xx = dir.listFiles();
            for (int i=0;i<xx.length;i++) {
                if (xx[i].isDirectory()) {
                    v.add(xx[i]);
                }
            }
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mAdapter.setData(v);
    }

    public class Adapter extends RecyclerView.Adapter<Holder> {
        private List<File> mList = new ArrayList<>();

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_run_program, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            File file = mList.get(position);
            holder.name.setText(file.getName());
            holder.itemView.setOnClickListener(v -> {
                if (type == TYPE_SCRIPT) {
                    Bus.getDefault().post(new RunProgramEvent(file.getPath(), false));
                }
                if (type == TYPE_PROJECT) {
                    Bus.getDefault().post(new RunProgramEvent(file.getPath(), true));
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setData(List<File> files) {
            mList.clear();
            mList.addAll(files);
            notifyDataSetChanged();
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView name;

        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
