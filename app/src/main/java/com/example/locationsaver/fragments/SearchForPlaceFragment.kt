package com.example.locationsaver.fragments

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.helper.InternerConnection
import com.example.locationsaver.R
import com.example.locationsaver.adapters.OldUserLocationsAdapter
import com.example.locationsaver.adapters.OnOldHistoryClickListener
import com.example.locationsaver.adapters.SearchHistoryAdapter
import com.example.locationsaver.adapters.onHistoryItemClickListener
import com.example.locationsaver.pojo.AddressedLocation
import com.example.locationsaver.pojo.HistorySearchedLocations
import com.example.locationsaver.pojo.SavedLocation
import com.example.locationsaver.viewModel.DataBaseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*

private const val TAG = "SearchForPlaceActivity"

class SearchForPlaceFragment : Fragment(R.layout.fragment_search_for_location),
    onHistoryItemClickListener, OnOldHistoryClickListener {
    val dataBaseViewModel:DataBaseViewModel by lazy {
        getViewModel<DataBaseViewModel>()
    }
    lateinit var oldHistoryLinearLayoutManager: LinearLayoutManager
    lateinit var oldUserItemsGridLayoutManager: LinearLayoutManager
    lateinit var searchHistoryAdapter: SearchHistoryAdapter
    lateinit var oldUserLocationsAdapter: OldUserLocationsAdapter

    lateinit var fragmentSearchOldHistoryRecycler: RecyclerView
    lateinit var fragmentSearchTVRecent: TextView
    lateinit var fragmentSearchTxtDef: TextView
    lateinit var fragmentSearchRecyeclerViewUserItemPreview: RecyclerView
    lateinit var fragmentSearchETSearch: EditText
    lateinit var fragmentSearchCardViewResult: CardView
    lateinit var fragmentSearchCardViewTVTextEntered: TextView
    lateinit var fragmentSearchCardViewTVTextActualResult: TextView
    lateinit var fragmentSearchLayoutOldItems: ConstraintLayout
    lateinit var fragmentSearchLayoutNoResultFound: LinearLayout
    lateinit var fragmentSearchbtnBack: ImageView
    lateinit var geocoder: Geocoder
    var enteredSearch: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().findViewById<BottomNavigationView>(R.id.fragmentHome_bottomNav)?.visibility=View.GONE
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_search_for_location, null)
        initViews(view)
        init()
        getDataFromDataBase()
        (requireContext() as AppCompatActivity).supportActionBar?.hide()

        return view
    }

    private fun initViews(view: View) {
        fragmentSearchOldHistoryRecycler = view.findViewById(R.id.fragmentSearch_oldHistoryRecycler)
        fragmentSearchTVRecent = view.findViewById(R.id.fragmentSearch_TV_recent)
        fragmentSearchTxtDef = view.findViewById(R.id.fragmentSearch_txtDef)
        fragmentSearchRecyeclerViewUserItemPreview =
            view.findViewById(R.id.fragmentSearch_recyeclerView_userItemPreview)
        fragmentSearchETSearch = view.findViewById(R.id.fragmentSearch_ET_search)
        fragmentSearchCardViewResult = view.findViewById(R.id.fragmentSearch_cardView_Result)
        fragmentSearchCardViewTVTextEntered =
            view.findViewById(R.id.fragmentSearch_cardView_TV_textEntered)
        fragmentSearchCardViewTVTextActualResult =
            view.findViewById(R.id.fragmentSearch_cardView_TV_textActualResult)
        fragmentSearchLayoutOldItems = view.findViewById(R.id.fragmentSearch_layout_OldItems)
        fragmentSearchLayoutNoResultFound =
            view.findViewById(R.id.fragmentSearch_layout_noResultFound)
        fragmentSearchbtnBack =
            view.findViewById(R.id.fragmentSearch_btnBack)
        fragmentSearchbtnBack.setOnClickListener {
            hideSoftKeys()
            requireActivity().onBackPressed()
//            findNavController().navigate(SearchForPlaceFragmentDirections.actionSearchForPlaceFragmentToHomeFragment()

        }

    }

    private fun getDataFromDataBase() {

        getOldItems()
        getHistory()


    }

    private fun getHistory() {

        lifecycleScope.launch {
            dataBaseViewModel.getAllHistory()
            dataBaseViewModel.historyLiveData.collect {
                searchHistoryAdapter.locationHistoryList.clear()
                Collections.reverse(it)
                searchHistoryAdapter.locationHistoryList.addAll(it)
                searchHistoryAdapter.notifyDataSetChanged()
                if (it.size > 0 && it.get(0).latitude.equals("")) {
                    fragmentSearchOldHistoryRecycler.visibility =
                        View.INVISIBLE
                    fragmentSearchTVRecent.visibility =
                        View.INVISIBLE
                } else {
                    Log.d(TAG, "getHistory: ${it.size}")
                    fragmentSearchLayoutOldItems.visibility = View.VISIBLE
                    fragmentSearchOldHistoryRecycler.visibility =
                        View.VISIBLE
                    fragmentSearchTVRecent.visibility =
                        View.VISIBLE
                }
            }


        }

    }

    private fun getOldItems() {

        lifecycleScope.launch {
            dataBaseViewModel.getSavedLocations()
            dataBaseViewModel.locationsLiveData.collect {
                Log.d(TAG, "getOldItems: ${it}")
                oldUserLocationsAdapter.savedLoactioList.clear()
                Collections.reverse(it)
                oldUserLocationsAdapter.savedLoactioList.addAll(it)
                oldUserLocationsAdapter.notifyDataSetChanged()
                if (it.size > 0 && it.get(0).name.equals("")) {
                    fragmentSearchRecyeclerViewUserItemPreview.visibility =
                        View.INVISIBLE
                    fragmentSearchTxtDef.visibility =
                        View.INVISIBLE
                } else {
                    Log.d(TAG, "getHistory: ${it.size}")
                    fragmentSearchLayoutOldItems.visibility = View.VISIBLE
                    fragmentSearchRecyeclerViewUserItemPreview.visibility =
                        View.VISIBLE
                    fragmentSearchTxtDef.visibility =
                        View.VISIBLE
                }
            }


        }

    }

    fun init() {
        geocoder = Geocoder(requireContext())




        createHistoryRecyclerView()
        createOldItemRecyclerView()

        fragmentSearchETSearch.setOnEditorActionListener(object
            : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (InternerConnection.isConnected(requireContext()))
                    {
                        searchForLocation(v?.text.toString())
                    }
                    else
                    {
                        InternerConnection.makeAToast(requireContext())
                    }
                    return true
                }
                return false
            }

        })


//        addressedLocation.let {
        fragmentSearchCardViewResult.setOnClickListener()
        {
            if (addressedLocation != null) {

                saveLocationToHistory(addressedLocation!!)
                navigateWithCoordinates(addressedLocation!!.latitude, addressedLocation!!.logitude)
            }


        }


    }

    fun navigateWithCoordinates(latitude: String, logitude: String) {
        val action =
            SearchForPlaceFragmentDirections.actionSearchForPlaceFragmentToHomeFragment()
        action.latitude = latitude
        action.longitude = logitude
        findNavController().navigate(action)
    }

    private fun saveLocationToHistory(addressedLocation: AddressedLocation) {
        val textEntered = enteredSearch!!
        val textResult = addressedLocation.address
        val latitude = addressedLocation.latitude
        val longtude = addressedLocation.logitude
        val historySearchedLocations =
            HistorySearchedLocations(textEntered, textResult, longtude, latitude)
        lifecycleScope.launch(Dispatchers.IO)
        {
           dataBaseViewModel.insertHistory(historySearchedLocations)

        }
    }

    var addressedLocation: AddressedLocation? = null

    private fun searchForLocation(text: String) {

        if (text.length == 0) {
            return
        }
        recyclerViewsVisibilty(false)

        val addressList = geocoder.getFromLocationName(text, 1)
        if (addressList.size > 0) {
            resultCardViewVisibilty(true)
            resultNotFoundLayoutVisibile(false)
            val addresses = addressList.get(0)!!
            val nameOfAddress: String = getAddressName(addresses)
            addressedLocation = AddressedLocation(
                latitude = addresses.latitude.toString(),
                logitude = addresses.longitude.toString(),
                address = nameOfAddress
            )
            Log.d(TAG, "searchForLocation: ${addressedLocation}")
            fragmentSearchCardViewTVTextEntered.text = text
            fragmentSearchCardViewTVTextActualResult.text =
                nameOfAddress
            hideSoftKeys()
            enteredSearch = text


        } else {
            Log.d(TAG, "searchForLocation nothing:")

            resultCardViewVisibilty(false)
            resultNotFoundLayoutVisibile(true)
        }

    }


    private fun createOldItemRecyclerView() {
        oldHistoryLinearLayoutManager = LinearLayoutManager(requireContext())
        fragmentSearchOldHistoryRecycler.also {
            it.setHasFixedSize(true)
            it.layoutManager = oldHistoryLinearLayoutManager
            it.adapter = searchHistoryAdapter
        }

    }

    private fun createHistoryRecyclerView() {
        oldUserLocationsAdapter = OldUserLocationsAdapter(requireContext())
        OldUserLocationsAdapter.onOldHistoryClickListener = this
        oldUserItemsGridLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        searchHistoryAdapter = SearchHistoryAdapter(requireContext())
        SearchHistoryAdapter.onHistoryItemClickListener = this
        fragmentSearchRecyeclerViewUserItemPreview
            .also {
                it.setHasFixedSize(true)
                it.layoutManager = oldUserItemsGridLayoutManager
                it.adapter = oldUserLocationsAdapter
            }

    }

    fun recyclerViewsVisibilty(isVisible: Boolean) {
        if (isVisible) {
            fragmentSearchLayoutOldItems.visibility =
                View.VISIBLE

        } else {
            fragmentSearchLayoutOldItems.visibility =
                View.INVISIBLE
        }

    }

    fun resultCardViewVisibilty(isVisible: Boolean) {
        if (isVisible) {
            fragmentSearchCardViewResult.visibility = View.VISIBLE


        } else {
            fragmentSearchCardViewResult.visibility =
                View.INVISIBLE

        }
    }

    fun resultNotFoundLayoutVisibile(isVisible: Boolean) {
        if (isVisible) {
            fragmentSearchLayoutNoResultFound.visibility =
                View.VISIBLE


        } else {
            fragmentSearchLayoutNoResultFound.visibility =
                View.INVISIBLE

        }
    }

    fun getAddressName(address: Address): String {
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
                it.append(address.countryName + "")
            }
        }
        return fullAddress.toString()

    }

    fun hideSoftKeys() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var currentFocusView = requireActivity().currentFocus
        if (currentFocusView == null) {
            currentFocusView = View(requireContext());
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
    }

    override fun onItemClick(historySearchedLocations: HistorySearchedLocations) {
        navigateWithCoordinates(
            historySearchedLocations.latitude,
            historySearchedLocations.logitude
        )
    }

    override fun onHistoryClick(savedLoaction: SavedLocation) {
        navigateWithCoordinates(savedLoaction.loctionLatitude, savedLoaction.loctionLongitude)

    }

    override fun onPause() {
        super.onPause()
        requireActivity().findViewById<BottomNavigationView>(R.id.fragmentHome_bottomNav)?.visibility=View.VISIBLE

    }
}


