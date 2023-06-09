package com.example.roveapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.VoiceInteractor
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.roveapp.databinding.ActivityHeatMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.google.android.libraries.places.api.Places
import org.json.JSONArray
import android.os.Looper
import androidx.core.app.ActivityCompat
import android.provider.Settings
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonArray
import java.io.IOException
import java.lang.reflect.Method
import java.net.URL
import kotlinx.android.synthetic.main.activity_heat_map.*
import java.util.*

class HeatMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHeatMapBinding
    private lateinit var btn: FloatingActionButton
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var marker: Marker
    private lateinit var pieBtn : FloatingActionButton
    private lateinit var barBtn : FloatingActionButton
    private lateinit var navView: NavigationView
    var currentLocation: LatLng = LatLng(26.8467, 80.9462)
    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHeatMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btn = binding.addButton
        pieBtn = binding.pieButton
        barBtn = binding.barButton
        navView = binding.navView

        barBtn.setOnClickListener{
            startActivity(Intent(this, BarChart::class.java))
        }
        pieBtn.setOnClickListener{
            startActivity(Intent(this, PieChart::class.java))
        }
        btn.setOnClickListener{
            val intent = Intent(this, ReportCrimeActivity::class.java)
            startActivity(intent)
        }


        val drawerLayout: DrawerLayout= binding.drawerLayout


        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        navView.setNavigationItemSelectedListener() {
            when(it.itemId){
                R.id.nav_home -> Toast.makeText(applicationContext,"Clicked Home",Toast.LENGTH_SHORT).show()
                R.id.duty_sch -> Toast.makeText(applicationContext,"Duty Schedule",Toast.LENGTH_SHORT).show()
                R.id.crime_identify -> startActivity(Intent(this, PieChart::class.java))
                R.id.crime_list -> Toast.makeText(applicationContext,"Crime List",Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(applicationContext,"Clicked Share",Toast.LENGTH_SHORT).show()
                R.id.nav_logout ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent= Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } //Toast.makeText(applicationContext,"Clicked Logout",Toast.LENGTH_SHORT).show()
            }

            true
        }

        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        // Initializing fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Adding functionality to the button
        val btn = findViewById<FloatingActionButton>(R.id.myLocation)
        btn.setOnClickListener {
            getLastLocation()
        }
    }

    // Get current location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        //mMap.clear()
                        //mMap.addMarker(MarkerOptions().position(currentLocation))
                        mMap.setMyLocationEnabled(true)
                        mMap.getUiSettings().setMyLocationButtonEnabled(false)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    // Get current location, if shifted
    // from previous location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Looper.myLooper()?.let {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()!!
            )
        }
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) //|| locationManager.isProviderEnabled(
            //LocationManager.NETWORK_PROVIDER
        //)
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            pERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun getJsonDataFromAsset(fileName: String): JSONArray? {
        try {
            val jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
            return JSONArray(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    private fun generateHeatMapData() {

        val requestQueue = Volley.newRequestQueue(this@HeatMapActivity)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET,"https://vaibhavmehra1119.github.io/RoveAPI/jipi8-scuu8.json",null,Response.Listener {response ->
            try {
                val data = ArrayList<WeightedLatLng>()

                for (i in 0 until response.length()) {
                    // parse each json object
                    val entry = response.getJSONObject(i)
                    val lat = entry.getString("Latitude")
                    val lon = entry.getString("Longitude")
                    val weightedLatLng = WeightedLatLng(LatLng(lat.toDouble(), lon.toDouble()))
                    data.add(weightedLatLng)
                }
                if(!data.isEmpty()) {
                    val heatMapProvider = HeatmapTileProvider.Builder()
                        .weightedData(data).radius(50) // load our weighted data
                        // optional, in pixels, can be anything between 20 and 50
                        .build()
                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        },
            Response.ErrorListener {error ->

            })
        requestQueue.add(jsonArrayRequest)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap=googleMap
        generateHeatMapData()
        val indiaLatLng = LatLng(26.8467, 80.9462)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, 12f))
        var marker= googleMap.addMarker(MarkerOptions().position(indiaLatLng))

        googleMap.setOnMapClickListener { latLng ->
            if(marker!=null){
                marker!!.remove()
            }
            //marker?.remove()
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            marker=googleMap.addMarker(markerOptions)
        }
        getLastLocation()
    }
}
