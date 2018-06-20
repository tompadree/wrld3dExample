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
import android.content.Intent
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
import android.support.v4.graphics.ColorUtils
import android.widget.Button
import com.eegeo.mapapi.positioner.PositionerOptions
import kotlinx.android.synthetic.main.activity_main.*
import com.eegeo.mapapi.geometry.ElevationMode.HeightAboveGround
import com.eegeo.mapapi.geometry.LatLngAlt
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener
import com.eegeo.mapapi.indoors.OnIndoorExitedListener
import com.eegeo.mapapi.polygons.Polygon
import com.eegeo.mapapi.polygons.PolygonOptions
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
//    private val m_routeViews = ArrayList<RouteView>()
    lateinit var m_marker: Marker
    val markerOptions = MarkerOptions()
    private var m_indoors = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EegeoApi.init(this, getString(R.string.eegeo_api_key))
        setContentView(R.layout.activity_main)
        m_mapView?.onCreate(savedInstanceState)

        m_mapView?.getMapAsync { map ->

            m_eegeoMap = map
//            m_eegeoMap!!.addOnIndoorEnteredListener(this)
//            m_eegeoMap!!.addOnIndoorExitedListener(this)

            val buttons = ArrayList<Button>()

            buttons.add(topfloor_indoor_button)
            buttons.add(moveup_indoor_button)
            buttons.add(movedown_indoor_button)
            buttons.add(bottomfloor_indoor_button)
            buttons.add(move_floor_exit_indoor_button)

            golden_gate_button.isEnabled = true

            val indoorEventListener = IndoorEventListener(buttons, topfloor_indoor_buttons, golden_gate_button);
            map.addOnIndoorEnteredListener(indoorEventListener)
            map.addOnIndoorExitedListener(indoorEventListener)

            val uiContainer = findViewById<View>(R.id.eegeo_ui_container) as RelativeLayout
            m_indoorMapView = IndoorMapView(m_mapView!!, uiContainer, m_eegeoMap)

            m_marker = m_eegeoMap!!.addMarker(MarkerOptions()
                    .position(LatLng(37.769698, -122.466866))
                    .indoor("california_academy_of_sciences_19794", 0)
                    .labelText("Academy cafe marker"))



//                    val polygon : Polygon = m_eegeoMap!!.addPolygon(PolygonOptions()
//                            .add(
//                                     LatLng(37.769698, -122.466866),
//                                     LatLng(37.769658, -122.466806),
//                                    LatLng(37.769628, -122.466856),
//                                    LatLng(37.769618, -122.466786))
//                            .fillColor(ColorUtils.setAlphaComponent(Color.BLUE, 128))
//                            .indoor("california_academy_of_sciences_19794", 0));
                   // m_polygons.add(polygon);

            // m_markers.add(marker)

        }
    }

//    fun onClick(View view) {
//        if (m_indoors) {
//            m_eegeoMap.exitIndoorMap();
//        }
//    }

    override fun onResume() {
        super.onResume()

        m_mapView?.onResume()

    }

    override fun onPause() {
        super.onPause()
        m_mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (m_eegeoMap != null) {
//            for (routeView in m_routeViews) {
//                routeView.removeFromMap()
//            }
//            for (marker in m_markers) {
            m_eegeoMap?.removeMarker(m_marker)
            //m_marker.destroy()

//            }
        }

        m_mapView?.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }

    fun onGoldenGate(view : View) {
        startActivity(Intent(this, GoldenGateActivity::class.java))
      //  finish()
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

    class IndoorEventListener(buttons: List<Button>, indoor_buttons: RelativeLayout, golden_gate_button : Button) : OnIndoorEnteredListener, OnIndoorExitedListener {

        private var m_buttons: List<Button> = buttons // ArrayList()
        private var topfloor_indoor_buttons: RelativeLayout = indoor_buttons
        private var golden_gate_bttn : Button = golden_gate_button

        override fun onIndoorEntered() {
            setButtonStates(true)
            topfloor_indoor_buttons.visibility = View.VISIBLE
            golden_gate_bttn.visibility = View.GONE
            golden_gate_bttn.isEnabled = false
        }

        override fun onIndoorExited() {
            setButtonStates(false)
            topfloor_indoor_buttons.visibility = View.GONE
            golden_gate_bttn.visibility = View.VISIBLE
            golden_gate_bttn.isEnabled = true
        }

        fun setButtonStates(state: Boolean) {
            val i: Iterator<Button> = m_buttons.listIterator()

            while (i.hasNext()) {
                val b: Button = i.next()
                b.isEnabled = state
            }
        }
    }

}
