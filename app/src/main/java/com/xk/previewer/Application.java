package com.xk.previewer;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xk.previewer.utils.SpUtils;

/**
 * @author xuekai
 * @date 2025/01/11
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SpUtils.init(this);
        Fresco.initialize(this);
        app = this;
    }
    public static Application app;
}
