package wrld.com.wrldtest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.eegeo.mapapi.EegeoApi
import com.eegeo.mapapi.EegeoMap
import com.eegeo.mapapi.markers.Marker
import kotlinx.android.synthetic.main.activity_golden_gate.*
import com.eegeo.mapapi.geometry.LatLng
import com.eegeo.mapapi.markers.MarkerOptions




class GoldenGateActivity : AppCompatActivity() {

    private var m_eegeoMap_gg: EegeoMap? = null
    private val m_markers = ArrayList<Marker>()
    lateinit var m_marker : Marker
    private var m_locationToggle = false
    private val m_timerHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        EegeoApi.init(this, getString(R.string.eegeo_api_key))
        setContentView(R.layout.activity_golden_gate)
        m_mapView_gg!!.onCreate(savedInstanceState)
        golden_gate_button_back.isEnabled = true


        m_mapView_gg!!.getMapAsync { map ->

            m_eegeoMap_gg = map

            val locationA = LatLng(37.816670, -122.478190)
            val locationB = LatLng(37.822041, -122.478835)

            m_marker = m_eegeoMap_gg!!.addMarker(MarkerOptions()
                    .position(locationA).labelText("This is a moving marker"))

            m_timerHandler.postDelayed(object : Runnable {
                override fun run() {
                    if (m_eegeoMap_gg != null) {
                        m_locationToggle = !m_locationToggle
                        val newLocation = if (m_locationToggle) locationB else locationA
                        m_marker.setPosition(newLocation)
                        m_timerHandler.postDelayed(this, 2000)
                    }
                }
            }, 2000)


        }
    }

    override fun onResume() {
        super.onResume()
        m_mapView_gg?.onResume()
    }

    override fun onPause() {
        super.onPause()
        m_mapView_gg?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (m_eegeoMap_gg != null) {
//            for (routeView in m_routeViews) {
//                routeView.removeFromMap()
//            }
            for (marker in m_markers) {
                m_eegeoMap_gg!!.removeMarker(marker)
            }
        }

        /*TODO CRASH*/
       // m_mapView_gg?.onDestroy()
        //unregisterReceiver(m_mapView_gg)
    }

    fun onGoldenGateBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
