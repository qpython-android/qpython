package org.qpython.qpy.main.dialog;

import android.support.v4.app.DialogFragment;

//import org.qpython.qpy.databinding.DialogLogoBinding;
import org.qpython.qpy.main.activity.HomeMainActivity;

public class LogoDialog extends DialogFragment {
    public static final String TAG = "LogoDialog";
    //private DialogLogoBinding mBinding;

    public static LogoDialog newInstance() {
        return new LogoDialog();
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_logo, container, false);
//
//        mBinding.btnQrcode.setOnClickListener(v -> {
//            Bus.getDefault().post(new HomeMainActivity.StartQrCodeActivityEvent());
//            dismiss();
//        });
//        mBinding.btnScript.setOnClickListener(v -> {
//            Bus.getDefault().post(new HomeMainActivity.ShowProgramDialogEvent(false));
//            dismiss();
//        });
//        mBinding.btnProject.setOnClickListener(v -> {
//            Bus.getDefault().post(new HomeMainActivity.ShowProgramDialogEvent(true));
//            dismiss();
//        });
//
//        return mBinding.getRoot();
//    }
}
