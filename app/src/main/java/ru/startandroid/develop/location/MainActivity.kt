package ru.startandroid.develop.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    var tvEnabledGPS: TextView? = null
    var tvStatusGPS: TextView? = null
    var tvLocationGPS: TextView? = null
    var tvEnabledNet: TextView? = null
    var tvStatusNet: TextView? = null
    var tvLocationNet: TextView? = null
    private var locationManager: LocationManager? = null
    var sbGPS = StringBuilder()
    var sbNet = StringBuilder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvEnabledGPS = findViewById<View>(R.id.tvEnabledGPS) as TextView
        tvStatusGPS = findViewById<View>(R.id.tvStatusGPS) as TextView
        tvLocationGPS = findViewById<View>(R.id.tvLocationGPS) as TextView
        tvEnabledNet = findViewById<View>(R.id.tvEnabledNet) as TextView
        tvStatusNet = findViewById<View>(R.id.tvStatusNet) as TextView
        tvLocationNet = findViewById<View>(R.id.tvLocationNet) as TextView
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, (
                1000 * 10).toLong(), 10f, locationListener)
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, (1000 * 10).toLong(), 10f,
            locationListener)
        checkEnabled()
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener)
    }

    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            showLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            checkEnabled()
        }

        override fun onProviderEnabled(provider: String) {
            checkEnabled()
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
            showLocation(locationManager!!.getLastKnownLocation(provider))
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            if (provider == LocationManager.GPS_PROVIDER) {
                tvStatusGPS!!.text = "Status: $status"
            } else if (provider == LocationManager.NETWORK_PROVIDER) {
                tvStatusNet!!.text = "Status: $status"
            }
        }
    }

    private fun showLocation(location: Location?) {
        if (location == null) return
        if (location.provider == LocationManager.GPS_PROVIDER) {
            tvLocationGPS!!.text = formatLocation(location)
        } else if (location.provider ==
            LocationManager.NETWORK_PROVIDER
        ) {
            tvLocationNet!!.text = formatLocation(location)
        }
    }

    private fun formatLocation(location: Location?): String {
        return if (location == null) "" else String.format(
            "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3\$tF %3\$tT",
            location.latitude, location.longitude, Date(
                location.time))
    }

    private fun checkEnabled() {
        tvEnabledGPS!!.text = ("Enabled: "
                + locationManager
            ?.isProviderEnabled(LocationManager.GPS_PROVIDER))
        tvEnabledNet!!.text = ("Enabled: "
                + locationManager
            ?.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    fun onClickLocationSettings(view: View?) {
        startActivity(Intent(
            Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}
