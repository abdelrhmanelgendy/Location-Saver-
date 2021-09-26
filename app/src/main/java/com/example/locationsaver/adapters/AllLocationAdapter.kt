package com.example.locationsaver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.R
import com.example.locationsaver.pojo.SavedLocation

class AllLocationAdapter(var context: Context,var onLocationClickListener: OnLocationClickListener) :
    RecyclerView.Adapter<AllLocationAdapter.AllLocationViewHolder>() {
    var locationList: ArrayList<SavedLocation> = ArrayList()
        set(value) {
            field = value
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllLocationViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.custome_item_show_locations_all_locations, null)
        return AllLocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllLocationViewHolder, position: Int) {
        val location = locationList.get(position)
        holder.bind(location,onLocationClickListener)
    }

    override fun getItemCount(): Int =
        locationList.size


    class AllLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtLocationName: TextView
        var txtLocationAddress: TextView

        //         var txtLocationCoordinates: TextView
//         var txtLocationNote: TextView
        var txtLocationDate: TextView
        var imgLocationImage: ImageView
        var imgLocationImageOPenInMap: ImageView
        var imgLocationImageShareText: ImageView
        var imgLocationImageDeleteLocation: ImageView
        var imgOpenInGoogleDirection: ImageView

        init {
            txtLocationName = itemView.findViewById(R.id.custom_item_allLocations_txt_LocationName)
            txtLocationAddress =
                itemView.findViewById(R.id.custom_item_allLocations_txt_LocationAddress)

            txtLocationDate = itemView.findViewById(R.id.custom_item_allLocations_txt_LocationDate)
            imgLocationImageOPenInMap =
                itemView.findViewById(R.id.custom_item_allLocations_img_openInNormalGoogleMap)
            imgLocationImageShareText =
                itemView.findViewById(R.id.custom_item_allLocations_img_shareText)
            imgLocationImageDeleteLocation =
                itemView.findViewById(R.id.custom_item_allLocations_img_delete)
            imgOpenInGoogleDirection =
                itemView.findViewById(R.id.custom_item_allLocations_btn_openInGoogleMapDirections)
            imgLocationImage = itemView.findViewById(R.id.custom_item_allLocations_img_Image)
        }

        fun bind(location: SavedLocation,onLocationClickListener: OnLocationClickListener) {
            txtLocationName.text = "Name: ${location.name}"
            txtLocationAddress.text = "Address: ${location.loctionAddress.trim()}"

            imgLocationImage.setImageBitmap(location.image)
            txtLocationDate.text = location.date + "   " + location.time

            imgLocationImageOPenInMap.setOnClickListener { onLocationClickListener.onLocationClick(it,location) }
            imgLocationImageShareText.setOnClickListener { onLocationClickListener.onLocationClick(it,location) }
            imgLocationImageDeleteLocation.setOnClickListener { onLocationClickListener.onLocationClick(it,location) }
            imgOpenInGoogleDirection.setOnClickListener { onLocationClickListener.onLocationClick(it,location) }
        }

    }
}

interface OnLocationClickListener {

    fun onLocationClick(view: View,savedLocation: SavedLocation)
}
