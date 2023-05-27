package com.example.roveapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.roveapp.databinding.ActivityReportCrimeBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageRegistrar
import java.util.*


class ReportCrimeActivity : AppCompatActivity(), OnMapReadyCallback, PermissionListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityReportCrimeBinding
    private lateinit var btn: Button
    private lateinit var et : EditText
    private lateinit var rg: RadioGroup
    private lateinit var select_image: Button
    private lateinit var upload_image:Button
    private lateinit var uri: Uri
    private lateinit var img: ImageView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var latitude:String
    private lateinit var longitude:String
    private lateinit var progressDialog: ProgressDialog

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
        latitude=""
        longitude=""
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 -> crimeType= p0?.getItemAtPosition(p2).toString() }
        btn.setOnClickListener{
            val desc = et.text.toString()
            val radioId= rg.checkedRadioButtonId
            val radioButton:RadioButton=findViewById(radioId)
            val level:String=radioButton.text.toString()
            et.text.clear()
            rg.clearCheck()
            if(latitude.equals("")|| longitude.equals("")){
                latitude="26.3045309360496"
                longitude="80.3495394989439"
            }
            saveFireStore(latitude,longitude, crimeType, level,desc)

        }
        val languages = resources.getStringArray(R.array.Languages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        autoCompleteTextView.setAdapter(adapter)
        select_image.setOnClickListener {
            intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*")
            startActivityForResult(Intent.createChooser(intent,"select picture"),1);

        }
        upload_image.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val storage=FirebaseStorage.getInstance()
            val date = Date()
            val fileName : String = date.toString()
            val storageReference: StorageReference=storage.getReference("image/"+fileName)
            progressDialog=ProgressDialog(this)
            progressDialog.setMessage("Uploading")
            progressDialog.show()
            storageReference.putFile(uri).addOnSuccessListener {
                img.setImageBitmap(null)
                progressDialog.dismiss()
                Toast.makeText(this,"Image Uploaded",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                img.setImageBitmap(null)
                progressDialog.dismiss()
                Toast.makeText(this,"Upload Failed",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun saveFireStore( lat: String,  lg:String, et:String, lvl:String,desc:String){
        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("Submitting")
        progressDialog.show()
        val db = FirebaseFirestore.getInstance()
        val Reports:HashMap<String,String> = hashMapOf(
            "Event-Type" to et,
            "Level" to lvl,
            "Latitude" to lat,
            "Longitude" to lg,
            "Description" to desc
        )

        db.collection("reports")
            .add(Reports)
            .addOnSuccessListener { documentReference ->
                progressDialog.dismiss()
                Toast.makeText(this,"Successfully Saved",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
                System.out.println(e)
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            uri = data.data!!
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
            latitude=latLng.latitude.toString()
            longitude=latLng.longitude.toString()
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