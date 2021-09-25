package com.example.locationsaver.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchHistoryTable" )
data class HistorySearchedLocations(
    val searchEntered: String,
    val searchResult: String,
    val logitude: String,
    val latitude: String
) {
    @PrimaryKey(autoGenerate = true)
    var historyId: Int? = null
}