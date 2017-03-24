package com.quseit.common.updater.callback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.quseit.common.updater.R;
import com.quseit.common.updater.Updater;
import com.quseit.common.updater.updatepkg.UpdatePackage;

import java.util.List;

public class DialogCallback implements Updater.CheckUpdateCallback {
    private FragmentActivity mActivity;
    private boolean mIsSilence;

    public DialogCallback(FragmentActivity activity, boolean isSilence) {
        mActivity = activity;
        mIsSilence = isSilence;
    }

    @Override
    public void hasUpdate(List<UpdatePackage> pkgs) {
        StringBuilder description = new StringBuilder();
        for (UpdatePackage pkg : pkgs) {
            description
                    .append(pkg.getName())
                    .append("：")
                    .append("\n")
                    .append(pkg.getVersionDescription())
                    .append("\n\n");
        }
        description.delete(description.length() - 2, description.length());

        DialogFragment dialog = SimpleReminderDialogFragment.newInstance(description.toString());
        try {
            dialog.show(mActivity.getSupportFragmentManager(), SimpleReminderDialogFragment.TAG);
        } catch (Exception e) {
            // mActivity 不可用时忽略
        }
    }

    @Override
    public void noneUpdate() {
        try {
            if (!mIsSilence) {
                Toast.makeText(mActivity, R.string.latest_version, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // mActivity 不可用时忽略
        }
    }

    @Override
    public void error(Throwable e) {
        try {
            if (!mIsSilence) {
                Toast.makeText(mActivity, R.string.check_update_error, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e1) {
            // mActivity 不可用时忽略
        }
    }

    public static class SimpleReminderDialogFragment extends DialogFragment {
        public static final String TAG = "SimpleReminderDialogFragment";
        private static final String DESCRIPTION = "description";

        public static SimpleReminderDialogFragment newInstance(String description) {
            Bundle args = new Bundle();
            args.putString(DESCRIPTION, description);
            SimpleReminderDialogFragment fragment = new SimpleReminderDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.has_update)
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Updater.update();
                            dismiss();
                        }
                    })
                    .setNegativeButton(R.string.show_detail, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String description = getArguments().getString(DESCRIPTION);
                            DialogFragment dialog = DetailReminderDialogFragment.newInstance(description);
                            dialog.show(getFragmentManager(), DetailReminderDialogFragment.TAG);
                            dismiss();
                        }
                    })
                    .create();
        }
    }

    public static class DetailReminderDialogFragment extends DialogFragment {
        public static final String TAG = "DetailReminderDialogFragment";
        private static final String DESCRIPTION = "description";

        public static DetailReminderDialogFragment newInstance(String description) {
            Bundle args = new Bundle();
            args.putString(DESCRIPTION, description);
            DetailReminderDialogFragment fragment = new DetailReminderDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.has_update)
                    .setMessage(getArguments().getString(DESCRIPTION))
                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Updater.update();
                            dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    })
                    .create();
        }
    }
}
