package com.xk.previewer.fragment;

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.xk.previewer.IRefresh
import com.xk.previewer.net.Item
import com.xk.previewer.R
import com.xk.previewer.activity.MainActivity
import com.xk.previewer.net.Response
import com.xk.previewer.net.Callback
import com.xk.previewer.net.NetUtils
import com.xk.previewer.net.RequestType
import com.xk.previewer.utils.SpUtils
import com.xk.previewer.view.CustomWebView

/**
 * @author xuekai
 * @date 2025/01/11
 */
class ThreeDimensionPreviewFragment : Fragment(R.layout.fragment_3d_preview), IRefresh {
    private val CACHE_DATA: String = "CACHE_3D"
    val gson = Gson()

    // 接入方中使用AgentWeb对webview进行了pause，所以我们这里需要resume一下
    var webView: WebView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 清空描述
        (activity as? MainActivity)?.setDesc("")
        view.findViewById<WebView>(R.id.webview).webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val data = SpUtils.getInstance().get(CACHE_DATA)
                if (TextUtils.isEmpty(data)) {
                    refreshFromNet()
                } else {
                    refreshUI(data)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        webView = WebView(context)
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
        webView?.pauseTimers()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
        webView?.resumeTimers()
    }

    private fun refreshFromNet() {
        NetUtils.request(RequestType._3D, object : Callback {
            override fun onFailed(msg: String) {
                view?.post {
                    Toast.makeText(
                        this@ThreeDimensionPreviewFragment.context,
                        "请求失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onSuccess(data: String) {
                SpUtils.getInstance().set(CACHE_DATA, data)
                this@ThreeDimensionPreviewFragment.view?.post {
                    refreshUI(data)
                }
            }
        }, view)
    }

    private fun refreshUI(refreshData: String) {
        val data = gson.fromJson(refreshData, Response::class.java)
        val titleList = data.data.map {
            it.title
        }

        val groupList = view?.findViewById<Spinner>(R.id._3d_list)

        groupList?.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titleList)
        groupList?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                refresh3DData(p2, data.data)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        view?.findViewById<Button>(R.id.next)?.setOnClickListener {
            groupList?.selectedItemId?.let {
                if (it.toInt() == groupList.count - 1) {
                    groupList.setSelection(0)
                } else {
                    groupList.setSelection(it.toInt() + 1)
                }
            }
        }
        view?.findViewById<Button>(R.id.prev)?.setOnClickListener {
            groupList?.selectedItemId?.let {
                if (it.toInt() == 0) {
                    groupList.setSelection(groupList.count - 1)
                } else {
                    groupList.setSelection(it.toInt() - 1)
                }
            }
        }
        setupTools()
    }

    private fun setupTools() {
        view?.findViewById<TextView>(R.id.left)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('moveLeft')", null)
        }
        view?.findViewById<TextView>(R.id.up)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('moveUp')", null)
        }
        view?.findViewById<TextView>(R.id.right)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('moveRight')", null)
        }
        view?.findViewById<TextView>(R.id.down)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('moveDown')", null)
        }
        view?.findViewById<TextView>(R.id.zoomDown)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('zoomDown')", null)
        }
        view?.findViewById<TextView>(R.id.zoomUp)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('zoomUp')", null)
        }
        view?.findViewById<TextView>(R.id.x_add)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('rotateX',10)", null)
        }
        view?.findViewById<TextView>(R.id.x_sub)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('rotateX',-10)", null)
        }
        view?.findViewById<TextView>(R.id.y_add)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('rotateY',10)", null)
        }
        view?.findViewById<TextView>(R.id.y_sub)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('rotateY',-10)", null)
        }
        view?.findViewById<TextView>(R.id.z_add)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('rotateZ',10)", null)
        }
        view?.findViewById<TextView>(R.id.z_sub)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('rotateZ',-10)", null)
        }
        view?.findViewById<TextView>(R.id.reset)?.setOnClickListener {
            view?.findViewById<CustomWebView>(R.id.webview)
                ?.evaluateJavascript("moveControl('reset')", null)
        }
    }

    fun refresh3DData(position: Int, data: Array<Item>) {
        if (TextUtils.isEmpty(data[position].desc)) {
            (activity as? MainActivity)?.setDesc("暂无描述")
        } else {
            (activity as? MainActivity)?.setDesc(data[position].desc)
        }
        view?.findViewById<CustomWebView>(R.id.webview)?.set3DUrl(data[position].images[0])
    }

    override fun refresh() {
        refreshFromNet()
    }
}
