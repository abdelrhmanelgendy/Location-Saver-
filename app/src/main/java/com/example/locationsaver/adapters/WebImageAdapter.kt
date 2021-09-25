package com.example.locationsaver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.R
import com.example.locationsaver.pojo.ImageAdapter
import com.google.android.gms.common.images.WebImage
import com.squareup.picasso.Picasso

class WebImageAdapter(val context: Context, var onImageClickListener: OnImageClickListener) :
    RecyclerView.Adapter<WebImageAdapter.ImageViewHolder>() {

    var imageList: ArrayList<ImageAdapter> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.custome_item_web_image, null)
        return ImageViewHolder(view, imageList, onImageClickListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList.get(position).imageUrl
        Picasso.get().load(imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageViewHolder(
        itemView: View,
        var imageList: ArrayList<ImageAdapter>,
        var onImageClickListener: OnImageClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.webImage_customItem)
            itemView.setOnClickListener({
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onImageClickListener.onImageClick(imageList.get(adapterPosition))
                }
            })
        }

    }
}

interface OnImageClickListener {
    fun onImageClick(adapter: ImageAdapter)

}