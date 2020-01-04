package org.qpython.qpy.texteditor;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.qpython.qpy.R;

// http://javatechig.com/android/how-to-get-list-of-installed-apps-in-android 

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;
 
    /**
     * @param context
     * @param textViewResourceId
     * @param appsList
     */
    public ApplicationAdapter(Context context, int textViewResourceId,
            List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
    }
 
    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }
 
    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }
 
        ApplicationInfo data = appsList.get(position);
        if (null != data) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
 
            appName.setText(data.loadLabel(packageManager));
            packageName.setText(data.packageName);
            iconview.setImageDrawable(data.loadIcon(packageManager));
        }
        return view;
    }
};
