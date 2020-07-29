package com.binar.permissionapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.binar.permissionapp.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var PERMISSION_ID: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        val image = binding.ivImage
        val inputUrl = binding.etInput
        val btnLocation = binding.btnSendLocation
        val btnSend = binding.btnSend
        var urlImage = "https://3.bp.blogspot.com/-qDc5kIFIhb8/UoJEpGN9DmI/AAAAAAABl1s/BfP6FcBY1R8/s1600/BlueHead.jpg"
        setContentView(view)

        // remove action bar
        this.supportActionBar?.hide()

        // default image
        Glide.with(this).load(urlImage).circleCrop().into(image)

        // change image from internet
        btnSend.setOnClickListener {
            urlImage = inputUrl.text.toString()
            Glide.with(this).load(urlImage).circleCrop().into(image)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // get user location
        btnLocation.setOnClickListener {
            userLocation()
        }

    }

    private fun userLocation(){
        // first a check permissions
        if(CheckPermission()){
            // now we check the location permissions is enabled
            if(isLocationEnabled()){
                // get location
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    var location: Location? = task.result
                    if (location == null ){

                    } else {
                        // location.latitude will return the latitude coordinate
                        // location.longtitude will return the longtitude coordinate
//                        textView.text="your location coordinates are : \n Lat: " + location.latitude+"; Long :" + location.longitude+
//                                "\n your city: "+getCityName(location.latitude,location.longitude) + " , your country: " +getCountryName(location.latitude,location.longitude)
                        Toast.makeText(this,"kordinat lokasi lat : ${location.latitude} , long : ${location.longitude}",Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText(this,"please enable your location service",Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }

    private fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        // opsional
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        // end opsional
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation= p0.lastLocation
            // set new name location
//            textView.text="your location coordinates are : \n Lat: " + lastLocation.latitude+"; Long :" + lastLocation.longitude
        }
    }

    // create fun to check the uses permissions
    private fun CheckPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    // fun allow to get user permission
    private fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

    // function to check if the location of the device is enable
    private fun isLocationEnabled():Boolean{
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // function to get city name
    private fun getCityName(lat:Double, long:Double):String{
        var cityName =""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress : MutableList<Address> = geoCoder.getFromLocation(lat,long,1)
        cityName = Adress.get(0).locality
        return cityName
    }

    // create function return the country name
    private fun getCountryName(lat:Double, long:Double):String{
        var countryName =""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress : MutableList<Address> = geoCoder.getFromLocation(lat,long,1)

        countryName = Adress.get(0).countryName

        return countryName
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // function to check the permissions result
        // use it just for debuging our code
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d("Debug","you have the permissions ")
        }
    }
}