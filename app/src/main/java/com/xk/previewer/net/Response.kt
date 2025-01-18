package com.xk.previewer.net

/**
 * @author xuekai
 * @date 2025/01/11
 */
data class Response(val code: Int, val msg: String, val data: Array<Item>)


data class Item(val title: String, val desc: String, val images: Array<String>)
