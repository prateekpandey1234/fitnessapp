package com.androiddevs.runningappyt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

@HiltAndroidApp
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}
// so in reality the dependency are injected during the compile time so there is nothing else t do here
//the java(generated) module is really the hilt class created

//we also need to name this application file as main application file by going in the manifests and
//adding-->android:name=".BaseApplication"