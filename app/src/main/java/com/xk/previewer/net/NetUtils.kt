package com.xk.previewer.net

import android.app.ProgressDialog
import android.view.View
import com.xk.previewer.Application
import com.xk.previewer.utils.SpUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.mock.AndroidResources.asset
import okhttp3.mock.Behavior
import okhttp3.mock.MediaTypes.MEDIATYPE_JSON
import okhttp3.mock.MockInterceptor
import okhttp3.mock.eq
import okhttp3.mock.get
import okhttp3.mock.rule
import okhttp3.mock.startWith
import okhttp3.mock.url
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author xuekai
 * @date 2025/01/12
 */
object NetUtils {
    val interceptor = MockInterceptor(Behavior.RELAYED).apply {
        rule(get, url eq "https://test/api?type=_3D", times = 99999, delay = 1000) {
            respond(
                Mock._3D, MEDIATYPE_JSON
            )
        }
        rule(get, url eq "https://test/api?type=IMG_GROUP", times = 99999, delay = 1000) {
            respond(
                Mock.IMG, MEDIATYPE_JSON
            )
        }

        rule(url startWith "https://test/file/pcd1.pcd") {
            respond(asset(Application.app, "pcd1.pcd"))
        }
        rule(url startWith "https://test/file/pcd2.pcd") {
            respond(asset(Application.app, "pcd2.pcd"))
        }

        rule(url startWith "https://test/file/pcd3.pcd") {
            respond(asset(Application.app, "pcd3.pcd"))
        }
        rule(url startWith "https://test/file/pointCloud.pcd") {
            respond(asset(Application.app, "pointCloud.pcd"))
        }
        rule(url startWith "https://test/file/tree.obj") {
            respond(asset(Application.app, "tree.obj"))
        }
        rule(url startWith "https://test/file/off.pcd") {
            respond(asset(Application.app, "off.pcd"))
        }
        rule(url startWith "https://test/file/dolphins.ply") {
            respond(asset(Application.app, "dolphins.ply"))
        }
        rule(url startWith "https://test/file/dolphins_colored.ply") {
            respond(asset(Application.app, "dolphins_colored.ply"))
        }
    }
    val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()


    fun downloadFile(url: String, path: String, callback: com.xk.previewer.net.Callback) {
        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val tempPath = path + "_temp"
                    val fileOutputStream = FileOutputStream(tempPath)
                    val buffer = ByteArray(4096)
                    response.body?.byteStream()?.let {
                        while (true) {
                            val read = it.read(buffer)
                            if (read == -1) {
                                break
                            } else {
                                fileOutputStream.write(buffer, 0, read)
                            }
                        }
                        File(tempPath).renameTo(File(path))
                        callback.onSuccess(path)
                        return
                    }
                    callback.onFailed("下载失败")
                }
            }
        })
    }


    fun request(
        type: RequestType,
        listener: com.xk.previewer.net.Callback,
        loadingAnchor: View? = null
    ) {
        val url = SpUtils.getInstance().get("url").ifEmpty { "https://test/api?type=" }
        val request = Request.Builder().get().url(url + type).build()
        if (loadingAnchor != null) {
            showLoading(loadingAnchor)
        }
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (loadingAnchor != null) {
                    hideLoading(loadingAnchor)
                }
                listener.onFailed(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (loadingAnchor != null) {
                    hideLoading(loadingAnchor)
                }
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        listener.onSuccess(body.string())
                    } else {
                        listener.onFailed("无数据返回")
                    }

                } else {
                    listener.onFailed("下载失败")
                }
            }
        })

    }

    var progressDialog: ProgressDialog? = null
    private fun showLoading(view: View) {
        view.post {
            progressDialog = ProgressDialog(view.context)
            progressDialog?.setMessage("加载中")
            progressDialog?.show()
        }
    }

    private fun hideLoading(view: View) {
        view.post {
            progressDialog?.hide()
        }
    }
}

enum class RequestType {
    IMG_GROUP,
    _3D
}