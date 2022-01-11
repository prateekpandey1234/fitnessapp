package com.androiddevs.runningappyt.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.androiddevs.runningappyt.db.RunningDatabase
import com.androiddevs.runningappyt.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.androiddevs.runningappyt.other.Constants.KEY_NAME
import com.androiddevs.runningappyt.other.Constants.KEY_WEIGHT
import com.androiddevs.runningappyt.other.Constants.RUNNING_DATABASE_NAME
import com.androiddevs.runningappyt.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//basically we create a object for our dragger hilt whose work is to give dependency
//to th object we call and the lifecycle for the dependency which are dependent on the component
//example->SingletonComponent means that dependency injected will remain throughout the whole LifeCycle of
//app
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //following injection is for our running database
    @Singleton//we use singleton because if we use inject several times multiple instances will be created for the same class which is not preferred
    @Provides//use it to tell dragger to provide the results of function to get dependency for our object creation
    fun provideRunningDatabase(
        @ApplicationContext app: Context//here it is a module file hence it doesn't know the context of our app
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()
    //following injection is for our dao object
    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
//    Retrieve and hold the contents of the preferences file 'name', returning a SharedPreferences through which
//    you can retrieve and modify its values. Only one instance of the SharedPreferences object is returned to any callers for
//    the same name, meaning they will see each other's edits as soon as they are made.
//This method is thread-safe.

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

}