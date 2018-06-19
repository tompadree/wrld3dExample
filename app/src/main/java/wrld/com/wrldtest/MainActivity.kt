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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnRoutingQueryCompletedListener  {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EegeoApi.init(this, getString(R.string.eegeo_api_key))
        setContentView(R.layout.activity_main)
        m_mapView!!.onCreate(savedInstanceState)

        val listener = this

        m_mapView!!.getMapAsync { map ->
            m_eegeoMap = map

            val uiContainer = findViewById<View>(R.id.eegeo_ui_container) as RelativeLayout
            m_indoorMapView = IndoorMapView(m_mapView!!, uiContainer, m_eegeoMap)

            val routingService = map.createRoutingService()

            routingService.findRoutes(RoutingQueryOptions()
                    .addIndoorWaypoint(LatLng(56.461231653264029, -2.983122836389253), 2)
                    .addIndoorWaypoint(LatLng(56.4600344, -2.9783117), 2)
                    .onRoutingQueryCompletedListener(listener))
        }
    }

    override fun onRoutingQueryCompleted(query: RoutingQuery, response: RoutingQueryResponse) {
        Toast.makeText(this, "Found routes", Toast.LENGTH_LONG).show()

        for (route in response.results) {
            val options = RouteViewOptions()
                    .color(Color.argb(128, 255, 0, 0))
                    .width(8.0f)
            val routeView = RouteView(m_eegeoMap, route, options)
            m_routeViews.add(routeView)

            for (section in route.sections) {
                for (step in section.steps) {
                    val markerOptions = MarkerOptions().position(step.path[0])
                    if (step.isIndoors) {
                        markerOptions.indoor(step.indoorId, step.indoorFloorId)
                    }
                    val marker = m_eegeoMap!!.addMarker(markerOptions)
                    m_markers.add(marker)
                }
            }

        }
    }

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
}
