package org.qpython.qpy.texteditor.ui.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.ui.adapter.bean.PopupItemBean;

import java.util.List;

public class EditorPopUp {
    private PopupWindow popupWindow;

    public EditorPopUp(Context context, final List<PopupItemBean> itemBeanList) {
        FrameLayout root = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.popup_add_editor, null);
        ListView listView = root.findViewById(R.id.list_view);

        ArrayAdapter<PopupItemBean> adapter = new ArrayAdapter<>(context, R.layout.item_pop_up, itemBeanList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            itemBeanList.get(position).getClickListener().onClick(view);
            adapter.notifyDataSetChanged();
            popupWindow.dismiss();
        });

        popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
    }

    public void show(View view, int gravity, int x, int y) {
        popupWindow.showAtLocation(view, gravity, x, y);
    }

    public void show(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupWindow.showAsDropDown(view, 0, 0, Gravity.END);
        } else {
            popupWindow.showAtLocation(view, Gravity.TOP | Gravity.END, 0, view.getHeight());
        }
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}
