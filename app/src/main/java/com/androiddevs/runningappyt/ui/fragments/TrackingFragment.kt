package com.androiddevs.runningappyt.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevs.runningappyt.R
import com.androiddevs.runningappyt.db.Run
import com.androiddevs.runningappyt.other.Constants.ACTION_PAUSE_SERVICE
import com.androiddevs.runningappyt.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.androiddevs.runningappyt.other.Constants.ACTION_STOP_SERVICE
import com.androiddevs.runningappyt.other.Constants.MAP_ZOOM
import com.androiddevs.runningappyt.other.Constants.POLYLINE_COLOR
import com.androiddevs.runningappyt.other.Constants.POLYLINE_WIDTH
import com.androiddevs.runningappyt.other.TrackingUtility
import com.androiddevs.runningappyt.service.Polyline
import com.androiddevs.runningappyt.service.TrackingService
import com.androiddevs.runningappyt.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.lang.Math.round
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    private var isTracking = false

    private var curTimeInMillis = 0L

    @set:Inject
    var weight = 80f

    private var pathPoints = mutableListOf<Polyline>()

    private var menu: Menu? = null
//here we add all the polylines we created

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //in activity the following function is given by default
        setHasOptionsMenu(true)
        //Report that this fragment would like to participate in populating the options
        // menu by receiving a call to onCreateOptionsMenu and related methods.
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    private fun addAllPolylines() {
        for(polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }
//so basically we want to connect the last two polylines or coordinates with a lines whenever a new polyline/location is added
    private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }
    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            tvTimer.text = formattedTime
        })
    }
    //toggling the run when we press the buttons in our ui
    private fun toggleRun() {
        if(isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }//used to change the visibility of the menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(curTimeInMillis > 0L) {//if we ran for some time
            this.menu?.getItem(0)?.isVisible = true
        }
    }
    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    //the function here reactes to any update in our service and change anything in our fragment
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }
    }
    //the function below allows to move camera when the location chnages
    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }
    //method to get the whole image of the run of user
    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
    //Creates a new bounds based on a southwest and a northeast corner
    //The bounds conceptually includes all points where:
    //the latitude is in the range [northeast.latitude, southwest.latitude];
    //the longitude is in the range [southwest.longtitude, northeast.longitude] if southwest.longtitude ≤ northeast.longitude; and
    //the longitude is in the range [southwest.longitude, 180) ∪ [-180, northeast.longitude] if southwest.longtitude > northeast.longitude.
        for(polyline in pathPoints) {
            for(pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(//this method here below allows us to take screenshot of the whole track we ran
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()//here is the padding we give to the screenshot the function takes
            )
        )
    }
    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for(polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = ((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10).roundToInt() / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(bmp, dateTimestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        //down from here are the necessary functions to run the google map
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }
    }
    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
//Called to ask the fragment to save its current dynamic state, so it can later be
// reconstructed in a new instance if its process is restarted. If a new instance of the
// fragment later needs to be created, the data you place in the Bundle here will be available in the Bundle given to onCreate(Bundle)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}