package org.qpython.qpy.plugin;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.qpython.qpy.R;



public class PluginDetailDialogFragment extends DialogFragment {
    public static final String TAG = "PluginDetailDialogFragment";
    private static final String TITLE = "title";
    private static final String MSG = "msg";

    public static PluginDetailDialogFragment newInstance(String title, String msg) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MSG, msg);
        PluginDetailDialogFragment fragment = new PluginDetailDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String msg = getArguments().getString(MSG);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_plugin_detail_positive, (dialog, which) -> dismiss())
                .create();
    }
}
