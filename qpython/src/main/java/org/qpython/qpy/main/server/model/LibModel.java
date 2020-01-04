package org.qpython.qpy.main.server.model;

import com.quseit.util.DateTimeHelper;

import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;

import java.util.Date;

/**
 * Lib model
 * Created by Hmei on 2017-05-27.
 */

public class LibModel extends BaseLibModel{

    /**
     * src : http://kivy.org
     * rdate : 2017-06-15
     * link : http://dl.qpy.io/2x/kivy/kivy-1.9.1.zip
     * description : kivy 1.9.1 prebuild for qpython
     * title : kivy
     * ver : 1
     * cat : dev
     * md5sum :
     * smodule : kivy
     * icon :
     */

    private String md5sum;
    private String icon;

    public LibModel(String title, Date lastModify) {
        super(title);
        installed = true;
        description = App.getContext().getString(R.string.update_time_hint, DateTimeHelper.formatData(lastModify,"yyyy-MM-dd"));
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
