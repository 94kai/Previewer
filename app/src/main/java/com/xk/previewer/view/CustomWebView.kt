package com.xk.previewer.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.xk.previewer.net.Callback
import com.xk.previewer.net.NetUtils
import com.xk.previewer.utils.Utils
import java.io.File
import java.io.FileInputStream


/**
 * @author xuekai
 */
@SuppressLint("RequiresFeature")
class CustomWebView(context: Context, attrs: AttributeSet?) : WebView(context, attrs) {

    init {
        setOf("*")
        WebViewCompat.addWebMessageListener(
            this,
            "msgArrayBuffer",
            setOf("*")
        ) { _, message, _, _, replyProxy ->
            if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_ARRAY_BUFFER)) {
                if (message.data == pcdPath) {
                    val readAllBytes = FileInputStream(pcdPath).readBytes()
                    replyProxy.postMessage(readAllBytes)
                }
            } else {
                try {
                    val packageInfo =
                        context.packageManager.getPackageInfo("com.google.android.webview", 0)
                    Toast.makeText(
                        this@CustomWebView.context,
                        "当前webview不支持arrayBuffer, versionName:${packageInfo?.versionName} versionCode:${packageInfo?.versionCode}",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        this.settings.javaScriptEnabled = true
        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                println(consoleMessage)
                return super.onConsoleMessage(consoleMessage)
            }

        }
        webViewClient = WebViewClientCompat()
        loadUrl("file:///android_asset/index.html");
    }

    lateinit var pcdPath: String
    fun setPcdUrl(url: String) {
        clearPointClouds()
        //todo 设置空之后，清楚画布
        pcdPath = getPath(url)
        if (File(pcdPath).exists()) {
            loadPcd(pcdPath)
        } else {
            NetUtils.downloadPCDFile(context, url, getPath(url), object : Callback {
                override fun onFailed(msg: String) {
                    post {
                        Toast.makeText(context, "pcd下载失败,url:$url", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onSuccess(data: String) {
                    if (pcdPath == data) {// 避免错位
                        post {
                            loadPcd(pcdPath)
                        }
                    }
                }
            })
        }
    }

    private fun loadPcd(pcdPath: String) {
        evaluateJavascript("loadPcd('${pcdPath}')", null)
    }

    private fun clearPointClouds() {
        evaluateJavascript("clearPointClouds()", null)
    }

    private fun getPath(src: String): String {
        val file = File(context.filesDir, "cache_pcd1/${Utils.md5(src)}")
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdir()
        }
        return file.absolutePath
    }
}