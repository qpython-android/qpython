package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.hipipal.qpyplus.wxapi.PaymentStatus;
//import com.hipipal.qpyplus.wxapi.WXAPIManager;
//import com.hipipal.qpyplus.wxapi.WeixinPay;
import com.quseit.util.ImageUtil;
import com.quseit.util.VeDate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityPurchaseBinding;
import org.qpython.qpy.databinding.ItemPriceBinding;
import org.qpython.qpy.main.adapter.MyViewHolder;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.app.User;
import org.qpython.qpy.main.service.PayUtil;
import org.qpython.qpy.main.widget.GridSpace;
import org.qpython.qpy.utils.UpdateHelper;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Weixin purchase activity
 * Created by Hmei on 2017-07-19.
 */

public class PurchaseActivity extends PayActivity {
    private static String[] prices = new String[]{"6", "12", "18", "24", "30", "36"};

    private ActivityPurchaseBinding binding;
    private String                  articleId;

    public static void start(Context context, String articleId) {
        Intent starter = new Intent(context, PurchaseActivity.class);
        starter.putExtra(ARTICLE_ID, articleId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase);
        articleId = getIntent().getStringExtra(ARTICLE_ID);
        initView();
        getPrices();
    }

    private void initView() {
        setSupportActionBar(binding.lt.toolbar);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(R.string.reward);
    }

    private void getPrices() {
        invalidateData();
        GridSpace gridSpace = new GridSpace(2, (int) ImageUtil.dp2px(16), false);
        binding.list.setLayoutManager(new GridLayoutManager(PurchaseActivity.this, 2));
        binding.list.addItemDecoration(gridSpace);
        binding.list.setAdapter(new ListAdapter(prices));
    }

    private void invalidateData() {
        binding.tvThanks.setVisibility(View.VISIBLE);
        binding.list.setVisibility(View.VISIBLE);
        binding.noData.llRoot.setVisibility(View.GONE);
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
            ItemPriceBinding itemBinding = holder.getBinding();
            itemBinding.tvPrices.setText(dataList[position]);
//            itemBinding.tvPrices.setOnClickListener(v -> payUtil.purchase(dataList[position], articleId, new Observer<WeixinPay>() {
//                @Override
//                public void onCompleted() {
//                    binding.pb.setVisibility(View.GONE);
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("type", dataList[position]);
//                        jsonObject.put("time", VeDate.getStringDateHourAsInt());
//                        jsonObject.put("crowdfunding",0);//0 非众筹/ 1: 0-100 /2: 100-500/3 500-2000
//                        jsonObject.put("articleId", articleId);
//                        if (App.getUser() != null) {
//                            jsonObject.put("account", App.getUser().getEmail());
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    UpdateHelper.submitIAPLog(PurchaseActivity.this, mOrderNo, App.getGson().toJson
//                            (jsonObject));
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    e.printStackTrace();
//                    Toast.makeText(PurchaseActivity.this, "connection error!", Toast.LENGTH_SHORT).show();
//                    binding.pb.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onNext(WeixinPay weixinPay) {
//                    WXAPIManager.wxPayReq(weixinPay);
//                    mOrderNo = weixinPay.out_trade_no;
//                }
//            }));
        }

        @Override
        public int getItemCount() {
            return dataList.length;
        }
    }
}
