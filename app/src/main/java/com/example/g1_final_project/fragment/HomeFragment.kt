package com.example.g1_final_project.fragment

import android.Manifest
import android.location.LocationManager
import com.daimajia.androidanimations.library.YoYo.YoYoString
import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.fxn.stash.Stash
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.example.g1_final_project.fragment.HomeFragment
import com.example.g1_final_project.R
import com.example.g1_final_project.models.HistoryItemModel
import com.karumi.dexter.listener.PermissionDeniedResponse
import android.widget.Toast
import android.content.Intent
import com.karumi.dexter.PermissionToken
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Looper
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.common.api.ResolvableApiException
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.g1_final_project.databinding.FragmentHomeBinding
import com.example.g1_final_project.utils.Constants
import com.example.g1_final_project.utils.Controller
import com.google.android.gms.location.*
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.listener.PermissionRequest
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class HomeFragment() : Fragment() {
    private var b: FragmentHomeBinding? = null
    private var currentMileagesDouble = 0.0
    private val totalMileagesDouble = 0.0
    private var finalDistancee = 0.0
    private var finalDistancec = 0.0
    private val locationManager: LocationManager? = null
    private val locationListener: LocationListener? = null
    private val gpsAnimation: YoYoString? = null
    private val LOCATION_REQUEST_CODE = 10001
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private val locationRequested = true
    private var startLocation: Location? = null
    var isStarted = false
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        b = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = b!!.root
        if (!Stash.getBoolean(Constants.IS_LOGGED_IN, false)) {
            return b!!.root
        }
        progressDialog = ProgressDialog(requireContext())
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        b!!.txtWelcome.text = "HELLO, " + Constants.userModel().username + "!"
        b!!.dashDuration.text = Stash.getString(Constants.CURRENT_TIME, "0 hrs 0 mins")
        Constants.databaseReference().child((Constants.auth().uid)!!)
            .child(Constants.CURRENT_MILEAGES)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && isAdded) {
                        currentMileagesDouble = (snapshot
                            .getValue<Double>(Double::class.java)!!)
                        val value = currentMileagesDouble.toString()
                        b!!.dashKilometer.text = "$value km"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        b!!.btnStartJourney.setOnClickListener(View.OnClickListener {
            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        Log.d(TAG, "onPermissionGranted: isStarted: $isStarted")
                        if (isStarted) {
                            stopLocationUpdates()
                            isStarted = false
                            b!!.btnStartJourney.text = "START\nJOURNEY"
                            b!!.btnStartJourney.icon = resources.getDrawable(
                                R.drawable.ic_play_icon
                            )
                            Controller.stopAnimation()
                            //                                    Controller.stopStopWatch();
//                                    handler.removeCallbacks(runnable);
                            timer!!.cancel()
                            val model = HistoryItemModel()
                            model.title = "Journey on " + Date()
                            model.distance = finalDistancec.toString() + ""
                            model.time = Stash.getString(Constants.CURRENT_TIME)
                            Constants.databaseReference().child((Constants.auth().uid)!!)
                                .child(Constants.HISTORY)
                                .push()
                                .setValue(model)
                        } else {
//                                    Controller.startStopWatch(b.dashDuration);
//                                    handler.postDelayed(runnable, 60000);// RUN AT EVERY MINUTE
                            timer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    runTimer()
                                }
                            }, 60000, 60000)
                            progressDialog!!.show()
                            isStarted = true
                            lastLocation
                        }
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied) {
                            // open device settings when the permission is
                            // denied permanently
                            Toast.makeText(
                                requireContext(),
                                "You need to provide permission!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest,
                        permissionToken: PermissionToken
                    ) {
                        permissionToken.continuePermissionRequest()
                    }
                }).check()
        })
        //        handler = new Handler();
        timer = Timer()
        return root
    }

    private fun runTimer() {
        // THIS WILL RUN AFTER EVERY MINUTE
        if (lastMinutes == 60) {
            lastMinutes = 0
            Stash.put(Constants.MINUTES, 0)
            lastHours++
            Stash.put(Constants.HOURS, lastHours)
        } else {
            lastMinutes++
            Stash.put(Constants.MINUTES, lastMinutes)
        }

        /* new Handler().post(() -> {
            });*/requireActivity().runOnUiThread({ b!!.dashDuration.setText(lastHours.toString() + " hrs " + lastMinutes + " mins") })
        Stash.put(Constants.CURRENT_TIME, "$lastHours hrs $lastMinutes mins")
        Log.d(TAG, "run: triggerAt: $lastMinutes")
    }

    var lastMinutes = Stash.getInt(Constants.MINUTES, 0)
    var lastHours = Stash.getInt(Constants.HOURS, 0)
    var timer: Timer? = null

    /*TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

        }
    };*/
    /*Handler handler1;
    Runnable runnable1 = () -> {


        handler.postDelayed(runnable, 60000);// RUN AT EVERY MINUTE

    };
*/
    var locationCallback: LocationCallback? = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if ((locationResult == null) && (requireActivity() == null
                        ) && (locationResult.lastLocation == null)
            ) {
                return
            }
            val currentLocation = locationResult.lastLocation ?: return
            Log.d(TAG, "onLocationResult: currentLocation " + currentLocation.latitude)
            Log.d(TAG, "onLocationResult: currentLocation " + currentLocation.longitude)
            val distance = startLocation!!.distanceTo(currentLocation).toDouble()
            Log.d(TAG, "onLocationResult: distance: $distance")
            val distanceInMiles = distance / 1609
            Log.d(TAG, "onLocationResult: finalDistance $distanceInMiles")
            val currentLocationDistance = currentMileagesDouble + distanceInMiles
            if (currentLocationDistance < finalDistancee) {
                startLocation = currentLocation
                if (finalDistancec == 0.0) Toast.makeText(
                    requireActivity(),
                    "finalDistancec == 0",
                    Toast.LENGTH_SHORT
                ).show()
                currentMileagesDouble = finalDistancec
                finalDistancee = 0.0
                return
            }
            finalDistancee = 0.0
            finalDistancee = currentMileagesDouble + distanceInMiles
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            b!!.dashKilometer.text = df.format(finalDistancee)
            finalDistancec = df.format(finalDistancee).toDouble()

            /*databaseReference
                    .child("cars")
                    .child(currentCarKey)
                    .child("booking")
                    .child("currentMileages")
                    .setValue(finalDistancec);*/Constants.databaseReference()
                .child((Constants.auth().uid)!!)
                .child(Constants.CURRENT_MILEAGES)
                .setValue(finalDistancec)
            Log.d(TAG, "onLocationResult: textview " + b!!.dashKilometer.text.toString())
            Log.d(TAG, "--------------------------------------------------------------\n\n\n")
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        if ((fusedLocationProviderClient != null
                    && locationCallback != null)
        ) fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
    }//                Controller.stopStopWatch();

    //                handler.removeCallbacks(runnable);
//                    Controller.stopStopWatch();
//                    handler.removeCallbacks(runnable);
    //We have a location
    private val lastLocation: Unit
        private get() {
            val TAG = "TrackerFragment"
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            locationRequest = LocationRequest.create()
            locationRequest?.setInterval(4000)
            locationRequest?.setFastestInterval(2000)
            locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            val locationTask = fusedLocationProviderClient?.getLastLocation()
            locationTask?.addOnSuccessListener(object : OnSuccessListener<Location?> {
                override fun onSuccess(location: Location?) {
                    if (location != null) {
                        //We have a location
                        progressDialog!!.dismiss()
                        Log.d(TAG, "onSuccess: startLocation: " + location.latitude)
                        Log.d(TAG, "onSuccess: startLocation: " + location.longitude)
                        b!!.btnStartJourney.text = "STOP\nJOURNEY"
                        b!!.btnStartJourney.setIcon(
                            resources.getDrawable(
                                R.drawable.ic_baseline_stop_circle_24
                            )
                        )
                        startLocation = location
                        Controller.startAnimation(b!!.imgBike)
                        startLocationChecker()
                    } else {
                        progressDialog!!.dismiss()
                        isStarted = false
                        Controller.stopAnimation()
                        b!!.btnStartJourney.text = "START\nJOURNEY"
                        b!!.btnStartJourney.setIcon(
                            resources.getDrawable(
                                R.drawable.ic_play_icon
                            )
                        )
                        Toast.makeText(requireContext(), "Location is null", Toast.LENGTH_SHORT)
                            .show()
                        Log.d(TAG, "onSuccess: Location was null...")
                        //                    Controller.stopStopWatch();
//                    handler.removeCallbacks(runnable);
                        timer!!.cancel()
                    }
                }
            })
            locationTask?.addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    progressDialog!!.dismiss()
                    isStarted = false
                    Controller.stopAnimation()
                    b!!.btnStartJourney.text = "START\nJOURNEY"
                    b!!.btnStartJourney.setIcon(
                        resources.getDrawable(
                            R.drawable.ic_play_icon
                        )
                    )
                    Toast.makeText(requireContext(), "Location is null", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onSuccess: Location was null...")
                    Log.e(TAG, "onFailure: " + e.localizedMessage)
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    //                Controller.stopStopWatch();
//                handler.removeCallbacks(runnable);
                    timer!!.cancel()
                }
            })
        }

    private fun startLocationChecker() {
        checkSettingsAndStartLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        if (requireActivity() != null) stopLocationUpdates()
        //        Controller.stopStopWatch();
//        handler.removeCallbacks(runnable);
        timer!!.cancel()
    }

    private fun checkSettingsAndStartLocationUpdates() {
        val request = LocationSettingsRequest.Builder()
            .addLocationRequest((locationRequest)!!).build()
        val client = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener { //Settings of device are satisfied and we can start location updates
            startLocationUpdates()
        }
        locationSettingsResponseTask.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), 1001)
                } catch (ex: SendIntentException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    (locationListener)!!
                )
            }
        }
    }

    companion object {
        private val TAG = "HomeFragment"
    }
}