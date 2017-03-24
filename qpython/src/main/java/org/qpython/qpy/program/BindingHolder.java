package org.qpython.qpy.program;


import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public final T binding;

    public BindingHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
