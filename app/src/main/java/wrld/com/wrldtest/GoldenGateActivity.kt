package wrld.com.wrldtest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.eegeo.mapapi.EegeoMap
import com.eegeo.mapapi.markers.Marker
import kotlinx.android.synthetic.main.activity_golden_gate.*
import com.eegeo.mapapi.geometry.LatLng
import com.eegeo.mapapi.markers.MarkerOptions
import java.util.*


class GoldenGateActivity : AppCompatActivity() {

    private var m_eegeoMap_gg: EegeoMap? = null
    private val m_markers = ArrayList<Marker>()
    lateinit var m_marker: Marker
    private val m_timerHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        EegeoApi.init(this, getString(R.string.eegeo_api_key))
        setContentView(R.layout.activity_golden_gate)
        m_mapView_gg!!.onCreate(savedInstanceState)
        golden_gate_button_back.isEnabled = true

        m_mapView_gg!!.getMapAsync { map ->

            m_eegeoMap_gg = map

            val latlngList: ArrayList<LatLng> = ArrayList()
            latlngList.add(LatLng(37.810887, -122.477521))
            latlngList.add(LatLng(37.812262, -122.477696))
            latlngList.add(LatLng(37.813554, -122.477821))
            latlngList.add(LatLng(37.815101, -122.478004))
            latlngList.add(LatLng(37.817702, -122.478311))
            latlngList.add(LatLng(37.818984, -122.478454))
            latlngList.add(LatLng(37.819584, -122.478554))
            latlngList.add(LatLng(37.820184, -122.478604))
            latlngList.add(LatLng(37.821184, -122.478704))
            latlngList.add(LatLng(37.822095, -122.478842))
            latlngList.add(LatLng(37.823553, -122.479008))
            latlngList.add(LatLng(37.824487, -122.479118))
            latlngList.add(LatLng(37.825604, -122.479249))
            var k = 0

            m_marker = m_eegeoMap_gg!!.addMarker(MarkerOptions()
                    .position(latlngList.get(0)))

            m_timerHandler.postDelayed(
                    object : Runnable {
                        override fun run() {
                            if (m_eegeoMap_gg != null) {
                                m_marker.setPosition(latlngList.get(k))
                                k++
                                if(k>=12)
                                    k=0

                                m_timerHandler.postDelayed(this, 1500)
                            }
                        }
                    }, 1500)

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

            for (marker in m_markers) {
                m_eegeoMap_gg!!.removeMarker(marker)
            }
        }

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
