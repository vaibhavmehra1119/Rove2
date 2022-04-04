package com.example.roveapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.roveapp.databinding.ActivityReportCrimeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File


class ReportCrimeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityReportCrimeBinding
    private lateinit var btn: Button
    private lateinit var et : EditText
    private lateinit var rb: RadioGroup
    private lateinit var rg: RadioButton
    private lateinit var select_image: Button
    private lateinit var upload_image:Button
    private var SELECT_IMAGE = 1
    private lateinit var textFile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportCrimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btn= binding.button2
        et = binding.editText
        rb = binding.rg
        select_image=binding.selectImageButton
        upload_image=binding.uploadImageButton
        textFile=binding.textView
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btn.setOnClickListener{
            val rep = et.text.toString()
            et.text.clear()
            rb.clearCheck()
            saveFireStore("26.654","80.945", rep, "L2")
        }
        var autotextView = findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        val languages = resources.getStringArray(R.array.Languages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        autotextView.setAdapter(adapter)
        select_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_IMAGE)
        }
        upload_image.setOnClickListener {
            val path = textFile.text.toString()
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val file = File(path)

            val mime = MimeTypeMap.getSingleton()
            val ext = file.name.substring(file.name.indexOf(".") + 1)
            val type = mime.getMimeTypeFromExtension(ext)

            intent.setDataAndType(Uri.fromFile(file),type)

            startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_IMAGE -> if (resultCode === RESULT_OK) {
                val FilePath: String = data?.data?.path.toString()
                textFile.text = FilePath
            }
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