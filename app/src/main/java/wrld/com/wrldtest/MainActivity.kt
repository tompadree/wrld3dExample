package wrld.com.wrldtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eegeo.mapapi.EegeoMap
import com.eegeo.mapapi.map.OnMapReadyCallback
import com.eegeo.mapapi.MapView
import com.eegeo.mapapi.EegeoApi
import com.eegeo.mapapi.geometry.LatLng
import com.eegeo.mapapi.markers.Marker

import com.eegeo.mapapi.markers.MarkerOptions
import com.eegeo.mapapi.widgets.RouteView
import com.eegeo.indoors.IndoorMapView
import android.R.attr.path
import android.view.View
import com.eegeo.mapapi.services.routing.RouteStep
import com.eegeo.mapapi.services.routing.RouteSection
import com.eegeo.mapapi.widgets.RouteViewOptions
import com.eegeo.mapapi.services.routing.Route
import com.eegeo.mapapi.services.routing.RoutingQueryResponse
import com.eegeo.mapapi.services.routing.RoutingQuery
import com.eegeo.mapapi.services.routing.RoutingQueryOptions
import com.eegeo.mapapi.services.routing.RoutingService
import android.widget.RelativeLayout
import com.eegeo.mapapi.services.routing.OnRoutingQueryCompletedListener
import android.graphics.Color;
import android.graphics.Point
import android.graphics.PointF
import android.widget.Button
import com.eegeo.mapapi.positioner.PositionerOptions
import kotlinx.android.synthetic.main.activity_main.*
import com.eegeo.mapapi.geometry.ElevationMode.HeightAboveGround
import com.eegeo.mapapi.geometry.LatLngAlt
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener
import com.eegeo.mapapi.indoors.OnIndoorExitedListener
import com.eegeo.mapapi.positioner.OnPositionerChangedListener
import com.eegeo.mapapi.positioner.Positioner
import com.eegeo.ui.util.ViewAnchor


class MainActivity : AppCompatActivity() {

    /*
    *
    * EegeoApi.init(this, "961c5e528fc42dcf39733ddd04a964ad")

        markerOptions.position(LatLng(37.769868, -122.466106))

        m_mapView.onCreate(savedInstanceState)


        m_eegeoMap?.addMarker(markerOptions)
    *
    * */

    private var m_indoorMapView: IndoorMapView? = null
    private var m_eegeoMap: EegeoMap? = null
    private val m_routeViews = ArrayList<RouteView>()
    private val m_markers = ArrayList<Marker>()
    val markerOptions = MarkerOptions()
    private var m_indoors = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EegeoApi.init(this, getString(R.string.eegeo_api_key))
        setContentView(R.layout.activity_main)
        m_mapView!!.onCreate(savedInstanceState)

        val listener = this

        m_mapView!!.getMapAsync { map ->


            m_eegeoMap = map
//            m_eegeoMap!!.addOnIndoorEnteredListener(this)
//            m_eegeoMap!!.addOnIndoorExitedListener(this)

            val buttons = ArrayList<Button>()

            buttons.add(findViewById(R.id.topfloor_indoor_button))
            buttons.add(findViewById(R.id.moveup_indoor_button));
            buttons.add(findViewById(R.id.movedown_indoor_button));
            buttons.add(findViewById(R.id.bottomfloor_indoor_button));
            buttons.add(findViewById(R.id.move_floor_exit_indoor_button));

            val indoorEventListener = IndoorEventListener(buttons);
            map.addOnIndoorEnteredListener(indoorEventListener)
            map.addOnIndoorExitedListener(indoorEventListener)

            val uiContainer = findViewById<View>(R.id.eegeo_ui_container) as RelativeLayout
            m_indoorMapView = IndoorMapView(m_mapView!!, uiContainer, m_eegeoMap)
            //markerOptions?.indoor(step.indoorId, step.indoorFloorId)
//            markerOptions.position(LatLng(37.769868, -122.466106))
////            markerOptions.indoor("Academy Cafe", 1)
//            val marker_m = m_eegeoMap!!.addMarker(markerOptions)
//            m_markers.add(marker_m)

            val numberOfFloors = 4
            for (floorId in 0 until numberOfFloors) {
                val marker = m_eegeoMap!!.addMarker(MarkerOptions()
                        .position(LatLng(37.769868, -122.466106))
                        .indoor("california_academy_of_sciences", floorId)
                        .labelText("Marker on floor $floorId"))

                m_markers.add(marker)
            }

//            m_eegeoMap.addMarker(markerOptions!!)


        }
    }

//    fun onClick(View view) {
//        if (m_indoors) {
//            m_eegeoMap.exitIndoorMap();
//        }
//    }

    override fun onResume() {
        super.onResume()
        m_mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        m_mapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (m_eegeoMap != null) {
            for (routeView in m_routeViews) {
                routeView.removeFromMap()
            }
            for (marker in m_markers) {
                m_eegeoMap!!.removeMarker(marker)
            }
        }

        m_mapView!!.onDestroy()
    }

    fun onExit(view: View) {
        m_eegeoMap!!.onExitIndoorClicked()
    }

    fun onTopFloor(view: View) {
        val indoorMap = m_eegeoMap!!.getActiveIndoorMap()
        m_eegeoMap!!.setIndoorFloor(indoorMap.floorCount - 1)
    }

    fun onBottomFloor(view: View) {
        m_eegeoMap!!.setIndoorFloor(0)
    }

    fun onMoveUp(view: View) {
        m_eegeoMap!!.moveIndoorUp()
    }

    fun onMoveDown(view: View) {
        m_eegeoMap!!.moveIndoorDown()
    }

    class IndoorEventListener(buttons: List<Button>) : OnIndoorEnteredListener, OnIndoorExitedListener {

        private var m_buttons: List<Button> = buttons // ArrayList()
//        m_buttons = buttons

        override fun onIndoorEntered() {
            setButtonStates(true)
        }

        override fun onIndoorExited() {
            setButtonStates(false)
        }


        fun setButtonStates(state: Boolean) {
            val i: Iterator<Button> = m_buttons.listIterator()

            while (i.hasNext()) {
                val b: Button = i.next()
                b.isEnabled = state
            }
        }
    }


//    inner class IndoorEventListener internal constructor(private val m_button: Button) : OnIndoorEnteredListener, OnIndoorExitedListener {
//
//        override fun onIndoorEntered() {
//            m_indoors = true
//            m_button.isEnabled = true
//        }
//
//        override fun onIndoorExited() {
//            m_indoors = false
//            m_button.isEnabled = false
//        }
//    }

}
