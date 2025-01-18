package com.xk.previewer.fragment;

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
import com.xk.previewer.view.CropImageView
import com.xk.previewer.net.Item
import com.xk.previewer.R
import com.xk.previewer.activity.MainActivity
import com.xk.previewer.net.Response
import com.xk.previewer.net.Callback
import com.xk.previewer.net.Mock
import com.xk.previewer.net.NetUtils
import com.xk.previewer.net.RequestType
import com.xk.previewer.utils.SpUtils
import com.xk.previewer.view.CustomWebView

/**
 * @author xuekai
 * @date 2025/01/11
 */
class PointCloudPreviewFragment : Fragment(R.layout.fragment_pcd_preview), IRefresh {
    private val CACHE_DATA: String = "CACHE_PCD"
    val gson = Gson()


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

    private fun refreshFromNet() {
        NetUtils.request(RequestType.PCD, object : Callback {
            override fun onFailed(msg: String) {
                Toast.makeText(
                    this@PointCloudPreviewFragment.context,
                    "请求失败",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSuccess(data: String) {
                SpUtils.getInstance().set(CACHE_DATA, data)
                this@PointCloudPreviewFragment.view?.post {
                    refreshUI(data)
                }
            }
        })
    }

    private fun refreshUI(refreshData: String) {
        val data = gson.fromJson(refreshData, Response::class.java)
        val titleList = data.data.map {
            it.title
        }

        val groupList = view?.findViewById<Spinner>(R.id.pcd_list)

        groupList?.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titleList)
        groupList?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                refreshPCDData(p2, data.data)
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
        val cropImageView = view?.findViewById<CropImageView>(R.id.crop_img)
//        view?.findViewById<View>(R.id.left_rotate)?.setOnClickListener {
//            cropImageView?.leftRotate()
//        }
//        view?.findViewById<View>(R.id.right_rotate)?.setOnClickListener {
//            cropImageView?.rightRotate()
//        }
//        view?.findViewById<View>(R.id.top_bottom_flip)?.setOnClickListener {
//            cropImageView?.topBottomFlip()
//        }
//        view?.findViewById<View>(R.id.left_right_flip)?.setOnClickListener {
//            cropImageView?.leftRightFlip()
//        }
//        view?.findViewById<View>(R.id.start_crop)?.setOnClickListener {
//            cropImageView?.switchCrop(true)
//        }
//        view?.findViewById<View>(R.id.apply_crop)?.setOnClickListener {
//            cropImageView?.applyCrop()
//        }
//        view?.findViewById<View>(R.id.cancel)?.setOnClickListener {
//        }
//        view?.findViewById<View>(R.id.restore)?.setOnClickListener {
//            cropImageView?.restore()
//        }
//        view?.findViewById<View>(R.id.save)?.setOnClickListener {
//            cropImageView?.save()
//        }
    }

    fun refreshPCDData(position: Int, data: Array<Item>) {
        if (TextUtils.isEmpty(data[position].desc)) {
            (activity as? MainActivity)?.setDesc("暂无描述")
        } else {
            (activity as? MainActivity)?.setDesc(data[position].desc)
        }
        view?.findViewById<CustomWebView>(R.id.webview)?.setPcdUrl(data[position].images[0])
    }

    override fun refresh() {
        refreshFromNet()
    }
}
