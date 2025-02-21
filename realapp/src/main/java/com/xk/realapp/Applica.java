package com.xk.realapp;

import android.app.Application;
import android.content.Context;

import com.xk.previewer.utils.SpUtils;

/**
 * @author xuekai
 * @date 2025/02/17
 */
public class Applica extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SpUtils.init(this);
    }
}
