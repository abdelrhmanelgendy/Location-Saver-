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
interface HistoryDao {


    @Insert
    suspend fun inserHistory(historySearchedLocations: HistorySearchedLocations)
    @Query("select * from searchhistorytable order by historyId ASC")
    suspend fun getHistory(): List<HistorySearchedLocations>


}