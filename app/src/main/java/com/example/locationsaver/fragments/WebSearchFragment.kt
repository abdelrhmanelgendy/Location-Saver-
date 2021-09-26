package com.example.locationsaver.fragments

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.Helper.InternerConnection
import com.example.locationsaver.R
import com.example.locationsaver.adapters.OnImageClickListener
import com.example.locationsaver.adapters.WebImageAdapter
import com.example.locationsaver.databases.remot.ImageWebSeachClient
import com.example.locationsaver.databases.remot.OnFillStateFlowListener
import com.example.locationsaver.pojo.ImageAdapter
import com.example.locationsaver.pojo.imageFromWeb.ImageWebSearch
import com.example.locationsaver.pojo.imageFromWeb.Value
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


lateinit var imageWebSeachClient: ImageWebSeachClient
lateinit var gridLayoutManager: GridLayoutManager
private const val TAG = "WebSearchActivity"
const val SEARCH_COUNTRY = "Egypt"

class WebSearchActivity : Fragment(R.layout.fragment_web_search), OnImageClickListener {
    lateinit var webImageAdapter: WebImageAdapter


    //Views

    lateinit var recyclerView: RecyclerView
    lateinit var imageDeleteText: ImageView
    lateinit var imageBack: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var txt_ET_search: EditText
    lateinit var toolbar: Toolbar
    lateinit var linearLayoutNoResult: LinearLayout

    companion object {
        public const val IMAGE_RESULT = "result"
    }

    var lastSearchText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)


    }


    fun initViews(view: View) {
        view.apply {
            recyclerView = findViewById(R.id.activityWebSearch_recyclerView)
            progressBar = findViewById(R.id.activityWebSearch_progressBar)
            txt_ET_search = findViewById(R.id.activityWebSearch_ET_searchText)
            imageDeleteText = findViewById(R.id.activityWebSearch_btnDeleteText)
            toolbar = findViewById(R.id.webSearchActivity_toolBar)
            imageBack = findViewById(R.id.activityWebSearch_imgBack)
            linearLayoutNoResult = findViewById(R.id.activityWebSearch_resultNotFound)
            (requireContext() as AppCompatActivity).supportActionBar?.hide()

        }





        imageDeleteText.setOnClickListener({
            txt_ET_search.setText("")

        })
        imageBack.setOnClickListener(
            {
                requireActivity().onBackPressed()
            }
        )
        txt_ET_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                linearLayoutNoResult.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        txt_ET_search.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                if (v?.id == R.id.activityWebSearch_ET_searchText &&
                    actionId == EditorInfo.IME_ACTION_SEARCH
                ) {
                    if (internetConnectionOk()) {
                        startSearching(v.text.toString())
                    }
                    else
                    {
                        InternerConnection.makeAToast(requireContext())
                    }

                }

                return true
            }
        })


        ImageWebSeachClient.onFillStateFlowListener = object : OnFillStateFlowListener {
            override fun fillData(imageWebSearch: ImageWebSearch) {
                GlobalScope.launch(Dispatchers.Main) {
                    webImageAdapter.notifyDataSetChanged()
                    setViewsVisibity(false)
                }

            }
        }
        gridLayoutManager = GridLayoutManager(requireContext(), 4)
        imageWebSeachClient = ViewModelProvider(this).get(ImageWebSeachClient::class.java)

        webImageAdapter = WebImageAdapter(requireContext(), this)
        recyclerView.also {
            it.layoutManager = gridLayoutManager
            it.adapter = webImageAdapter
        }


    }

    private fun internetConnectionOk(): Boolean {
        return InternerConnection.isConnected(requireContext())
    }

    private fun startSearching(searchText: String) {

        if (!searchText.equals("") && !searchText.equals(lastSearchText)) {
            showHideSoftKey(false, requireActivity())
//    val inputMethodManager=requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//            requireActivity().

            Log.d(TAG, "Hit API with " + searchText)
            linearLayoutNoResult.visibility = View.INVISIBLE
            setViewsVisibity(true)
            hitApi(searchText)
            lastSearchText = searchText

        } else {
            Log.d(TAG, "startSearching: Redundant")
            if (searchText.equals(lastSearchText)) {
                Log.d(TAG, "startSearching:Run Redundant")
                runRedundentFun()
            }
        }

    }

    private fun runRedundentFun() {
        setViewsVisibity(true)
        GlobalScope.launch(Dispatchers.Main) {
            delay(600)
            setViewsVisibity(false)
            showHideSoftKey(false, requireActivity())
        }

    }

    private fun hitApi(searchText: String) {


        Log.d(TAG, "hitApi: ")
        imageWebSeachClient.getData(
            searchText,
            SEARCH_COUNTRY,
            reapiApiKey = resources.getString(R.string.x_rapidapi_key),
            reapiApiHost = resources.getString(R.string.x_rapidapi_host)
        )
        GlobalScope.launch(Dispatchers.Main) {

            imageWebSeachClient.imageStateFlow
                .collect {
                    val imageList: ArrayList<ImageAdapter> = buildImageFromResult(it.value)

                    webImageAdapter.imageList.clear()
                    webImageAdapter.imageList.addAll(imageList)

                    Log.d(TAG, "onCreate: ${imageList.toString()}")
                    Log.d(TAG, "onCreate: ${imageList.size}")
                    if (webImageAdapter.imageList.size == 0) {
                        linearLayoutNoResult.visibility = View.VISIBLE
                    } else {
                        linearLayoutNoResult.visibility = View.INVISIBLE
                    }
                }
        }

    }

    private fun buildImageFromResult(value: List<Value>): ArrayList<ImageAdapter> {
        var index = 0
        val imageList: ArrayList<ImageAdapter> = ArrayList()
        for (value in value) {
            val imageUrl = value.thumbnailUrl
            val position = index
            imageList.add(ImageAdapter(position, imageUrl))
            index++

        }
        return imageList
    }

    fun setViewsVisibity(progrssVisibility: Boolean) {
        if (progrssVisibility) {

            recyclerView.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

        } else {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }


    }

    override fun onImageClick(image: ImageAdapter) {

        val action = WebSearchActivityDirections.actionWebSearchActivityToSaveLocationActivity()
            .setImageUrl(image.imageUrl)
            .setComeStatus("fromWeb")
        findNavController().navigate(action)


    }

    fun showHideSoftKey(show: Boolean, activity: Activity) {

        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (show) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } else {
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.web_search_icon, menu)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().findViewById<BottomNavigationView>(R.id.fragmentHome_bottomNav)?.visibility =
            View.GONE
    }
}