package com.example.locationsaver.di

import android.content.Context
import androidx.room.Room
import com.example.locationsaver.databases.local.LocationRoomBuilder
import com.example.locationsaver.databases.remot.ImageWebSearchAPI
import com.example.locationsaver.repository.Repository
import com.example.locationsaver.viewModel.DataBaseViewModel
import com.example.locationsaver.viewModel.RetrofitViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val module = org.koin.dsl.module {

    single {
        Retrofit.Builder()
            .baseUrl("https://bing-image-search1.p.rapidapi.com/images/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ImageWebSearchAPI::class.java)
    }
    single {

        Room.databaseBuilder(androidContext(), LocationRoomBuilder::class.java, "LocationDatabase")
            .build().historyDao()


    }
    single {
        Room.databaseBuilder(androidContext(), LocationRoomBuilder::class.java, "LocationDatabase")
            .build().LocationDao()

    }
    factory {
        Repository(get(), get(), get())
    }
    viewModel {
        DataBaseViewModel(get())
    }
    viewModel {
        RetrofitViewModel(get())
    }

}