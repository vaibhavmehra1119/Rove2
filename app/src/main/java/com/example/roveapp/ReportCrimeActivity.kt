package com.example.roveapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.roveapp.databinding.ActivityReportCrimeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*


class ReportCrimeActivity : AppCompatActivity(), OnMapReadyCallback, PermissionListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityReportCrimeBinding
    private lateinit var btn: Button
    private lateinit var et : EditText
    private lateinit var rg: RadioGroup
    //private lateinit var rg: RadioButton
    private lateinit var select_image: Button
    private lateinit var upload_image:Button
    private lateinit var filepath: Uri
    private lateinit var img: ImageView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    //private lateinit var crimeType:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportCrimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btn= binding.button2
        et = binding.editText
        autoCompleteTextView=binding.autoText
        rg=binding.rg
        select_image=binding.selectImageButton
        upload_image=binding.uploadImageButton
        img = binding.image
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var crimeType="57456"
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 -> crimeType= p0?.getItemAtPosition(p2).toString() }
        btn.setOnClickListener{
            val desc = et.text.toString()
            val radioId= rg.checkedRadioButtonId
            val radioButton:RadioButton=findViewById(radioId)
            val level:String=radioButton.text.toString()
            et.text.clear()
            rg.clearCheck()

            saveFireStore("26.654","80.945", crimeType, level,desc)
            System.out.println(crimeType)
            System.out.println(level)
        }
        //var autotextView = findViewById<AutoCompleteTextView>(R.id.autoText)
        val languages = resources.getStringArray(R.array.Languages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        autoCompleteTextView.setAdapter(adapter)
        select_image.setOnClickListener {
            intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*")
            startActivityForResult(Intent.createChooser(intent,"select picture"),1);

        }
        upload_image.setOnClickListener {

        }

    }

    private fun saveFireStore( lat: String,  lg:String, et:String, lvl:String,desc:String){
        val db = FirebaseFirestore.getInstance()
        // Create a new user with a first and last name
        val Reports:HashMap<String,String> = hashMapOf(
            "Event-Type" to et,
            "Level" to lvl,
            "Latitude" to lat,
            "Longitude" to lg,
            "Description" to desc
        )

// Add a new document with a generated ID
        db.collection("reports")
            .add(Reports)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this,"Successfully Saved",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                System.out.println(e)
            }
        
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                img.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
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
    //Dexter Permission
    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

        val intent: Intent= Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"Please Select Image"),1)
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        //TODO("Not yet implemented")
        Toast.makeText(applicationContext,"Denied",Toast.LENGTH_LONG).show()
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
        //TODO("Not yet implemented")
        p1?.continuePermissionRequest()
    }
}