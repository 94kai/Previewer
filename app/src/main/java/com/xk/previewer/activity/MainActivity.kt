package com.xk.previewer.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xk.previewer.IRefresh
import com.xk.previewer.view.NavigationView
import com.xk.previewer.R
import com.xk.previewer.fragment.ImagePreviewFragment
import com.xk.previewer.fragment.PointCloudPreviewFragment
import com.xk.previewer.fragment.SettingFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigation()
    }

    fun initNavigation() {
        val navigationView = findViewById<NavigationView>(R.id.left_root)
        navigationView.init(getSupportFragmentManager(), R.id.right_root);
        navigationView.addTab("图片", R.mipmap.image, ImagePreviewFragment());
        navigationView.addTab("云图", R.mipmap.cloud, PointCloudPreviewFragment());
        navigationView.addTab("设置", R.mipmap.setting, SettingFragment());
        navigationView.addTab("刷新", R.mipmap.refresh, object : OnClickListener {
            override fun onClick(p0: View?) {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.right_root)
                if (currentFragment is IRefresh) {
                    currentFragment.refresh()
                }
            }

        });

    }

    fun setDesc(desc: String) {
        findViewById<TextView>(R.id.desc).setText(desc)
    }

}