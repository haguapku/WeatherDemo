package com.example.weatherdemo

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.SearchRecentSuggestions
import android.provider.Settings
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.weatherdemo.databinding.ActivityMainBinding
import com.example.weatherdemo.util.HistorySearchAdapter
import com.example.weatherdemo.util.HistorySearchSuggestionsProvider
import com.example.weatherdemo.util.OnHistoryDeleteClickListener
import com.example.weatherdemo.util.OnItemClick
import com.example.weatherdemo.viewmodel.WeatherViewModel
import com.example.weatherdemo.viewmodel.WeatherViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var factory: WeatherViewModelFactory

    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var searchView: SearchView

    private lateinit var textView: TextView

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private val REQUEST_CHECK_SETTINGS = 0x1

    private val REQUEST_CITY_NAME = 0x2

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    private lateinit var mSettingsClient: SettingsClient

    private lateinit var mLocationRequest: LocationRequest

    private lateinit var mLocationSettingsRequest: LocationSettingsRequest

    private lateinit var mLocationCallback: LocationCallback

    private lateinit var mCurrentLocation: Location

    private lateinit var mSuggestionAdapter: HistorySearchAdapter

    private lateinit var suggestions: SearchRecentSuggestions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(toolbar)

        weatherViewModel = ViewModelProviders.of(this, factory).get(WeatherViewModel::class.java)

        weatherViewModel.weatherLivaData.observe(this,
        Observer { t ->
            t?.let {
                supportActionBar!!.title = t.city.name
                binding.cityInfo = t.city
                binding.mainData = t.list[0].main
                binding.windInfo = t.list[0].wind
                binding.cloudsInfo = t.list[0].clouds
            }
        })

        weatherViewModel.snackbar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(rootLayout, it, Snackbar.LENGTH_SHORT).show()
                weatherViewModel.onSnackbarShown()
            }
        })

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        mSuggestionAdapter = HistorySearchAdapter(this, R.layout.item_search_history_list, null)
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()

        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS

        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mLocationRequest.numUpdates = 1
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                mCurrentLocation = locationResult!!.lastLocation

                if (mCurrentLocation == null) {
                    textView.text = "No loaction data"
                }
                mCurrentLocation?.run {
                    weatherViewModel.getWeatherByCoordinates(mCurrentLocation.latitude.toFloat(), mCurrentLocation.longitude.toFloat())
                }

            }
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Timber.i(
                    "User agreed to make required location settings changes."
                )
                Activity.RESULT_CANCELED -> {
                    Timber.i("User chose not to make required location settings changes.")
                }
            }
            REQUEST_CITY_NAME -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val city = data?.getStringExtra("city")
                    weatherViewModel.getWeatherByCityName(city)
                }
                Activity.RESULT_CANCELED -> {
                    Timber.i("User chose not to make required location settings changes.")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchMenuItem = menu!!.findItem(R.id.action_search)
        searchView = searchMenuItem.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        suggestions = SearchRecentSuggestions(this,
            HistorySearchSuggestionsProvider.AUTHORITY, HistorySearchSuggestionsProvider.MODE)

        mSuggestionAdapter.setListener(object : OnHistoryDeleteClickListener {
            override fun onItemClick(query: String) {
                suggestions.clearHistory()
                val cursor = getRecentSuggestions("")
                cursor?.let {
                    mSuggestionAdapter.swapCursor(cursor)
                }
                searchView.onActionViewCollapsed()
//                val deleteSuccess = deleteSuggestions(query)
//                if(deleteSuccess == -1){
//                    Toast.makeText(this@MainActivity,"Delete failed",Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(this@MainActivity,"Delete succeeded",Toast.LENGTH_SHORT).show()
//                }
            }
        })

        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            suggestionsAdapter = mSuggestionAdapter
            setOnSearchClickListener {
                val cursor = getRecentSuggestions("")
                cursor?.let {
                    mSuggestionAdapter.swapCursor(cursor)
                }
            }
            setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (TextUtils.isEmpty(query)) return false
                    suggestions.saveRecentQuery(query, null)
                    if (hasNumeric(query!!)) {
                        weatherViewModel.getWeatherByZipCode(query)
                    } else {
                        weatherViewModel.getWeatherByCityName(query)
                    }
                    searchView.onActionViewCollapsed()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val cursor = getRecentSuggestions(newText!!)
                    cursor?.let {
                        mSuggestionAdapter.swapCursor(cursor)
                    }
                    return false
                }
            })
            setOnSuggestionListener(object :SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val str = mSuggestionAdapter!!.getSuggestionText(position)
                    if (hasNumeric(str!!)) {
                        weatherViewModel.getWeatherByZipCode(str)
                    } else {
                        weatherViewModel.getWeatherByCityName(str)
                    }
                    searchView.onActionViewCollapsed()
                    return true
                }
            })
            return true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
                true
            }

            R.id.activity_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivityForResult(intent, REQUEST_CITY_NAME)
                true
            }

            R.id.location_search -> {
                if (checkPermissions()) {
                    startLocationUpdates()
                } else if (!checkPermissions()) {
                    requestPermissions()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun stopLocationUpdates() {

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(this) {
            }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(actionStringId), listener).show()
    }

    private fun checkPermissions(): Boolean {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                android.R.string.ok, View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.size == 0) {
            return
        }
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                Timber.i("User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("Permission granted, updates requested, starting location updates")
                    startLocationUpdates()
                }
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                    R.string.settings, View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }

    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(this, OnSuccessListener<LocationSettingsResponse> {
                Timber.i("All location settings are satisfied.")
                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )
            })
            .addOnFailureListener(this, OnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Timber.i(
                            "Location settings are not satisfied. Attempting to upgrade location settings "
                        )
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                        } catch (sie: IntentSender.SendIntentException) {
                            Timber.i("PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Timber.e(errorMessage)
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    fun deleteSuggestions(id:String): Int? {
        val uriBuilder = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(HistorySearchSuggestionsProvider.AUTHORITY)

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)
        val uri = uriBuilder.build()
        return  contentResolver.delete(uri,"_id=$id",null)
    }

    fun getRecentSuggestions(query: String): Cursor? {
        val uriBuilder = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(HistorySearchSuggestionsProvider.AUTHORITY)

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)

        val selection = " ?"
        val selArgs = arrayOf(query)

        val uri = uriBuilder.build()

        return contentResolver.query(uri, null, selection, selArgs, null)
    }


    private fun hasNumeric(str: String): Boolean {
        val pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")
        val isNum: Matcher = pattern.matcher(str.subSequence(0,1))
        return isNum.matches()
    }
}
