package com.xk.previewer.net

/**
 * @author xuekai
 * @date 2025/01/11
 */
interface Callback {
    fun onFailed(msg: String)
    fun onSuccess(data: String)
}