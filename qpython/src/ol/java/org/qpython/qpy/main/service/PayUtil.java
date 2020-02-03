package org.qpython.qpy.main.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.main.server.MySubscriber;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hmei
 * 1/31/18.
 */

public class PayUtil {
    private static final int BUY_REQUEST_CODE = 2333;
    private IInAppBillingService mService;
    private ServiceConnection    mServiceConn;
    private Activity             context;

    public PayUtil(Activity context) {
        this.context = context;
    }

    public void initIAP(PayCallback callback) {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                if (callback != null) callback.doAfterConn();
            }
        };
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 从Google服务器获取不同国家价格表并显示
     */
    public void getPrices(ArrayList<String> skuList, MySubscriber<String[]> callback) {
        if (mService == null) {
            Toast.makeText(context, R.string.lose_google_server, Toast.LENGTH_SHORT).show();
            if (context.findViewById(R.id.pb) != null)
                context.findViewById(R.id.pb).setVisibility(View.GONE);
            return;
        }
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            Observable.just(mService.getSkuDetails(3, context.getPackageName(), "inapp", querySkus))
                    .map(bundle -> {
                                ArrayList<String> responseList = bundle.getStringArrayList("DETAILS_LIST");
                                if (responseList == null) {
                                    return null;
                                }
                                String[] prices = new String[responseList.size()];
                                for (int i = 0; i < responseList.size(); i++) {
                                    JSONObject object;
                                    try {
                                        object = new JSONObject(responseList.get(i));
                                        String price = object.getString("price");
                                        prices[i] = price;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Arrays.sort(prices, (o1, o2) -> Integer.parseInt(o1.replaceAll("[^0-9]", "")) - Integer.parseInt(o2.replaceAll("[^0-9]", "")));
                                return prices;
                            }
                    )
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void digestPurchase(String purchaseToken) {
        // 消耗购买，使能重复赞赏同一金额
        try {
            Observable.just(mService.consumePurchase(3, context.getPackageName(), purchaseToken))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void purchase(String sku) {
        try {
            if (mService == null) {
                Toast.makeText(context, R.string.lose_google_server, Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle buyIntentBundle = mService.getBuyIntent(3, context.getPackageName(),
                    sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
            switch (buyIntentBundle.getInt("RESPONSE_CODE")) {
                case 0:
                    //BILLING_RESPONSE_RESULT_OK
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    context.startIntentSenderForResult(pendingIntent.getIntentSender(),
                            BUY_REQUEST_CODE, new Intent(), 0, 0, 0);
                    break;

            }
        } catch (RemoteException | IntentSender.SendIntentException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void unbindPayService() {
        if (mService != null) {
            context.unbindService(mServiceConn);
        }
    }

    public interface PayCallback {
        void doAfterConn();
    }

}
