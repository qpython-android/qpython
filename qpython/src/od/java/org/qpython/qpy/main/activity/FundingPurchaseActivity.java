package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.quseit.util.VeDate;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityFundingPurchaseBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.GooglePurchaseModel;
import org.qpython.qpy.utils.UpdateHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hmei
 * 1/30/18.
 */

public class FundingPurchaseActivity extends PayActivity {
    private static final String ARTICLE_ID    = "article_id";
    private static final String FUNDING_COUNT = "fundingCount";
    //    private final String[] prices = get
    private ActivityFundingPurchaseBinding binding;
    private String                         sku;

    public static void startSupport(Context context, String articleId, int fundingPercent) {
        Intent starter = new Intent(context, FundingPurchaseActivity.class);
        starter.putExtra(ARTICLE_ID, articleId);
        starter.putExtra(FUNDING_COUNT, fundingPercent);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_funding_purchase);
        binding.pb.setVisibility(View.VISIBLE);
        initView();
        initPrice();
        initListener();
    }

    private void initView() {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(R.string.reward);
    }

    private void initPrice() {
        String[] skus = getResources().getStringArray(R.array.crowdfunding);
        int fundingCount = getIntent().getIntExtra(FUNDING_COUNT, 0);
        if (fundingCount < 100) {
            sku = skus[0];
        } else if (fundingCount < 500) {
            sku = skus[1];
        } else if (fundingCount < 2000) {
            sku = skus[2];
        } else {
            sku = skus[3];
        }
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(sku);
        initIAB(skuList, new MySubscriber<String[]>() {
            @Override
            public void onNext(String[] o) {
                super.onNext(o);
                if (o == null) {
                    return;
                }
                invalidateData(true);
                binding.price.setText(o[0]);
            }
        });
    }

    private void initListener() {
        binding.price.setOnClickListener(v -> payUtil.purchase(sku));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
    }

    private void invalidateData(boolean showData) {
        binding.price.setVisibility(showData ? View.VISIBLE : View.GONE);
        binding.tvThanks.setVisibility(showData ? View.VISIBLE : View.GONE);
        binding.noData.llRoot.setVisibility(showData ? View.GONE : View.VISIBLE);
        binding.pb.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BUY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                GooglePurchaseModel model = App.getGson().fromJson(data.getStringExtra("INAPP_PURCHASE_DATA"), GooglePurchaseModel.class);
                if (model == null) {
                    return;
                }
                switch (model.getProductId()) {
                    default:
                        Toast.makeText(this, R.string.thanks_your_support, Toast.LENGTH_SHORT).show();
                        break;
                }
                // 统计赞赏数据
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", model.getProductId());
                    jsonObject.put("time", VeDate.getStringDateHourAsInt());
                    int percent = getIntent().getIntExtra(FUNDING_COUNT, 0);
                    String[] fundingCount = getResources().getStringArray(R.array.funding_count_divider);
                    jsonObject.put("crowdfunding",
                            percent > (Integer.parseInt(fundingCount[1]) / Integer.parseInt(fundingCount[2])) ? 3 :
                                    percent > (Integer.parseInt(fundingCount[0]) / Integer.parseInt(fundingCount[1])) ? 2 : 1);//0 非众筹/ 1: 0-100 /2: 100-500/3 500-2000
                    jsonObject.put("articleId", getIntent().getStringExtra(ARTICLE_ID));
                    if (App.getUser() != null) {
                        jsonObject.put("account", App.getUser().getEmail());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UpdateHelper.submitIAPLog(this, model.getOrderId(), App.getGson().toJson(jsonObject));
                payUtil.digestPurchase(model.getPurchaseToken());
                finish();
            }
        }
    }
}
