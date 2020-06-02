package com.rajat.objectlistdetailsapp.roomdb

import android.content.Context
import androidx.room.Room
import com.rajat.objectlistdetailsapp.R

object AppDataBaseHelper {
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase::class.java, context.getString(R.string.db_name)).allowMainThreadQueries().fallbackToDestructiveMigration().build()
        }
        return instance as AppDatabase
    }
}