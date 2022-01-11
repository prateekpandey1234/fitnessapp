package com.androiddevs.runningappyt.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.runningappyt.R
import com.androiddevs.runningappyt.adapters.RunAdapter
import com.androiddevs.runningappyt.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.androiddevs.runningappyt.other.SortType
import com.androiddevs.runningappyt.other.TrackingUtility
import com.androiddevs.runningappyt.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

    private lateinit var runAdapter: RunAdapter
    private val viewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requestPermissions()
        setupRecyclerView()
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        when(viewModel.sortType) {
            SortType.DATE -> spFilter.setSelection(0)//0,1,2,3 here are the values from string resources values
            SortType.RUNNING_TIME -> spFilter.setSelection(1)
            SortType.DISTANCE -> spFilter.setSelection(2)
            SortType.AVG_SPEED -> spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> spFilter.setSelection(4)
        }
//here below we react to the changes made in SortType
        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }
        }
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }
    private fun setupRecyclerView() = rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
    //fore requesting the permissions

     val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var arrperms = mutableListOf<String>()
        if(!TrackingUtility.hasLocationPermissions(requireContext())){
                if(!TrackingUtility.hasLocationPermissions(requireContext())){
                    onPermissionsDenied(0, mutableListOf
                        (Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }


}