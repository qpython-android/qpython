package org.qpython.qpy.main.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.main.service.PayUtil;

import rx.Observer;

/**
 * Created by Hmei
 * 1/31/18.
 */

public class PayActivity extends AppCompatActivity {
    protected static final String ARTICLE_ID      = "article_id";
    protected String mOrderNo;
    protected PayUtil payUtil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payUtil = new PayUtil(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onPayResp(String msg) {
//        if (msg.equals(PaymentStatus.SUCCESS)) {
//            payUtil.checkOrderStatus(mOrderNo,new Observer<PaymentStatus>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    e.printStackTrace();
//                    if (findViewById(R.id.pb) != null)
//                        findViewById(R.id.pb).setVisibility(View.GONE);
//                    Toast.makeText(PayActivity.this, "支付失败！", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onNext(PaymentStatus paymentStatus) {
//                    if (paymentStatus.isSuccess()) {
//                        if (findViewById(R.id.pb) != null)
//                            findViewById(R.id.pb).setVisibility(View.GONE);
//                        Toast.makeText(PayActivity.this, "感谢您的支持！", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        payUtil.checkOrderStatus(mOrderNo,this);
//                    }
//                }
//            });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
