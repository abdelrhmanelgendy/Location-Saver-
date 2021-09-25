package com.example.locationsaver.databases.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.*
import com.example.locationsaver.pojo.HistorySearchedLocations
import com.example.locationsaver.pojo.SavedLocation
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable


@Dao
interface LocationDao {
    @Transaction
    @Query("select * from ImageTable order by id Asc")
    suspend fun getAllLocations(): List<SavedLocation>

    @Insert
    suspend fun insertLocation(savedLocation: SavedLocation)

    @Delete
    suspend fun deleteLocation(savedLocation: SavedLocation)




}