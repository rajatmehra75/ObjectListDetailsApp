package com.rajat.objectlistdetailsapp.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rajat.objectlistdetailsapp.model.ItemModel

@Database(entities = arrayOf(ItemModel::class),version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun itemDao() : ItemDao
}