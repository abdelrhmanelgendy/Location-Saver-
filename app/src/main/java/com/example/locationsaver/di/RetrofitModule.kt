package com.example.locationsaver.di

import com.example.locationsaver.databases.remot.ImageWebSearchAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityComponent::class)
object RetrofitModule {

    @Provides
    fun provideRetrofit(): ImageWebSearchAPI {
        return Retrofit.Builder()
            .baseUrl("https://bing-image-search1.p.rapidapi.com/images/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ImageWebSearchAPI::class.java)
    }

}