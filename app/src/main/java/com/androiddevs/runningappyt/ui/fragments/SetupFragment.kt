package com.androiddevs.runningappyt.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.androiddevs.runningappyt.R
import com.androiddevs.runningappyt.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.androiddevs.runningappyt.other.Constants.KEY_NAME
import com.androiddevs.runningappyt.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {
    @Inject
    lateinit var sharedPref: SharedPreferences
    //the following injection alows us to know that if we opened the for the first time
    @set:Inject//we use  @set only for primitive data type
    var isFirstAppOpen = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//use the following code when we open the application after 1st time
        if(!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if(success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun writePersonalDataToSharedPref(): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }

}