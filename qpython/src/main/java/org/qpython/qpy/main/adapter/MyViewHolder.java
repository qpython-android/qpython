package org.qpython.qpy.main.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Custom View Holder for store data binding
 * Created by Hmei on 2017-05-25.
 */

public class MyViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private T binding;

    public MyViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public T getBinding() {
        return binding;
    }

    public void setBinding(T binding) {
        this.binding = binding;
    }
}
