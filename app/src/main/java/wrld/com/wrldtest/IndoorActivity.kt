package wrld.com.wrldtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.eegeo.indoors.IndoorMapView
import com.eegeo.mapapi.EegeoApi
import com.eegeo.mapapi.EegeoMap
import com.eegeo.mapapi.indoors.IndoorMap
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener
import com.eegeo.mapapi.indoors.OnIndoorExitedListener
import com.eegeo.mapapi.markers.Marker
import com.eegeo.mapapi.widgets.RouteView
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import kotlinx.android.synthetic.main.activity_indoor.*

class IndoorActivity : AppCompatActivity() {


    private var m_indoorMapView: IndoorMapView? = null
    private var m_eegeoMap: EegeoMap? = null
    private val m_routeViews = ArrayList<RouteView>()
    private val m_markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EegeoApi.init(this, getString(R.string.eegeo_api_key))

        setContentView(R.layout.activity_indoor)
        move_floor_indoor_mapview!!.onCreate(savedInstanceState)

        val listener = this

        move_floor_indoor_mapview!!.getMapAsync { map ->


            m_eegeoMap = map

            val buttons = ArrayList<Button>()

            buttons.add(findViewById(R.id.topfloor_indoor_button))
            buttons.add(findViewById(R.id.moveup_indoor_button));
            buttons.add(findViewById(R.id.movedown_indoor_button));
            buttons.add(findViewById(R.id.bottomfloor_indoor_button));
            buttons.add(findViewById(R.id.move_floor_exit_indoor_button));

            val indoorEventListener = IndoorEventListener(buttons);
            map.addOnIndoorEnteredListener(indoorEventListener)
            map.addOnIndoorExitedListener(indoorEventListener)

        }
    }

    override fun onResume() {
        super.onResume()
        move_floor_indoor_mapview.onResume()
    }

    override fun onPause() {
        super.onPause()
        move_floor_indoor_mapview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        move_floor_indoor_mapview.onDestroy()
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

        private var m_buttons: List<Button> = ArrayList()


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
}
