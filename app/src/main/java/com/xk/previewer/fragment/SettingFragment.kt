package com.xk.previewer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.xk.previewer.R
import com.xk.previewer.activity.MainActivity
import com.xk.previewer.utils.SpUtils
import com.xk.previewer.utils.Utils
import java.io.File

/**
 * @author xuekai
 * @date 2025/01/11
 */
class SettingFragment : Fragment(R.layout.fragment_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setDesc("")
        view.findViewById<EditText>(R.id.url)
            .setText(SpUtils.getInstance().url)

        view.findViewById<Button>(R.id.save).setOnClickListener {
            val toString = view.findViewById<EditText>(R.id.url).text.toString()
            Toast.makeText(context, "url保存成功：" + toString, Toast.LENGTH_SHORT).show()
            SpUtils.getInstance().set("url", toString)
        }
        view.findViewById<Button>(R.id.clear_3d).setOnClickListener {
            val file = File(context?.filesDir, "cache_3d/")
            var count = 0
            file.listFiles()?.forEach {
                if (it.delete()) {
                    count++
                }
            }
            Toast.makeText(context, "清除云图" + count + "张", Toast.LENGTH_SHORT).show()

        }
        view.findViewById<Button>(R.id.clear_img).setOnClickListener {
            val file = File(context?.filesDir, "cache_img/")
            var count = 0
            file.listFiles()?.forEach {
                if (it.delete()) {
                    count++
                }
            }
            Toast.makeText(context, "清除图片" + count + "张", Toast.LENGTH_SHORT).show()

        }
        view.findViewById<Button>(R.id.clear_saved_img).setOnClickListener {
            context?.externalCacheDir?.absoluteFile.let {
                var count = 0
                it?.listFiles()?.forEach {
                    if (it.name.endsWith(".png") && it.delete()) {
                        count++
                    }
                }
                Toast.makeText(context, "清除保存的图片" + count + "张", Toast.LENGTH_SHORT).show()
            }

        }
    }
}