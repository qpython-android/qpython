package org.qpython.qpy.main.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "QrCodeActivity";
    private ZXingScannerView mScannerView;

    public static void start(Context context) {
        Intent starter = new Intent(context, QrCodeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        //EditorMainActivity.startActivity(this, result.getText());
        finish();
    }
}
