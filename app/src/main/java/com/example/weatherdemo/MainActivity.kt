package com.example.weatherdemo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherdemo.databinding.ActivityMainBinding
import com.example.weatherdemo.ui.WeeklyWeatherAdapter
import com.example.weatherdemo.viewmodel.WeatherViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var weeklyWeatherAdapter: WeeklyWeatherAdapter

    private val weatherViewModel: WeatherViewModel by viewModels()

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

    private var mCurrentLocation: Location? = null

    private var mCurrentCityName: String? = null

    private var mCurrentCountry: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(toolbar)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                }
            }
        })

        weatherViewModel.weatherLivaData.observe(this,
        Observer { t ->
            t?.let {
                supportActionBar!!.title = t.city.name
                binding.cityInfo = t.city
                binding.weatherInfo = t.list[0]
                weeklyWeatherAdapter.setWeatherInfos(t.list)
                swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = false }
                if (!mCurrentCountry.equals(t.city.country)) {
                    mCurrentCountry = t.city.country
                    val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return@Observer
                    with (sharedPref.edit())  {
                        putString(getString(R.string.current_country), t.city.country)
                        commit()
                    }
                }
            }
        })

        weatherViewModel.snackBar.observe(this, Observer { text ->
            text?.let {
                Snackbar.make(rootLayout, it, Snackbar.LENGTH_SHORT).show()
                weatherViewModel.onSnackbarShown()
                swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = false }
            }
        })

        weekly_weather.layoutManager = LinearLayoutManager(this)
        weekly_weather.adapter = weeklyWeatherAdapter
        weekly_weather.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        // Setup pull down refreshing
        swipeRefreshLayout.setOnRefreshListener {
            update()
        }
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    private fun update() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        mCurrentCityName = sharedPref.getString(getString(R.string.current_city), null)
        mCurrentCountry = sharedPref.getString(getString(R.string.current_country), null)
        if (mCurrentCityName != null) {
            swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = true }
            weatherViewModel.getWeatherByCityName("$mCurrentCityName,$mCurrentCountry")
        } else {
            if (checkPermissions()) {
                startLocationUpdates()
            } else if (!checkPermissions()) {
                requestPermissions()
            }
        }
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
                    swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = true }
                    weatherViewModel.getWeatherByCoordinates(mCurrentLocation!!.latitude.toFloat(), mCurrentLocation!!.longitude.toFloat())
                    val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
                    with (sharedPref.edit()) {
                        putString(getString(R.string.current_city), null)
                        commit()
                    }
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
                    mCurrentCityName = data?.getStringExtra("city")
                    val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
                    with (sharedPref.edit()) {
                        putString(getString(R.string.current_city), mCurrentCityName)
                        commit()
                    }
                    weatherViewModel.getWeatherByCityName("$mCurrentCityName,$mCurrentCountry")
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
        return true
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
        if(grantResults.isEmpty()) {
            return
        }
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
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
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
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

    private fun hasNumeric(str: String): Boolean {
        val pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")
        val isNum: Matcher = pattern.matcher(str.subSequence(0,1))
        return isNum.matches()
    }
}
