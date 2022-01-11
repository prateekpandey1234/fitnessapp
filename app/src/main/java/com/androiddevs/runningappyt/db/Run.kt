package com.androiddevs.runningappyt.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    var img:Bitmap?=null,
    var timestamp: Long = 0L,//the time at which run started...prefer using millisecond method as it will help in sorting the databases
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,//total the time of run
    var caloriesBurned: Int = 0
){
    @PrimaryKey(autoGenerate = true)//we add primary key here cuz we don't want to provide id everytime we create Run entity
    var id: Int? = null
}
