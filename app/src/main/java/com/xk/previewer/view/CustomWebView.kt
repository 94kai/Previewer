package com.xk.previewer.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Base64
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.webkit.WebViewClientCompat
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

    lateinit var _3DPath: String
    fun set3DUrl(url: String) {
        clear3D()
        //todo 设置空之后，清楚画布
        _3DPath = getPath(url)
        if (File(_3DPath).exists()) {
            load3D(_3DPath)
        } else {
            NetUtils.downloadFile(url, getPath(url), object : Callback {
                override fun onFailed(msg: String) {
                    post {
                        Toast.makeText(context, "3d下载失败,url:$url", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onSuccess(data: String) {
                    if (_3DPath == data) {// 避免错位
                        load3D(_3DPath)
                    }
                }
            })
        }
    }

    private fun load3D(_3DPath: String) {
        val readAllBytes = FileInputStream(_3DPath).readBytes()
        var s: String = Base64.encodeToString(readAllBytes, Base64.DEFAULT)
        s = s.replace("\n", "")
        post {
            evaluateJavascript("hideUploadContainer()", null)

            if (_3DPath.endsWith("pcd")) {
                evaluateJavascript("showPcd('${s}')", null)
            } else if (_3DPath.endsWith("ply")) {
                evaluateJavascript("showPly('${s}')", null)
            } else if (_3DPath.endsWith("obj")) {
                evaluateJavascript("showObj('${s}')", null)
            }
        }
    }

    private fun clear3D() {
        // TODO
        evaluateJavascript("clearPointClouds()", null)
    }

    private fun getPath(src: String): String {
        var suffix = "pcd"
        if (src.endsWith("pcd")) {
            suffix = "pcd"
        } else if (src.endsWith("ply")) {
            suffix = "ply"
        } else if (src.endsWith("obj")) {
            suffix = "obj"
        }
        val file = File(context.filesDir, "cache_3d/${Utils.md5(src)}.${suffix}")
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdir()
        }
        return file.absolutePath
    }
}