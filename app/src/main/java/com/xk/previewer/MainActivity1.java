package com.xk.previewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class MainActivity1 extends Activity {

    private byte[] readFormAssets() throws IOException {
        InputStream open = getAssets().open("off.pcd");

        byte[] buffer = new byte[open.available()];
        open.read(buffer);
        open.close();
        return buffer;
    }

    @SuppressLint("RequiresFeature")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        testImageView();
        WebView webView = findViewById(R.id.webview);
        HashSet<String> objects = new HashSet<>();
        objects.add("*");

        WebViewCompat.addWebMessageListener(webView, "msgArrayBuffer", objects, (view, message, sourceOrigin, isMainFrame, replyProxy) -> {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_ARRAY_BUFFER)) {
                try {
                    byte[] array = readFormAssets();
                    replyProxy.postMessage(array);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                System.out.println(consoleMessage.message());
                return true;
            }
        });
        webView.loadUrl("file:///android_asset/index.html");

    }

    private void testImageView() {
//        CropImageView viewById = findViewById(R.id.crop_image_view);
//        viewById.setImageToCrop(BitmapFactory.decodeResource(getResources(), R.drawable.img));
    }

}
