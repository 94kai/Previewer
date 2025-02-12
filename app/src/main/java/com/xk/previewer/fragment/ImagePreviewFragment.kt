package com.xk.previewer.fragment;

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.xk.previewer.IRefresh
import com.xk.previewer.view.CropImageView
import com.xk.previewer.net.Item
import com.xk.previewer.adapter.MainImgListAdapter
import com.xk.previewer.R
import com.xk.previewer.R.id.group_list
import com.xk.previewer.R.id.main_img_list
import com.xk.previewer.activity.FullPreviewActivity
import com.xk.previewer.activity.MainActivity
import com.xk.previewer.net.Response
import com.xk.previewer.net.Callback
import com.xk.previewer.net.NetUtils
import com.xk.previewer.net.RequestType
import com.xk.previewer.utils.SpUtils

/**
 * @author xuekai
 * @date 2025/01/11
 */
class ImagePreviewFragment : Fragment(R.layout.fragment_img_preview), IRefresh {
    private val CACHE_DATA: String = "CACHE_IMG_GROUP"
    val gson = Gson()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setDesc("")
        val data = SpUtils.getInstance().get(CACHE_DATA)
        if (TextUtils.isEmpty(data)) {
            refreshFromNet()
        } else {
            refreshUI(data)
        }
    }

    private fun refreshFromNet() {
        NetUtils.request(RequestType.IMG_GROUP, object : Callback {
            override fun onFailed(msg: String) {
                view?.post {
                    Toast.makeText(
                        this@ImagePreviewFragment.context,
                        "请求失败",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onSuccess(data: String) {
                SpUtils.getInstance().set(CACHE_DATA, data)
                this@ImagePreviewFragment.view?.post {
                    refreshUI(data)
                }
            }

        }, view)
    }

    private fun refreshUI(refreshData: String) {
        val data = gson.fromJson(refreshData, Response::class.java)
        val titleList = data.data.map {
            it.title
        }

        val groupList = view?.findViewById<Spinner>(group_list)

        groupList?.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titleList)
        groupList?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                refreshImageGroup(p2, data.data)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        view?.findViewById<Button>(R.id.next)?.setOnClickListener {
            groupList?.selectedItemId?.let {
                if (it.toInt() == groupList.count - 1) {
                    groupList.setSelection(0)
                } else {
                    groupList.setSelection(it.toInt() + 1)
                }
            }
        }
        view?.findViewById<Button>(R.id.prev)?.setOnClickListener {
            groupList?.selectedItemId?.let {
                if (it.toInt() == 0) {
                    groupList.setSelection(groupList.count - 1)
                } else {
                    groupList.setSelection(it.toInt() - 1)
                }
            }
        }

        setupTools()

    }

    private fun setupTools() {
        val cropImageView = view?.findViewById<CropImageView>(R.id.crop_img)
        view?.findViewById<View>(R.id.left_rotate)?.setOnClickListener {
            cropImageView?.leftRotate()
        }
        view?.findViewById<View>(R.id.right_rotate)?.setOnClickListener {
            cropImageView?.rightRotate()
        }
        view?.findViewById<View>(R.id.top_bottom_flip)?.setOnClickListener {
            cropImageView?.topBottomFlip()
        }
        view?.findViewById<View>(R.id.left_right_flip)?.setOnClickListener {
            cropImageView?.leftRightFlip()
        }
        view?.findViewById<View>(R.id.start_crop)?.setOnClickListener {
            setCropBtn(true)
        }
        view?.findViewById<View>(R.id.apply_crop)?.setOnClickListener {
            cropImageView?.applyCrop()
            setCropBtn(false)
        }
        view?.findViewById<View>(R.id.cancel)?.setOnClickListener {
            setCropBtn(false)
        }
        view?.findViewById<View>(R.id.restore)?.setOnClickListener {
            setCropBtn(false)
            cropImageView?.restore()
        }
        view?.findViewById<View>(R.id.save)?.setOnClickListener {
            cropImageView?.save()
        }
    }

    fun refreshImageGroup(position: Int, data: Array<Item>) {
        val mainImageList = view?.findViewById<RecyclerView>(main_img_list)
        val mainImgListAdapter = MainImgListAdapter(data[position].images)
        mainImageList?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mainImageList?.adapter = mainImgListAdapter

        view?.findViewById<CropImageView>(R.id.crop_img)?.setImageUrl(data[position].images.last())
        view?.findViewById<CropImageView>(R.id.crop_img)?.setOnClickListener {
            val intent = Intent(
                view?.context,
                FullPreviewActivity::class.java
            )
            intent.putExtra("url", data[position].images.last())
            view?.context?.startActivity(intent)
        }
        setCropBtn(false)
        (activity as? MainActivity)?.setDesc(data[position].desc)
    }

    private fun setCropBtn(isCrop: Boolean) {
        view?.findViewById<CropImageView>(R.id.crop_img)?.switchCrop(isCrop)
        if (isCrop) {
            view?.findViewById<View>(R.id.apply_crop)?.visibility = VISIBLE
            view?.findViewById<View>(R.id.cancel)?.visibility = VISIBLE
            view?.findViewById<View>(R.id.start_crop)?.visibility = GONE
        } else {
            view?.findViewById<View>(R.id.apply_crop)?.visibility = GONE
            view?.findViewById<View>(R.id.cancel)?.visibility = GONE
            view?.findViewById<View>(R.id.start_crop)?.visibility = VISIBLE
        }

    }

    override fun refresh() {
        refreshFromNet()
    }
}
