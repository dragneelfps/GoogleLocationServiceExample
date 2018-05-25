  package com.example.sourabh.googlelocationserviceexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_maps.*


class MapsUiSettingsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap : GoogleMap? = null
    private var mUiSettings: UiSettings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
            as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map_type, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(!isMapAvailable()){
            return super.onOptionsItemSelected(item)
        }
        val map = mMap!!
        when(item?.itemId){
            R.id.map_type_none -> map.mapType = GoogleMap.MAP_TYPE_NONE
            R.id.map_type_normal -> map.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.map_type_terrain -> map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            R.id.map_type_satellite -> map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.map_type_hybrid -> map.mapType = GoogleMap.MAP_TYPE_HYBRID
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setDefaultChecks(){
        if(isMapAvailable() && isUiSettingsAvailable()){
            zoomControl.isChecked = mUiSettings!!.isZoomControlsEnabled
            compassControl.isChecked = mUiSettings!!.isCompassEnabled
            myLocationBtnControl.isChecked = mUiSettings!!.isMyLocationButtonEnabled
            tiltControl.isChecked = mUiSettings!!.isTiltGesturesEnabled
            rotateControl.isChecked = mUiSettings!!.isRotateGesturesEnabled
            mapGestureControl.isChecked = true
            zoomGestureControl.isChecked = mUiSettings!!.isZoomGesturesEnabled
        }
    }

    private fun initListeners(){
        zoomControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.isZoomControlsEnabled = isChecked
            }
        }
        compassControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.isCompassEnabled = isChecked
            }
        }
        myLocationBtnControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.isMyLocationButtonEnabled = isChecked
                if(hasPermssion(this)) {
                    mMap!!.isMyLocationEnabled = isChecked
                }else{
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 123)
                }
            }
        }
        tiltControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.isTiltGesturesEnabled = isChecked
            }
        }
        rotateControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.isRotateGesturesEnabled = isChecked
            }
        }
        mapGestureControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.setAllGesturesEnabled(isChecked)
            }
        }
        zoomGestureControl.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isMapAvailable() && isUiSettingsAvailable()){
                mUiSettings!!.isZoomGesturesEnabled = isChecked
            }
        }
    }

    private fun removeListeners(){
        zoomControl.setOnCheckedChangeListener(null)
        compassControl.setOnCheckedChangeListener(null)
        myLocationBtnControl.setOnCheckedChangeListener(null)
        tiltControl.setOnCheckedChangeListener(null)
        rotateControl.setOnCheckedChangeListener(null)
        mapGestureControl.setOnCheckedChangeListener(null)
        zoomGestureControl.setOnCheckedChangeListener(null)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mUiSettings = map.uiSettings
        setDefaultChecks()
    }

    private fun isChecked(id: Int): Boolean {
        return findViewById<CheckBox>(id).isChecked
    }

    private fun isMapAvailable(): Boolean{
        mMap ?: return false
        return true
    }

    private fun isUiSettingsAvailable(): Boolean{
        mUiSettings ?: return false
        return true
    }

    override fun onResume() {
        super.onResume()
        initListeners()
    }

    override fun onStop() {
        super.onStop()
        removeListeners()
    }
}
