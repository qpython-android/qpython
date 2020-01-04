package org.qpython.qpy.texteditor.ui.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


public class MyViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private B binding;

    public MyViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public B getBinding() {
        return binding;
    }

    public void setBinding(B binding) {
        this.binding = binding;
    }
}
