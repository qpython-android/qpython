package org.qpython.qpy.main;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.quseit.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.main.activity.PurchaseActivity;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.widget.GridSpace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hmei
 * 1/30/18.
 */

public class PayActivity extends AppCompatActivity {
    public static final int BUY_REQUEST_CODE = 2333;
    private IInAppBillingService   mService;
    private ServiceConnection      mServiceConn;
    private ArrayList<String>      skuList;
    private MySubscriber<String[]> callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void initIAB(ArrayList<String> skuList, MySubscriber<String[]> callback) {
        this.skuList = skuList;
        this.callback = callback;
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                getPrices(skuList, callback);
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 从Google服务器获取不同国家价格表并显示
     */
    private void getPrices(ArrayList<String> skuList, MySubscriber<String[]> callback) {
        if (mService == null) {
            Toast.makeText(this, R.string.lose_google_server, Toast.LENGTH_SHORT).show();
            if (findViewById(R.id.pb) != null) findViewById(R.id.pb).setVisibility(View.GONE);
            return;
        }
//        ArrayList<String> skuList;
//        if (!isCrowdFunding) {
//            skuList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sku)));
//        } else {
//            skuList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.crowdfunding)));
//            int index = (int) (getIntent().getIntExtra(PERCENT, 0) / 100.00 * 4);
//            String sku = skuList.get(index);
//            skuList.clear();
//            skuList.add(sku);
//        }
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            Observable.just(mService.getSkuDetails(3, getPackageName(), "inapp", querySkus))
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
                    .subscribe(callback
//                            new MySubscriber<String[]>() {
//                        @Override
//                        public void onNext(String[] o) {
//                            super.onNext(o);
//                            if (o == null) {
//                                return;
//                            }
//                            invalidateData();
//                            GridSpace gridSpace = new GridSpace(2, (int) ImageUtil.dp2px(16), false);
//                            binding.list.setLayoutManager(new GridLayoutManager(PurchaseActivity.this, 2));
//                            binding.list.addItemDecoration(gridSpace);
//                            binding.list.setAdapter(new PurchaseActivity.ListAdapter(o));
//                        }
//                    }
                    );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void purchase(String sku) {
//        String[] skus;
//        if (isCrowdFunding) {
//            skus = getResources().getStringArray(R.array.crowdfunding);
//        } else {
//            skus = getResources().getStringArray(R.array.sku);
//        }
        try {
            if (mService == null) {
                Toast.makeText(this, R.string.lose_google_server, Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
            switch (buyIntentBundle.getInt("RESPONSE_CODE")) {
                case 0:
                    //BILLING_RESPONSE_RESULT_OK
                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                            BUY_REQUEST_CODE, new Intent(), 0, 0, 0);
                    break;

            }
        } catch (RemoteException | IntentSender.SendIntentException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_menu) {
            if (findViewById(R.id.pb) != null) findViewById(R.id.pb).setVisibility(View.VISIBLE);
            getPrices(skuList, callback);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

}
