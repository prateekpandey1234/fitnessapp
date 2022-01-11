package com.androiddevs.runningappyt.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.runningappyt.R
import com.androiddevs.runningappyt.db.RunDAO
import com.androiddevs.runningappyt.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//we added the method here as it allows us to reach the trackgnfragment even the app is closed
        navigateToTrackingFragmentIfNeeded(intent)
        setSupportActionBar(toolbar)//
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        //here is the thing , in our app we don't want to see bottom nav bar at some frags ,
        //so we use ondestinationchangelistener which is triggered when we change from fragment to another one

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment ->
                        bottomNavigationView.visibility = View.VISIBLE
                    else -> bottomNavigationView.visibility = View.GONE
                }
            }
    }
//we added the method here as it allows us to reach the trackgnfragment even the app is open
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }
//here we check if the action of that intent is from the notification
    //we also need to add his following method in both onCreate() and OnNewIntent() or else it will not run properly
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}
//so basically dependency is not we add in our gradle file ..... there is another concept of dependency...
//data person(
//     val name:String,
//     val age:Int,
//     val city:String
// ){}
//the above class is dependent on the input parameters hence therefore they are dependency of the class
//the Main advantage of dragger is that it helps to remove boilerplate codes in our files when we try to use dependency for a class our object
//ex::-->
//class MainActivity : AppCompatActivity() {
//    lateinit var RoomDatabase
//    lateinit var Repository
//    lateinit var ViewModel
//    lateinit var ViewModelFactory
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        RoomDatabase(Repository)
//        ViewModelFactory(RoomDatabase)
//        ViewModel(ViewModelFactory)
//    }
//}
//here above we need to init everytime the dependency we need to create our ViewModel....
//RoomDatabase need Repository,ViewModelFactory needs RoomDatabase and so on....which increases boiler plate code
//dragger is a dependency injection
//here we can use dragger::-->
//class MainActivity : AppCompatActivity() {
//    @Inject
//    lateinit var ViewModelFactory
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        ViewModel(ViewModelFactory)
//    }
//}
//here we tell the dragger that we need dependency for our ViewModel object and the dragger will do that by itself and simply provide
//use the dependency we required