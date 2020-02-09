/*
 * Copyright 2020 indra953@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indra.mindvalley_test

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.indra.slide.Slide
import com.indra.slide.common.Priority
import com.indra.slide.error.ANError
import com.indra.slide.interfaces.BitmapRequestListener
import com.shiburagi.imageloader.entities.Image
import kotlinx.android.synthetic.main.item_board.view.*

/**
 * Created by indra953@gmail.com on 2020-02-09.
 */
class MainAdapter(listener: Listener): RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var imageUrl: MutableList<String> = mutableListOf()
    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }
    override fun getItemCount(): Int = imageUrl.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageUrl[position], listener)
    }

    fun updateData(x: List<Image>) {
        val datas = x.map { it.urls?.small!! }
        imageUrl.addAll(datas.toMutableList())
        notifyDataSetChanged()
    }

    class ViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        constructor(parent: ViewGroup) :
                this(LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false))
        fun bind(imageUrl: String, listener: Listener?) {
            with(itemView) {
                Slide.get(imageUrl)
                    .setPriority(Priority.MEDIUM)
                    ?.setBitmapMaxHeight(0)
                    ?.setBitmapMaxWidth(0)
                    ?.setBitmapConfig(Bitmap.Config.ARGB_8888)
                    ?.setImageScaleType(ImageView.ScaleType.FIT_XY)
                    ?.build()?.getAsBitmap(object : BitmapRequestListener {
                        override fun onResponse(response: Bitmap?) {
                            ivImageView.setImageBitmap(response)
                        }

                        override fun onError(anError: ANError?) {}
                    })

                /*btnDownload.setOnClickListener {
                    listener?.onDownloadClicked(imageUrl)
                }*/
            }
        }
    }

    interface Listener{
        fun onDownloadClicked(url: String)
    }
}