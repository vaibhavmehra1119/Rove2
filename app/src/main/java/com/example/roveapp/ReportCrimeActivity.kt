package com.example.roveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.roveapp.databinding.ActivityReportCrimeBinding
import com.google.firebase.firestore.FirebaseFirestore

class ReportCrimeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityReportCrimeBinding
    private lateinit var btn: Button
    private lateinit var et : EditText
    private lateinit var rb: RadioGroup
    private lateinit var rg: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportCrimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btn= binding.button2
        et = binding.editText
        rb = binding.rg
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btn.setOnClickListener{
            val rep = et.text.toString()
            et.text.clear()
            rb.clearCheck()
            saveFireStore("26.654","80.945", rep, "L2")
        }
    }

    private fun saveFireStore( lat: String,  lg:String, et:String, lvl:String){
        val db = FirebaseFirestore.getInstance()
        val reports : MutableMap<String, Any> = HashMap<String, Any>()
        reports["Event Type"] = et
        reports["Level"] = lvl
        reports["Latitude"] = lat
        reports["Longitude"] = lg

        db.collection("reports").add(reports).addOnSuccessListener {
            println("Report adeed sucessfully")
        }.addOnFailureListener{
            println("ERROR!!!!!")
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val lucknow = LatLng(26.8467, 80.9462)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lucknow,15f))
        googleMap.setOnMapClickListener { latLng -> // Creating a marker
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude.toString())
            googleMap.clear()
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.addMarker(markerOptions)
        }
    }
}