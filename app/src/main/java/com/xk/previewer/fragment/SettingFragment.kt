package com.xk.previewer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.xk.previewer.R
import com.xk.previewer.activity.MainActivity
import com.xk.previewer.utils.SpUtils

/**
 * @author xuekai
 * @date 2025/01/11
 */
class SettingFragment : Fragment(R.layout.fragment_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setDesc("")
        view.findViewById<EditText>(R.id.url)
            .setText(SpUtils.getInstance().get("url").ifEmpty { "请输入url" })

        view.findViewById<EditText>(R.id.url).doOnTextChanged { text, _, _, _ ->
            SpUtils.getInstance().set("url", text.toString())
        }
        try {
            context?.let {
                WebView(it)
                val packageInfo =
                    it.packageManager.getPackageInfo("com.google.android.webview", 0)
                view.findViewById<TextView>(R.id.version)
                    .setText("WebView信息 \n版本名：${packageInfo.versionName} \n版本号：${packageInfo.versionCode}")
            }
        } catch (e: Throwable) {
            e.printStackTrace()

        }
    }
}