package com.example.locationsaver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.R
import com.example.locationsaver.pojo.HistorySearchedLocations
import com.example.locationsaver.pojo.SavedLocation
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class OldUserLocationsAdapter(val context: Context) :
    RecyclerView.Adapter<OldUserLocationsAdapter.OldItemHolder>() {

    var savedLoactioList: ArrayList<SavedLocation> = ArrayList()
        set(value) {
            field = value
        }

    companion object {
        lateinit var onOldHistoryClickListener: OnOldHistoryClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldItemHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.custome_item_old_user_locations, null)
        return OldItemHolder(view)
    }

    override fun onBindViewHolder(holder: OldItemHolder, position: Int) {
        val history = savedLoactioList.get(position)

        holder.bind(history)

    }

    override fun getItemCount(): Int {
        return savedLoactioList.size
    }

    class OldItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewLocationAddress: TextView
        var locationImage: CircleImageView

        init {
            textViewLocationAddress =
                itemView.findViewById(R.id.custome_item_userOldLocation_txtLocation)
            locationImage =
                itemView.findViewById(R.id.custome_item_userOldLocation_imgLocation)
        }


        fun bind(savedLoaction: SavedLocation) {
            textViewLocationAddress.setText(savedLoaction.name)
            locationImage.setImageBitmap(savedLoaction.image)
            if (adapterPosition != RecyclerView.NO_POSITION) {

                itemView.setOnClickListener { onOldHistoryClickListener.onHistoryClick(savedLoaction) }
            }

        }
    }


}

interface OnOldHistoryClickListener {
    fun onHistoryClick(savedLoaction: SavedLocation)
}