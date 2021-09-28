package com.example.locationsaver.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.locationsaver.Helper.BitmapResizer
import com.example.locationsaver.R
import com.example.locationsaver.databases.local.LocationRoomBuilder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


private const val TAGInit = "HomeFragmentInit"
private const val LOCATION_REQUEST_PERMISSION_CODE = 51
private const val SAVE_LOCATION_INTENT_REQ = 11
private const val NO_TYPE = -1
private const val MAP_TYPE_SHARED_FILE = "map_shared_pref"
private const val MAP_TYPE = "map_type"

private const val RELATIVE_LAYOUT_DOWN_HEIGH = 65
private const val RELATIVE_LAYOUT_UP_HEIGH = 315
private const val MAP_ZOOMING = 18f
lateinit var currentUserLatLng: LatLng

class HomeFragment : Fragment(), OnMapReadyCallback, View.OnClickListener,
    PopupMenu.OnMenuItemClickListener, GoogleMap.OnMarkerClickListener {


    override fun onStart() {
        super.onStart()

    }

    lateinit var imgUserLocation: ImageView
    lateinit var imgCollapseRelative: ImageView
    lateinit var imgShareLocationAsText: ImageView
    lateinit var imgShareLocationInGoogleMaps: ImageView
    lateinit var imgSearchLocationByname: ImageView
    lateinit var imgChangeMapType: ImageView
    lateinit var imgShowAllLocations: ImageView

    var listOfMarkers = ArrayList<Marker>()
    lateinit var relativeLayoutInfos: RelativeLayout
    lateinit var currentUserTabedPosition: LatLng
    lateinit var currentUserPosition: LatLng
    lateinit var txtAddressName: TextView
    lateinit var txtAddressLat: TextView
    lateinit var txtAddressLong: TextView
    lateinit var progressData: ProgressBar
    lateinit var btnSaveLocation: Button
    lateinit var btntest: Button

    var address: Address? = null
    lateinit var googleMap: GoogleMap
    val permissionsList =
        arrayOf<String>(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    lateinit var sharedPreferences: SharedPreferences
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences =
            requireActivity().getSharedPreferences(MAP_TYPE_SHARED_FILE, Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initialize Views
        (requireContext() as AppCompatActivity).supportActionBar?.show()
        val homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(homeFragmentView)

        //InitMap
        initMap(requireActivity())




        return homeFragmentView
    }


    private fun initViews(homeFragmentView: View) {
        imgUserLocation = homeFragmentView.findViewById(R.id.fragmentHome_imgGetUserLocation)
        imgCollapseRelative =
            homeFragmentView.findViewById(R.id.circularImag_def2)
        relativeLayoutInfos =
            homeFragmentView.findViewById(R.id.fragmentHome_relativeLocationActions)
        imgShareLocationAsText =
            homeFragmentView.findViewById(R.id.fragmentHome_imgShareLocationAsText)
        imgShareLocationInGoogleMaps =
            homeFragmentView.findViewById(R.id.fragmentHome_imgOpenLocationInGoogleMaps)
        txtAddressLat = homeFragmentView.findViewById(R.id.fragmentHome_txtCurrentLat)
        txtAddressLong = homeFragmentView.findViewById(R.id.fragmentHome_txtCurrentLong)
        txtAddressName = homeFragmentView.findViewById(R.id.fragmentHome_txtCurrentAddress)
        progressData = homeFragmentView.findViewById(R.id.fragmentHome_progrssGettingData)
        btnSaveLocation = homeFragmentView.findViewById(R.id.fragmentHome_btnSaveLocation)
        imgSearchLocationByname = homeFragmentView.findViewById(R.id.fragmentHome_imgSearch)
        imgShowAllLocations = homeFragmentView.findViewById(R.id.fragmentHome_imgAllLocations)
        imgChangeMapType = homeFragmentView.findViewById(R.id.fragmentHome_imgMapType)
        imgSearchLocationByname.setOnClickListener {


            findNavController().navigate(R.id.action_homeFragment_to_searchForPlaceFragment)
        }


        //Listeners
        imgUserLocation.setOnClickListener(this)
        imgCollapseRelative.setOnClickListener({ Log.d(TAGInit, "initViews: ") })
        imgShareLocationInGoogleMaps.setOnClickListener(this)
        btnSaveLocation.setOnClickListener(this)
        imgShareLocationAsText.setOnClickListener(this)
        imgChangeMapType.setOnClickListener(this)
        imgShowAllLocations.setOnClickListener(this)


    }

    fun locateEveryLocationOnTheMap() {
        if (!::currentUserPosition.isInitialized)
        {
            return
        }
        GlobalScope.launch(Dispatchers.Default) {
            val allLocations = LocationRoomBuilder.buildDataBase(requireContext())
                .LocationDao().getAllLocations()
            for (i in allLocations) {
                val location = i
                val latLng =
                    LatLng(
                        location.loctionLatitude.toDouble(),
                        location.loctionLongitude.toDouble()
                    )

                val resizedBitmap = BitmapResizer.getResizedBitmap(location.image!!, 120, 80)
                val fromBitmap = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                withContext(Dispatchers.Main) {
                    addMarker(latLng, fromBitmap)
                    animateCamera(currentUserPosition, 200, 7f)

                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fragmentHome_imgGetUserLocation -> getUserCurrentLocation()

            R.id.fragmentHome_imgOpenLocationInGoogleMaps -> openCurrentPositionInGoogleMaps()

            R.id.fragmentHome_imgShareLocationAsText -> shareUserSelectedLocation()

            R.id.fragmentHome_btnSaveLocation -> saveUserCurrentLocation()
            R.id.fragmentHome_imgMapType -> changeMapType(v)
            R.id.fragmentHome_imgAllLocations -> locateEveryLocationOnTheMap()

        }
    }


    private fun changeMapType(v: View) {


        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.inflate(R.menu.map_type_popup_menu)
        popupMenu.setOnMenuItemClickListener(this)
        val pop = PopupMenu::class.java.getDeclaredField("mPopup")
        pop.isAccessible = true
        val menu = pop.get(popupMenu)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
        popupMenu.show()
    }


    private fun saveUserCurrentLocation() {
        var isLocationFound = false
        runBlocking {


            GlobalScope.launch(Dispatchers.IO) {
                val locationList = LocationRoomBuilder.buildDataBase(requireContext())
                    .LocationDao().getAllLocations()
                locationList.forEach {
                    Log.d(
                        TAGInit,
                        "saveUserCurrentLocation: ${it.loctionAddress}  ${
                            buildFullAddressStringBuilderOfAddress(address!!)
                        }"
                    )

                    if (it.loctionAddress.trim()
                            .equals(buildFullAddressStringBuilderOfAddress(address!!).trim())
                    ) {
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(
                                requireContext(),
                                "Location Already Exists",
                                Toast.LENGTH_LONG
                            ).show()
                            isLocationFound = true
                        }
                    }
                }
                if (!isLocationFound)
                {
                    val fullAddress = buildFullAddressStringBuilderOfAddress(address!!)
                    val latitude = address?.latitude
                    val longitude = address?.longitude

                    val actionHomeDirections = HomeFragmentDirections.actionHomeFragmentToSaveLocationActivity()
                    actionHomeDirections.setAddress(fullAddress.toString())
                    actionHomeDirections.setLatitude(latitude.toString())
                    actionHomeDirections.setLogitude(longitude.toString())
                    actionHomeDirections.setComeStatus("fromHome")
                    withContext(Dispatchers.Main)
                    {
                        findNavController().navigate(actionHomeDirections)

                    }
                }
            }
        }



    }

    private fun shareUserSelectedLocation() {
        Log.d(TAGInit, "shareUserSelectedLocation: ")
        val addressToShare = StringBuilder()
        addressToShare.append("Location Saver App")
        addressToShare.append("\nAddress: ")

        if (address?.locality != null) {
            addressToShare.append(address?.locality)
        }

        if (address?.subAdminArea != null) {
            addressToShare.append(", " + address?.subAdminArea)
        }
        if (address?.adminArea != null) {
            addressToShare.append(", " + address?.adminArea)
        }
        if (address?.countryName != null) {
            addressToShare.append(", " + address?.countryName)
        }
        addressToShare.append("\n")
        addressToShare.append("coordinates: \n")
        addressToShare.append(
            address?.latitude.toString()
                    + " , "
                    + address?.longitude.toString()
        )
        addressToShare.append("\nGoogle Map:\n")
        addressToShare.append(getGoogleMapShareFormat(address?.latitude!!, address?.longitude!!))
        val shareIntent = Intent()
        shareIntent.also {
            it.action = Intent.ACTION_SEND
            it.type = "text/plain"
            it.putExtra(Intent.EXTRA_TEXT, addressToShare.toString())

            startActivity(it)
        }
        Log.d(TAGInit, "shareUserSelectedLocation: ${addressToShare}")


    }

    private fun openCurrentPositionInGoogleMaps() {

        val currentPositon = currentUserPosition


        val urlAddress =
            "geo:<${currentPositon.latitude}>,<${currentPositon.longitude}>" +
                    "?q=<${address?.latitude}>,<${address?.longitude}>()"
        val googleMapUri = Uri.parse(urlAddress)

        val googleMapIntent: Intent = Intent(Intent.ACTION_VIEW, googleMapUri)
        googleMapIntent.also {
            startActivity(it)
        }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    private fun initMap(activity: FragmentActivity) {

        Log.d(TAGInit, "initMap: start init map")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        Log.d(TAGInit, "initMap: Map Created Sucessffuly")
        googleMap = map
        googleMap.isTrafficEnabled=true
        val map_type = getCurrentMapTypeFromShared()
        if (map_type != NO_TYPE) {
            changeGoogleMapType(map_type)
        }
        googleMap.setOnMarkerClickListener(this)


        googleMap.uiSettings.isCompassEnabled = true
        createMapOptions()
        val latitude: String = HomeFragmentArgs.fromBundle(requireArguments())
            .latitude
        val longitude: String = HomeFragmentArgs.fromBundle(requireArguments())
            .longitude
//
//
        if (!latitude.equals("-1") && !longitude.equals("-1")) {

            val latLng: LatLng = LatLng(latitude.toDouble(), longitude.toDouble())
            setUpLocationOnMap(latLng, 18f)
            Log.d(TAGInit, "onViewCreated: ${latitude} $longitude")


        } else {


            getUserCurrentLocation()
        }

        googleMap.setOnMapLongClickListener { mapLongClicked(it) }


    }

    private fun mapLongClicked(latLng: LatLng) {
        waitingViewsEnable(false)

        for (marker in listOfMarkers) {
            marker.remove()
        }


        setUpLocationOnMap(latLng)
    }

    fun setUpLocationOnMap(latLng: LatLng) {
        addMarker(latLng)
        animateCamera(latLng, 600, googleMap.cameraPosition.zoom)
        val geoLocationByLatLng = getGeoLocationByLatLng(latLng)
        setUpDataInViews(geoLocationByLatLng)
    }

    fun setUpLocationOnMap(latLng: LatLng, zoom: Float) {
        addMarker(latLng)
        animateCamera(latLng, 600, zoom)
        val geoLocationByLatLng = getGeoLocationByLatLng(latLng)
        setUpDataInViews(geoLocationByLatLng)
    }


    private fun createMapOptions() {

        //googleMap.mapType=GoogleMap
        try {
            googleMap.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Log.d(TAGInit, "MapOptions: ${e.message.toString()}")
        }
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false


    }

    fun getUserCurrentLocation() {
        if (!checkGPSOpen()) {
            askUserForOpenGps()
            return
        }


        //red when no connection and GPS
        setImageUserLocationTint(R.color.whiteGray)

        waitingViewsEnable(false)
        for (marker in listOfMarkers) {
            marker.remove()
        }
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        permissionRequires()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionRequires()
        }
        val lastLocation = fusedLocationProviderClient.lastLocation
        try {


            lastLocation.addOnCompleteListener(object : OnCompleteListener<Location> {
                override fun onComplete(task: Task<Location>) {
                    if (task.isSuccessful) {

                        val latLng: LatLng
                        val result = task.result
                        if (result != null) {
                            latLng = LatLng(result.latitude, result.longitude)
                            animateCamera(latLng, 1000, MAP_ZOOMING)
                            currentUserPosition = latLng
                            currentUserTabedPosition = latLng
                            val geoLocationAddress = getGeoLocationByLatLng(latLng)
                            setUpDataInViews(geoLocationAddress)
                            setImageUserLocationTint(R.color.whiteBlue)
                            currentUserLatLng = latLng
                            Log.d(TAGInit, "onComplete: ${geoLocationAddress.toString()}")

                        }

                    } else {
                        Log.d(TAGInit, "onError ${task.exception.toString()}")
                    }
                }
            })
        } catch (e: Exception) {
            Log.d(TAGInit, "getUserCurrentLocation Exepction: ${e.message}")
        }

    }

    private fun askUserForOpenGps() {
        val gpsOptionsIntent = Intent(
            Settings.ACTION_LOCATION_SOURCE_SETTINGS
        )
        startActivityForResult(gpsOptionsIntent, 526)
    }

    private fun checkGPSOpen(): Boolean {
        Log.d(TAGInit, "checkGPSEnable: ")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permissionsList[0]
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                permissionsList[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAGInit, "checkPermission: ")
            permissionRequires()
        }
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return providerEnabled

    }

    fun permissionRequires() {
//Ask User Permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requireActivity().requestPermissions(permissionsList, LOCATION_REQUEST_PERMISSION_CODE)
        }

    }

    fun animateCamera(position: LatLng, animationSpeed: Int, zoom: Float) {
        val camerUpdate = CameraUpdateFactory
            .newLatLngZoom(position, zoom)

        googleMap.animateCamera(camerUpdate, animationSpeed, null)
    }

    fun addMarker(
        position: LatLng
    ) {
        val markerOptions = MarkerOptions()
        markerOptions.position(position)

        val markerAdded = googleMap.addMarker(markerOptions)
        listOfMarkers.add(markerAdded!!)
    }

    fun addMarker(
        position: LatLng, bitmapDescriptor: BitmapDescriptor
    ) {
        val markerOptions = MarkerOptions()
        markerOptions.position(position)
        markerOptions.icon(bitmapDescriptor)
        val markerAdded = googleMap.addMarker(markerOptions)
        listOfMarkers.add(markerAdded!!)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_PERMISSION_CODE)
            if ((grantResults.get(0) == PackageManager.PERMISSION_GRANTED) &&
                (grantResults.get(1) == PackageManager.PERMISSION_GRANTED)
            ) {
                getUserCurrentLocation()

            }

    }

    fun getGeoLocationByLatLng(latLng: LatLng): Address {
        Log.d(TAGInit, "getGeoLocationByLatLng: ")
        val geocoder = Geocoder(requireContext())
        val listOfAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (listOfAddresses.size > 0) {
            address = listOfAddresses.get(0)
            return listOfAddresses.get(0)

        } else {
            Log.d(TAGInit, "getGeoLocationAddress Error: ")
            return Address(Locale.ENGLISH)
        }

    }

    fun setUpDataInViews(address: Address) {
        val fullAddress = StringBuilder()


        try {

            fullAddress.append("Address:  ")
            if (address.locality != null) {
                fullAddress.append(address.locality)
            }

            if (address.subAdminArea != null) {
                fullAddress.append(", " + address.subAdminArea)
            }
            if (address.adminArea != null) {
                fullAddress.append(", " + address.adminArea)
            }
            if (address.countryName != null) {
                fullAddress.append(", " + address.countryName)
            }
            val lonitude = "Logitude: " + address.longitude.toString()
            val latitude = "Latitude: " + address.latitude.toString()
            txtAddressName.text = fullAddress
            txtAddressLong.text = lonitude
            txtAddressLat.text = latitude
            imgShareLocationInGoogleMaps.visibility = View.VISIBLE


        } catch (e: java.lang.Exception) {
            txtAddressName.text = "UnKnown Loaction By Google"
            txtAddressLong.text = ""
            txtAddressLat.text = ""
            imgShareLocationInGoogleMaps.visibility = View.INVISIBLE


        } finally {
            waitingViewsEnable(true)
        }


    }

    fun waitingViewsEnable(visibility: Boolean) {
        if (visibility) {
            progressData.visibility = View.INVISIBLE
            txtAddressName.visibility = View.VISIBLE
            txtAddressLong.visibility = View.VISIBLE
            txtAddressLat.visibility = View.VISIBLE
            if (txtAddressName.text.equals("UnKnown Loaction By Google")) {
                imgShareLocationInGoogleMaps.visibility = View.INVISIBLE
                imgShareLocationInGoogleMaps.visibility = View.INVISIBLE
                btnSaveLocation.visibility = View.INVISIBLE
                imgShareLocationAsText.visibility = View.INVISIBLE
            } else {
                imgShareLocationInGoogleMaps.visibility = View.VISIBLE
                btnSaveLocation.visibility = View.VISIBLE
                btnSaveLocation.visibility = View.VISIBLE
                imgShareLocationAsText.visibility = View.VISIBLE

                //Share Here, Save
            }

        } else {
            progressData.visibility = View.VISIBLE
            txtAddressName.visibility = View.INVISIBLE
            txtAddressLong.visibility = View.INVISIBLE
            txtAddressLat.visibility = View.INVISIBLE
            imgShareLocationInGoogleMaps.visibility = View.INVISIBLE
            btnSaveLocation.visibility = View.INVISIBLE
            imgShareLocationAsText.visibility = View.INVISIBLE

        }
    }

    fun setImageUserLocationTint(colorId: Int) {
        ImageViewCompat.setImageTintList(
            imgUserLocation,
            ColorStateList.valueOf(resources.getColor(colorId))
        )
    }

    fun buildFullAddressStringBuilderOfAddress(address: Address): StringBuilder {
        val fullAddress = StringBuilder()
        fullAddress.also {
            if (address.locality != null) {
                it.append(address.locality + ", ")
            }

            if (address.subAdminArea != null) {
                it.append(address.subAdminArea + ", ")
            }
            if (address.adminArea != null) {
                it.append(address.adminArea + ", ")
            }
            if (address.countryName != null) {
                it.append(address.countryName + ", ")
            }
        }
        return fullAddress

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 526) {

            getUserCurrentLocation()

        }


    }

    lateinit var menuItem: MenuItem
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menuItem = menu.findItem(R.id.webSearchActivity_iconSearch)
        menuItem.setVisible(false)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.mapType_ROADMAP -> changeGoogleMapType(GoogleMap.MAP_TYPE_NORMAL)
            R.id.mapType_SATELLITE -> changeGoogleMapType(GoogleMap.MAP_TYPE_HYBRID)


        }
        return true
    }

    fun changeGoogleMapType(map_type: Int) {
        googleMap.mapType = map_type
        saveCurrentMapTypeInShared(map_type)

    }

    fun saveCurrentMapTypeInShared(map_type: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(MAP_TYPE, map_type)
        editor.apply()

    }

    fun getCurrentMapTypeFromShared(): Int {
        return sharedPreferences.getInt(MAP_TYPE, NO_TYPE)

    }

    fun getGoogleMapShareFormat(lat: Double, lon: Double): String {
        return "https://maps.google.com/maps?q=${lat}%2c${lon}"
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        animateCamera(p0.position, 800, 15f)
        return true
    }


}