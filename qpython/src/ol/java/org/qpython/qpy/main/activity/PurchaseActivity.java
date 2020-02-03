package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quseit.util.ImageUtil;
import com.quseit.util.VeDate;

import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityPurchaseBinding;
import org.qpython.qpy.databinding.ItemPriceBinding;
import org.qpython.qpy.main.adapter.MyViewHolder;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.GooglePurchaseModel;
import org.qpython.qpy.main.widget.GridSpace;
import org.qpython.qpy.utils.UpdateHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Google purchase activity
 * Created by Hmei on 2017-07-19.
 */

public class PurchaseActivity extends PayActivity {

    private static final String ARTICLE_ID = "article_id";

    private ActivityPurchaseBinding binding;
    private ArrayList<String>       skus;

    public static void start(Context context, String articleId) {
        Intent starter = new Intent(context, PurchaseActivity.class);
        starter.putExtra(ARTICLE_ID, articleId);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase);
        skus = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sku)));

        initIAB(skus, new MySubscriber<String[]>() {
            @Override
            public void onNext(String[] o) {
                super.onNext(o);
                if (o == null) {
                    return;
                }
                invalidateData();
                GridSpace gridSpace = new GridSpace(2, (int) ImageUtil.dp2px(16), false);
                binding.list.setLayoutManager(new GridLayoutManager(PurchaseActivity.this, 2));
                binding.list.addItemDecoration(gridSpace);
                binding.list.setAdapter(new PurchaseActivity.ListAdapter(o));
            }
        });

        initView();
    }

    private void initView() {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(R.string.reward);
    }

    private void invalidateData() {
        binding.tvThanks.setVisibility(View.VISIBLE);
        binding.list.setVisibility(View.VISIBLE);
        binding.noData.llRoot.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);
        return true;
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
                    jsonObject.put("crowdfunding", 0);//0 非众筹/ 1: 0-100 /2: 100-500/3 500-2000
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

    private class ListAdapter extends RecyclerView.Adapter<MyViewHolder<ItemPriceBinding>> {
        String[] dataList;

        ListAdapter(String[] dataList) {
            this.dataList = dataList;
        }

        @Override
        public MyViewHolder<ItemPriceBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemPriceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(PurchaseActivity.this), R.layout.item_price, parent, false);
            MyViewHolder<ItemPriceBinding> holder = new MyViewHolder<>(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder<ItemPriceBinding> holder, int position) {
            ItemPriceBinding binding = holder.getBinding();
            binding.tvPrices.setText(dataList[position]);
            binding.tvPrices.setOnClickListener(v -> payUtil.purchase(skus.get(position)));
        }

        @Override
        public int getItemCount() {
            return dataList.length;
        }
    }
}
