package com.xk.previewer.utils

import com.facebook.common.internal.ByteStreams.toByteArray
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

}