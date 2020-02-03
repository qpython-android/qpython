package org.qpython.qpy.main.service;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

//import com.hipipal.qpyplus.wxapi.PaymentStatus;
//import com.hipipal.qpyplus.wxapi.WXAPIManager;
//import com.hipipal.qpyplus.wxapi.WeixinPay;

import org.qpython.qpy.R;
import org.qpython.qpy.main.activity.PayActivity;
import org.qpython.qpy.main.activity.SignInActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.User;
import org.qpython.qpy.main.server.MySubscriber;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hmei
 * 1/31/18.
 */

public class PayUtil {
    private static final String             GET_ORDER_URL   = "https://api.wegox.net/create_order";
    public static final  String             CHECK_ORDER_URL = "https://api.wegox.net/check_order";
    private              List<Subscription> mSubscriptions  = new ArrayList<>();

    private int checkCount = 0;
    private Activity context;

    public PayUtil(Activity context) {
        this.context = context;
    }

    public void initIAP(PayCallback callback) {
        callback.doAfterConn();
    }

    /**
     * 赞赏
     */
//    public void purchase(String price, String articleId, Observer<WeixinPay> callback) {
//        User user = App.getUser();
//        if (user == null) {
//            context.startActivity(new Intent(context, SignInActivity.class));
//            return;
//        }
//        String userName = user.getUserName();
//        String userId = user.getUserId();
//        WXAPIManager.getWXService().requestPay(GET_ORDER_URL, price, userName, userId, articleId)
//                .subscribeOn(Schedulers.io())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(callback);
//    }

    /**
     * 查询订单
     */
//    public void checkOrderStatus(String mOrderNo, Observer<PaymentStatus> callback) {
//        checkCount++;
//        if (checkCount < 31) {
//            Subscription subscription = WXAPIManager.getWXService().checkStatus(CHECK_ORDER_URL, mOrderNo)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(callback);
//            mSubscriptions.add(subscription);
//        } else {
//            if (context.findViewById(R.id.pb) != null)
//                context.findViewById(R.id.pb).setVisibility(View.GONE);
//            Toast.makeText(context, "支付失败！", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void unbindPayService() {
        for (Subscription subscription : mSubscriptions) {
            if (subscription != null && subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    public interface PayCallback {
        void doAfterConn();
    }
}
