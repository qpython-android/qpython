package org.qpython.qpy.main.server.gist;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 文 件 名: TokenManager
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:13
 * 修改时间：
 * 修改备注：
 */

public class TokenManager {
    private static SharedPreferences sharedPreferences;
    private static final String TOKEN_KEY = "token";
    private static final String GIST_KEY = "gist";

    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences(GIST_KEY,Context.MODE_PRIVATE);
    }

    public static String getToken(){
        return sharedPreferences.getString(TOKEN_KEY,"");
    }

    public static void saveToken(String token){
        sharedPreferences.edit().putString(TOKEN_KEY,token).apply();
    }
}
