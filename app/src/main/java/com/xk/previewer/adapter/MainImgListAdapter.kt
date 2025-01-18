package com.xk.previewer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xk.previewer.R
import com.xk.previewer.activity.FullPreviewActivity
import com.xk.previewer.view.CustomImageView

/**
 * @author xuekai
 * @date 2025/01/12
 */
class MainImgListAdapter(val data: Array<String>) : Adapter<MainImgListAdapter.Holder>() {


    class Holder(rootView: View) : ViewHolder(rootView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_main_img, parent, false)
        return Holder(inflate)
    }

    override fun getItemCount(): Int {
        return data.size - 1
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.findViewById<CustomImageView>(R.id.img).setImageUrl(data[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(
                holder.itemView.context,
                FullPreviewActivity::class.java
            )
            intent.putExtra("url", data[position])
            holder.itemView.context.startActivity(intent)
        }
    }
}