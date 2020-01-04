package org.qpython.qpy.main.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.zxing.Result;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.quseit.base.QBaseApp;
import com.quseit.util.NUtil;

import org.apache.http.Header;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.texteditor.EditorActivity;

import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    public static void start(Context context) {
        Intent starter = new Intent(context, QrCodeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        setTitle(R.string.read_script_from_qrcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        mScannerView = (ZXingScannerView) findViewById(R.id.scanner);
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

    private void close() {
        this.finish();
    }
    private void linkToScanURL(String scanResult) {
        QrCodeActivity.this.close();

        QWebViewActivity.start(this, "QWebView", scanResult);

    }
    @Override
    public void handleResult(Result result) {
        String scanResult = result.getText();
        if (scanResult.startsWith("http://") || scanResult.startsWith("https://")) {
            // Handle url
            new AlertDialog.Builder(this)
                    .setTitle(R.string.qr_info)
                    .setMessage(getString(R.string.qr_goto)+"\n\n"+scanResult)
                    .setNegativeButton(R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton(R.string.ok, (dialog1, which) -> linkToScanURL(scanResult))
                    .create()
                    .show();

        } else if (scanResult.startsWith("qwe://")) {
            // start the qpy.io editor
            if (scanResult.contains("token=")) {
                String qweLink = scanResult.replace("qwe://", "http://") + "&ip=" + NUtil.getIPAddress(true) + ":10000";
                Map<String, List<String>> ps = NUtil.getQueryParams(qweLink);
                String token = "";
                if (ps.get("token") != null) {
                    token = ps.get("token").get(0);
                }
                ScriptExec.getInstance().playScript(this,getApplicationContext().getFilesDir() + "/bin/qedit4web.py", token, false);
                QBaseApp.getInstance().getAsyncHttpClient().get(this, qweLink, null, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Toast.makeText(getApplicationContext(), "QWE Service started", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "QWE Service failed to start", Toast.LENGTH_SHORT).show();

                    }

                });
            } else {
                Toast.makeText(getApplicationContext(), "Failed to parse token", Toast.LENGTH_SHORT).show();
            }
            finish();

        } else {
            EditorActivity.start(this, result.getText(), "FromQRCode");

            finish();

        }
    }
}
