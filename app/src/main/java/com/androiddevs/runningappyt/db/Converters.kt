package com.androiddevs.runningappyt.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
//we use typeconverters because our database only works with primary datatypes not user-defined classes
//we should create 2 typeconverters from one type to other and vice versa
class Converters {

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter//usually we see image in form of bitmap but we can covert them as pieces of bytes which further can be stored inside a array which
    //is helpful to store them in the database
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()//Creates a new byte array output stream
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}