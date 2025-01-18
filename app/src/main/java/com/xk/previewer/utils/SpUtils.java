package com.xk.previewer.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

    private static SpUtils instance;
    private static Application context;

    public static void init(Application context) {
        SpUtils.context = context;
    }

    public static SpUtils getInstance() {
        if (instance == null) {
            instance = new SpUtils();
        }
        return instance;
    }

    private SharedPreferences sp;


    private SpUtils() {
        sp = context.getSharedPreferences("sp_config1", Context.MODE_PRIVATE);
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public String get(String key) {
        return sp.getString(key, "");
    }


}