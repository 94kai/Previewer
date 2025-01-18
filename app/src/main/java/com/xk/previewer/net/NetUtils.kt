package com.xk.previewer.net

import android.content.Context
import com.xk.previewer.utils.SpUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author xuekai
 * @date 2025/01/12
 */
object NetUtils {
    val okHttpClient: OkHttpClient = OkHttpClient()
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

    fun downloadPCDFile(
        context: Context,
        url: String,
        path: String,
        callback: com.xk.previewer.net.Callback
    ) {
        val readBytes = context.assets.open(url).readBytes()
        FileOutputStream(path).write(readBytes)
        callback.onSuccess(path)
    }

    fun request(type: RequestType, listener: com.xk.previewer.net.Callback) {

        Mock.refreshData(type, listener)
        return
//        val url = SpUtils.getInstance().get("url").ifEmpty { "https://test" }
//        val request = Request.Builder().get().url(url + "?type=" + type).build()
//        okHttpClient.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                listener.onFailed(e.toString())
//            }
//
//            override fun onResponse(call: Call, response: Response) {
////                if (response.isSuccessful) {
////                    val buffer = ByteArray(4096)
////                    response.body?.byteStream()?.let {
////                        while (true) {
////                            val read = it.read(buffer)
////                            if (read == -1) {
////                                break
////                            } else {
////                                fileOutputStream.write(buffer, 0, read)
////                            }
////                        }
////                        File(tempPath).renameTo(File(path))
////                        callback.onSuccess(path)
////                        return
////                    }
////                    callback.onFailed("下载失败")
//            }
////
//        })
//
    }
}

enum class RequestType {
    IMG_GROUP,
    PCD
}