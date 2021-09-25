package com.example.locationsaver.fragments

import android.app.Activity.MODE_PRIVATE
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.locationsaver.Helper.BitmabCreator
import com.example.locationsaver.Helper.FileSizes
import com.example.locationsaver.Helper.ProgressBarDialog
import com.example.locationsaver.R
import com.example.locationsaver.databases.local.LocationRoomBuilder
import com.example.locationsaver.pojo.AddressedLocation
import com.example.locationsaver.pojo.SavedLocation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SaveLocationTags"
const val READ_STORAGE_CODE = 101
private const val GALLERY_INTETN_REQ: Int = 41
private const val WEB_SEARCH_IAMGE: Int = 71
private const val _SHARED_PREFERNCE_NAME = "saveLocationPref"
private const val _SHARED_PREFERNCE_LOCATION_NAME = "locationName"
private const val _SHARED_PREFERNCE_LOCATION_ADDRESS = "locationAddress"
private const val _SHARED_PREFERNCE_LOCATION_COORDINATES_LATITUDE = "locationCoordinatesLatitude"
private const val _SHARED_PREFERNCE_LOCATION_COORDINATES_LONGITUDE = "locationCoordinatesLongitude"
private const val _SHARED_PREFERNCE_LOCATION_NOTE = "locationNote"

class SaveLocationActivity : Fragment(), View.OnClickListener {
    var pickedImageUri: String = "-1"
    lateinit var addressedLocation: AddressedLocation


    //Views
    lateinit var locationImage: ImageView
    lateinit var txtView_fromDevice: TextView
    lateinit var txtView_fromWeb: TextView
    lateinit var txtInput_ET_name: TextInputEditText;
    lateinit var txtInput_ET_address: TextInputEditText;
    lateinit var txtInput_ET_coordinatesLatitude: TextInputEditText;
    lateinit var txtInput_ET_coordinatesLongitude: TextInputEditText;
    lateinit var txtInput_ET_notes: TextInputEditText;
    lateinit var btn_save: Button
    lateinit var btn_cancel: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.fragment_save_location, null)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as  AppCompatActivity).supportActionBar?.hide()
        activity?.findViewById<BottomNavigationView>(R.id.fragmentHome_bottomNav)?.visibility =
            View.GONE

    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<BottomNavigationView>(R.id.fragmentHome_bottomNav)?.visibility =
            View.VISIBLE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireContext() as AppCompatActivity).supportActionBar?.show()

        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        val savedArgs = SaveLocationActivityArgs.fromBundle(requireArguments())
        addressedLocation =
            AddressedLocation(savedArgs.latitude, savedArgs.logitude, savedArgs.address)
        if (savedArgs.comeStatus.equals("fromHome")) {
            setupData(savedArgs.address, savedArgs.latitude, savedArgs.logitude)
        } else if (savedArgs.comeStatus.equals("fromWeb")) {
            _getSharedPrefrences()
            if (!savedArgs.imageUrl.equals("-1")) {
                Picasso.get().load(savedArgs.imageUrl).into(locationImage)

            }
            pickedImageUri = savedArgs.imageUrl
        }
        initListeners()
    }

    private fun initViews(view: View) {
        view.apply {
            locationImage = findViewById(R.id.activitySaveLocation_imgViewLocation)
            txtView_fromDevice = findViewById(R.id.activitySaveLocation_TV_chooseFromDevice)
            txtView_fromWeb = findViewById(R.id.activitySaveLocation_TV_chooseOnline)
            txtInput_ET_name = findViewById(R.id.activitySaveLocation_ET_LocationName)
            txtInput_ET_address = findViewById(R.id.activitySaveLocation_ET_LocationAddress)
            txtInput_ET_coordinatesLatitude =
                findViewById(R.id.activitySaveLocation_ET_LocationLatitude)
            txtInput_ET_coordinatesLongitude =
                findViewById(R.id.activitySaveLocation_ET_LocationLongitude)
            txtInput_ET_notes = findViewById(R.id.activitySaveLocation_ET_LocationNote)
            btn_save = findViewById(R.id.activitySaveLocation_btnSave)
            btn_cancel = findViewById(R.id.activitySaveLocation_btnCancel)

        }
        txtInput_ET_name.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKey()
                    return true
                }
                return false
            }

        })

    }

    private fun hideSoftKey() {
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(requireContext())
        }
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    val permissionsList = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    //
    private fun initListeners() {
        txtView_fromDevice.setOnClickListener(this)
        txtView_fromWeb.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)

    }

    //
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activitySaveLocation_TV_chooseFromDevice -> chooseImageFromuserDevice()
            R.id.activitySaveLocation_TV_chooseOnline -> chooseImageOnline()
            R.id.activitySaveLocation_btnSave -> saveLocationIntoRoom()
            R.id.activitySaveLocation_btnCancel -> userCancel()

        }

    }

    //
    private fun userCancel() {


    }

    //
    private fun saveLocationIntoRoom() {

        if (txtInput_ET_name.text.toString()
                .equals("")
        ) {
            txtInput_ET_name.error =
                "enter location name first"
            return
        }
        if (pickedImageUri.equals("-1")) {
            Toast.makeText(requireContext(), "choose image first", Toast.LENGTH_LONG).show()
            return
        } else {
            saveLocatoin()
        }


    }

    //
    private fun saveLocatoin() {
        ProgressBarDialog.createProgressDialog(
            requireContext(),
            false,
            "Saving Location",
            "In Progress"
        )
        ProgressBarDialog.showProgressDialog()
        val locationDao = LocationRoomBuilder.buildDataBase(requireContext()).LocationDao()

        GlobalScope.launch(Dispatchers.IO) {
            val location = getLocationForSaving()
            if (location != null) {
                Log.d(TAG, "saveLocatoin: ${location.toString()}")
                locationDao.insertLocation(location)
                delay(500)
                ProgressBarDialog.dismissProgressDialog()
                clearData()

            }

            Log.d(TAG, "saveLocatoin: " + locationDao.getAllLocations())
        }


    }

    private fun clearData() {
        lifecycleScope.launch(Dispatchers.Main)
        {
            Toast.makeText(requireContext(), "Location Saved", Toast.LENGTH_LONG).show()

        }

    }

    //
    private suspend fun getLocationForSaving(): SavedLocation? {

        val locationName =
            txtInput_ET_name.text.toString()
        val locationNote =
            txtInput_ET_notes.text.toString()
        val longitude = txtInput_ET_coordinatesLongitude.text.toString()
        val latitude = txtInput_ET_coordinatesLatitude.text.toString()
        val address = txtInput_ET_address.text.toString()
        val addedDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val addedtimeFormat = SimpleDateFormat("HH:mm:ss")
        val addedDate = addedDateFormat.format(Date())
        val addedTime = addedtimeFormat.format(Date())
        Log.d(TAG, "getLocationForSaving: ${longitude} $latitude $address")
        val locationBitmap = BitmabCreator.getBitmapFromLink(pickedImageUri, requireContext())
        val location = SavedLocation(
            locationName,
            locationNote,
            latitude,
            longitude,
            address,
            locationBitmap,
            addedDate,
            addedTime
        )

        return location
    }


    private fun chooseImageOnline() {
        _clearSharedPrefrences()
        saveToSharedPreference()


        val action = SaveLocationActivityDirections.actionSaveLocationActivityToWebSearchActivity()
        findNavController().navigate(action)

    }


    private fun saveToSharedPreference() {
        val sharedPreferences =
            requireContext().getSharedPreferences(_SHARED_PREFERNCE_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(_SHARED_PREFERNCE_LOCATION_ADDRESS, txtInput_ET_address.text.toString())
        editor.putString(
            _SHARED_PREFERNCE_LOCATION_COORDINATES_LATITUDE,
            txtInput_ET_coordinatesLatitude.text.toString()
        )
        editor.putString(
            _SHARED_PREFERNCE_LOCATION_COORDINATES_LONGITUDE,
            txtInput_ET_coordinatesLongitude.text.toString()
        )
        editor.putString(_SHARED_PREFERNCE_LOCATION_NAME, txtInput_ET_name.text.toString())
        editor.putString(_SHARED_PREFERNCE_LOCATION_NOTE, txtInput_ET_notes.text.toString())
        editor.apply()
    }

    private fun _clearSharedPrefrences() {
        val sharedPreferences =
            requireContext().getSharedPreferences(_SHARED_PREFERNCE_NAME, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

    }

    private fun _getSharedPrefrences() {
        val sharedPreferences =
            requireContext().getSharedPreferences(_SHARED_PREFERNCE_NAME, MODE_PRIVATE)
        val note = sharedPreferences.getString(_SHARED_PREFERNCE_LOCATION_NOTE, "-1")
        val name = sharedPreferences.getString(_SHARED_PREFERNCE_LOCATION_NAME, "-1")
        val address = sharedPreferences.getString(_SHARED_PREFERNCE_LOCATION_ADDRESS, "-1")
        val coordinates_latitude = sharedPreferences.getString(
            _SHARED_PREFERNCE_LOCATION_COORDINATES_LATITUDE, "-1"
        )
        val coordinates_longitude = sharedPreferences.getString(
            _SHARED_PREFERNCE_LOCATION_COORDINATES_LONGITUDE, "-1"
        )

        txtInput_ET_name.setText(name)
        txtInput_ET_address.setText(address)
        txtInput_ET_coordinatesLongitude.setText(coordinates_longitude)
        txtInput_ET_coordinatesLatitude.setText(coordinates_latitude)
        txtInput_ET_address.setText(address)
        txtInput_ET_notes.setText(note)


    }


    private fun chooseImageFromuserDevice() {
        Log.d(TAG, "chooseImageFromuserDevice: ")

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permissionsList[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(permissionsList, READ_STORAGE_CODE)
            }


        } else {
            openImagePickIntent()
        }


    }

    //
    private fun openImagePickIntent() {

        val galleryIntetnt = Intent()
        galleryIntetnt.setType("image/*");
        galleryIntetnt.action = Intent.ACTION_PICK
        startActivityForResult(galleryIntetnt, GALLERY_INTETN_REQ)


    }


    //
    private fun setupData(address: String?, latitude: String?, longitude: String?) {

        txtInput_ET_address.setText(address)
        txtInput_ET_coordinatesLatitude.setText(latitude)
        txtInput_ET_coordinatesLongitude.setText(longitude)

    }

    //
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_CODE) {
            if (grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: ")
                openImagePickIntent()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $resultCode $resultCode")
        if (requestCode == GALLERY_INTETN_REQ && resultCode == RESULT_OK) {
            val fileSizes = FileSizes.getFileSize(data?.data!!, requireContext())

            fileSizes?.let {
                if (fileSizes > 200) {
                    Toast.makeText(
                        requireContext(),
                        "image is too big to save in local device storage", Toast.LENGTH_LONG
                    ).show()
                    pickedImageUri = "-1"
                    locationImage.setImageDrawable(
                        resources.getDrawable(R.drawable.defaul_image)
                    )

                } else {
                    setUpImage(data)
                    pickedImageUri = data.data.toString()
                }
            }
        }
        if (requestCode == WEB_SEARCH_IAMGE && resultCode == RESULT_OK) {
            val imageUrl = data?.getStringExtra(WebSearchActivity.IMAGE_RESULT)
            setUpImage(imageUrl!!)
        }
    }

    private fun setUpImage(data: Intent?) {
        val imgUri = data?.data
        locationImage
            .setImageURI(imgUri)

    }

    private fun setUpImage(imageUrl: String) {
        Log.d(TAG, "setUpImage: ${imageUrl}")
        pickedImageUri = imageUrl
        Picasso.get().load(imageUrl).into(
            locationImage

        )


    }


}