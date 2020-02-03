package org.qpython.qpy.main.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import org.qpython.qpy.R;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.service.PayUtil;

import java.util.ArrayList;

/**
 * Created by Hmei
 * 1/30/18.
 */

public class PayActivity extends AppCompatActivity {
    public static final int BUY_REQUEST_CODE = 2333;
    private ArrayList<String>      skuList;
    private MySubscriber<String[]> callback;
    protected PayUtil payUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payUtil = new PayUtil(this);
    }

    protected void initIAB(ArrayList<String> skuList, MySubscriber<String[]> callback) {
        this.skuList = skuList;
        this.callback = callback;
        payUtil.initIAP(() -> payUtil.getPrices(skuList,callback));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_menu) {
            if (findViewById(R.id.pb) != null) findViewById(R.id.pb).setVisibility(View.VISIBLE);
            payUtil.getPrices(skuList, callback);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        payUtil.unbindPayService();
    }

}
