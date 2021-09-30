package com.example.locationsaver.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.R
import com.example.locationsaver.adapters.AllLocationAdapter
import com.example.locationsaver.adapters.OnLocationClickListener
import com.example.locationsaver.databases.local.LocationRoomBuilder
import com.example.locationsaver.pojo.SavedLocation
import com.example.locationsaver.viewModel.DataBaseViewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*

private const val TAG = "LocationsFragmentList"

class LocationsFragment : Fragment(R.layout.fragment_locations), OnLocationClickListener {
    val dataBaseViewModel by lazy {
        getViewModel<DataBaseViewModel>()
    }
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var allLocationAdapter: AllLocationAdapter
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_locations, null)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.fragmentAllLocation_recyclerView)
        linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        allLocationAdapter = AllLocationAdapter(requireContext(), this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = allLocationAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            dataBaseViewModel.getSavedLocations()
            dataBaseViewModel.locationsLiveData.collect {
                allLocationAdapter.locationList.clear()
                Collections.reverse(it)
                allLocationAdapter.locationList.addAll(it)
                Log.d(TAG, "onViewCreated: ${it}")
                allLocationAdapter.notifyDataSetChanged()
            }
        }


    }

    override fun onLocationClick(view: View, savedLocation: SavedLocation) {
        when (view.id) {
            R.id.custom_item_allLocations_img_delete -> deleteLocation(savedLocation)
            R.id.custom_item_allLocations_img_shareText -> shareLocationAsText(savedLocation)
            R.id.custom_item_allLocations_img_openInNormalGoogleMap -> openNormalGoolgeMap(
                savedLocation
            )
            R.id.custom_item_allLocations_btn_openInGoogleMapDirections -> openGoogleMapDirection(
                savedLocation
            )
        }
    }

    private fun openGoogleMapDirection(savedLocation: SavedLocation) {

        val navigationIntentUri =
            Uri.parse("google.navigation:q=" + savedLocation.loctionLatitude + "," + savedLocation.loctionLongitude)


        val intent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)


    }

    private fun openNormalGoolgeMap(savedLocation: SavedLocation) {


        val urlAddress =
            "geo:<${savedLocation.loctionLatitude}>,<${savedLocation.loctionLongitude}>" +
                    "?q=<${savedLocation.loctionLatitude}>,<${savedLocation.loctionLongitude}>()"
        val googleMapUri = Uri.parse(urlAddress)

        val googleMapIntent: Intent = Intent(Intent.ACTION_VIEW, googleMapUri)
        googleMapIntent.also {
            it.setPackage("com.google.android.apps.maps")

            startActivity(it)
        }

    }

    private fun shareLocationAsText(address: SavedLocation) {
        val addressToShare = StringBuilder()
        addressToShare.append("Location Saver App")
        addressToShare.append("\nName: ${address.name}")
        addressToShare.append("\nAddress: ${address.loctionAddress}")
        addressToShare.append("\n")
        addressToShare.append("coordinates: \n")
        addressToShare.append(
            address.loctionLatitude.toString()
                    + " , "
                    + address.loctionLongitude.toString()
        )
        addressToShare.append("\nGoogle Map:\n")
        addressToShare.append(
            getGoogleMapShareFormat(
                address.loctionLatitude,
                address.loctionLongitude
            )
        )
        val shareIntent = Intent()
        shareIntent.also {
            it.action = Intent.ACTION_SEND
            it.type = "text/plain"
            it.putExtra(Intent.EXTRA_TEXT, addressToShare.toString())

            startActivity(it)
        }
    }

    private fun deleteLocation(savedLocation: SavedLocation) {

        lifecycleScope.launch(IO) {
            val job = launch {
                dataBaseViewModel.deleteLocation(savedLocation)
            }
            job.invokeOnCompletion {
                Log.d(TAG, "deleteLocation: ")
                Log.d(TAG, "deleteLocation: 2")
                updateUI(savedLocation)
            }


        }
    }

    private fun updateUI(savedLocation: SavedLocation) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main)
            {
                allLocationAdapter.locationList.remove(savedLocation)
                allLocationAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Location Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getGoogleMapShareFormat(lat: String, lon: String): String {
        return "https://maps.google.com/maps?q=${lat}%2c${lon}"
    }
}