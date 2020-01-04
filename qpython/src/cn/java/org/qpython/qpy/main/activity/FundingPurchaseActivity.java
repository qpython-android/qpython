package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

//import com.hipipal.qpyplus.wxapi.PaymentStatus;
//import com.hipipal.qpyplus.wxapi.WXAPIManager;
//import com.hipipal.qpyplus.wxapi.WeixinPay;
import com.quseit.util.VeDate;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityFundingPurchaseBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.utils.UpdateHelper;

import rx.Observer;

/**
 * Created by Hmei
 * 1/30/18.
 */

public class FundingPurchaseActivity extends PayActivity {
    private static final String FUNDING_COUNT = "fundingCount";
    private ActivityFundingPurchaseBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_funding_purchase);
        initView();
        String[] count = getResources().getStringArray(R.array.funding_count_divider);
        String[] prices = getResources().getStringArray(R.array.wechat_funding_price);
        String price = null;
        int fundingCount = getIntent().getIntExtra(FUNDING_COUNT, 0);
        for (int i = 0; i < count.length; i++) {
            if ((Integer.parseInt(count[i]) - fundingCount) > 0) {
                price = prices[i];
                break;
            }
        }
        if (TextUtils.isEmpty(price)) price = prices[prices.length - 1];
        binding.price.setText(getString(R.string.rmb, price));
        String finalPrice = price;
//        binding.price.setOnClickListener(v -> payUtil.purchase(finalPrice, getIntent().getStringExtra(ARTICLE_ID), new Observer<WeixinPay>() {
//            @Override
//            public void onCompleted() {
//                binding.pb.setVisibility(View.GONE);
//                // 统计赞赏数据
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("type", finalPrice);
//                    jsonObject.put("time", VeDate.getStringDateHourAsInt());
//                    int percent = getIntent().getIntExtra(FUNDING_COUNT, 0);
//                    String[] fundingCount = getResources().getStringArray(R.array.funding_count_divider);
//                    jsonObject.put("crowdfunding",
//                            percent > (Integer.parseInt(fundingCount[1]) / Integer.parseInt(fundingCount[2])) ? 3 :
//                                    percent > (Integer.parseInt(fundingCount[0]) / Integer.parseInt(fundingCount[1])) ? 2 : 1);//0 非众筹/ 1: 0-100 /2: 100-500/3 500-2000
//                    jsonObject.put("articleId", getIntent().getStringExtra(ARTICLE_ID));
//                    if (App.getUser() != null) {
//                        jsonObject.put("account", App.getUser().getEmail());
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                UpdateHelper.submitIAPLog(FundingPurchaseActivity.this, mOrderNo, App.getGson().toJson(jsonObject));
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(FundingPurchaseActivity.this, R.string.conn_error, Toast.LENGTH_SHORT).show();
//                binding.pb.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onNext(WeixinPay weixinPay) {
//                WXAPIManager.wxPayReq(weixinPay);
//                mOrderNo = weixinPay.out_trade_no;
//            }
//        }));
    }

    public static void startSupport(Context context, String articleId, int fundingPercent) {
        Intent starter = new Intent(context, FundingPurchaseActivity.class);
        starter.putExtra(ARTICLE_ID, articleId);
        starter.putExtra(FUNDING_COUNT, fundingPercent);
        context.startActivity(starter);
    }

    private void initView() {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(R.string.reward);
        binding.tvThanks.setVisibility(View.VISIBLE);
        binding.price.setVisibility(View.VISIBLE);
    }
}
