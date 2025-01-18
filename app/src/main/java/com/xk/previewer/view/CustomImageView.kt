package com.xk.previewer.view

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.xk.previewer.net.Callback
import com.xk.previewer.net.NetUtils
import com.xk.previewer.utils.Utils
import java.io.File

/**
 * @author xuekai
 */
open class CustomImageView(context: Context, attrs: AttributeSet?) :
    AppCompatImageView(context, attrs) {

    lateinit var imgPath: String

    fun setImageUrl(url: String) {
        setImageBitmap(null)
        imgPath = getPath(url)
        if (File(imgPath).exists()) {
            val decodeFile = BitmapFactory.decodeFile(imgPath)
            setImageBitmap(decodeFile)
        } else {
            NetUtils.downloadFile(url, getPath(url), object : Callback {
                override fun onFailed(msg: String) {
                    post {
                        Toast.makeText(context, "图片下载失败,url:$url", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onSuccess(data: String) {
                    val decodeFile = BitmapFactory.decodeFile(data)
                    if (imgPath == data) {// 避免错位
                        post {
                            setImageBitmap(decodeFile)
                        }
                    }
                }
            })
        }
    }

    private fun getPath(src: String): String {
        val file = File(context.filesDir, "cache_img/${Utils.md5(src)}")
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdir()
        }
        return file.absolutePath
    }

}