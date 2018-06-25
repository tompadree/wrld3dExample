package wrld.com.wrldtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.eegeo.mapapi.EegeoMap
import com.eegeo.mapapi.EegeoApi
import com.eegeo.mapapi.geometry.LatLng
import com.eegeo.mapapi.markers.Marker
import com.eegeo.mapapi.markers.MarkerOptions
import com.eegeo.indoors.IndoorMapView
import android.content.Intent
import android.view.View
import android.widget.RelativeLayout
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener
import com.eegeo.mapapi.indoors.OnIndoorExitedListener

class MainActivity : AppCompatActivity() {

    private var m_indoorMapView: IndoorMapView? = null
    private var m_eegeoMap: EegeoMap? = null
    lateinit var m_marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EegeoApi.init(this, getString(R.string.eegeo_api_key))
        setContentView(R.layout.activity_main)
        m_mapView?.onCreate(savedInstanceState)

        m_mapView?.getMapAsync { map ->

            m_eegeoMap = map

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

        }
    }

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

            m_eegeoMap?.removeMarker(m_marker)

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
