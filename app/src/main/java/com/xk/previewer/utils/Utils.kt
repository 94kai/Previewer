package com.xk.previewer.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.xk.previewer.R
import com.xk.previewer.R.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest


/**
 * @author xuekai
 * @date 2025/01/12
 */
object Utils {
    fun md5(src: String): String {
        val md5 = MessageDigest.getInstance("MD5")
        return BigInteger(1, md5.digest(src.toByteArray())).toString(16).padStart(32, '0')
    }

    fun copyFile(source: String, dest: String): Boolean {
        try {
            FileInputStream(source).use { fis ->
                FileOutputStream(dest).use { fos ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (fis.read(buffer).also { bytesRead = it } != -1) {
                        fos.write(buffer, 0, bytesRead)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun showToast(context: Context, msg: String) {
        val inflate = LayoutInflater.from(context)?.inflate(layout.toast, null)

        val findViewById = inflate?.findViewById<TextView>(R.id.toast_text)
        findViewById?.setText(msg)
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.view =inflate
        toast.show()
    }

}