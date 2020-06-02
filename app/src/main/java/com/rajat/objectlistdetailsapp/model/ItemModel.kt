package com.rajat.objectlistdetailsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ItemModel(
    @PrimaryKey
    val id: String,
    val name: String,
    val details: String,
    var isFavorite: Boolean = false
) : Serializable {
    override fun toString(): String = "$name /n$details"
}