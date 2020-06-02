package com.rajat.objectlistdetailsapp.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rajat.objectlistdetailsapp.model.ItemModel

@Dao
interface ItemDao {
    @Insert
    fun insertAll(item: List<ItemModel>)

    @Insert
    fun insertItem(item: ItemModel)

    @Update
    fun updateItem(item: ItemModel)

    @Query("SELECT * FROM itemmodel")
    fun getAllData() : List<ItemModel>

    @Query("SELECT * FROM itemmodel where isFavorite=1")
    fun getFavoriteData() : List<ItemModel>

    @Query("SELECT count(name) FROM itemmodel")
    fun getCount() : Int

    @Query("delete FROM itemmodel")
    fun deleteAll() : Int
}