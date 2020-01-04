package org.qpython.qpy.main.widget.scheduleview;

import android.content.Context;

import org.qpython.qpy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hmei
 * 1/9/18.
 */

public class DotObj {
    private String dotName;
    private String dotNum;

    public DotObj(String dotName, String dotNum) {
        this.dotName = dotName;
        this.dotNum = dotNum;
    }

    public static List<DotObj> getDefaultList(Context context) {
        List<DotObj> dotObjs = new ArrayList<>();
        String[] name = context.getResources().getStringArray(R.array.funding_stage);
        String[] count = context.getResources().getStringArray(R.array.funding_count_divider);
        dotObjs.add(new DotObj(name[0], count[0]));
        dotObjs.add(new DotObj(name[1], count[1]));
        dotObjs.add(new DotObj(name[2], count[2]));
        return dotObjs;
    }

    public String getDotName() {
        return dotName;
    }

    public void setDotName(String dotName) {
        this.dotName = dotName;
    }

    public String getDotNum() {
        return dotNum;
    }

    public void setDotNum(String dotNum) {
        this.dotNum = dotNum;
    }
}
