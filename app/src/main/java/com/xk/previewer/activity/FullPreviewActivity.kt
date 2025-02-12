package com.xk.previewer.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xk.previewer.R
import com.xk.previewer.R.id
import com.xk.previewer.view.CustomImageView

/**
 * @author xuekai
 * @date 2025/01/12
 */
class FullPreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full)
        val imgView = findViewById<CustomImageView>(id.img)
        intent.getStringExtra("url")?.let { imgView.setImageUrl(it) }
//        imgView.setOnClickListener { finish() }
    }

}