package jackpal.androidterm.emulatorview;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.LinkedList;

/**
 * 文 件 名: HIstoryCmdUtils
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/1/31 17:15
 * 修改时间：
 * 修改备注：
 */

public class HistoryCmdUtils {
    private static HistoryCmdUtils instance = null;
    private SharedPreferences mSharedPreferences;
    private LinkedList<String> cmdList;
    private static final int COUNT_MAX = 10;
    private static final String CACHE_NAME = "history";

    private HistoryCmdUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        cmdList = stringToList(mSharedPreferences.getString(CACHE_NAME,""));
    }

    public static HistoryCmdUtils getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryCmdUtils(context);
        }
        return instance;
    }

    public void addCmd(String cmd) {
        Log.d("HistoryCmdUtils", "addCmd:"+cmd);
        int index = cmdList.indexOf(cmd);
        if (TextUtils.isEmpty(cmd) || index==0){
            return;
        }
        if (index>0){
            cmdList.remove(index);
        }else if (cmdList.size()==COUNT_MAX){
            cmdList.pollLast();
        }
        cmdList.addFirst(cmd);

        mSharedPreferences.edit().putString(CACHE_NAME,listToString(cmdList)).apply();
    }

    private LinkedList<String> stringToList(String text){
        LinkedList<String> list = new LinkedList<>();
        if (!TextUtils.isEmpty(text)){
            String[] tempList = text.split("\n");
            for (String str:tempList){
                list.addLast(str);
            }
        }
        return list;
    }

    private String listToString(LinkedList<String> list){
        StringBuffer stringBuffer = new StringBuffer();
        for (String str:list){
            stringBuffer.append(str);
            if (!str.equals(list.peekLast())){
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

    public LinkedList<String> getCmdList(){
        return cmdList;
    }

}
