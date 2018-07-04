package com.arpitjindal97.barcodescanner.di

import android.app.Application
import android.content.Context
import com.arpitjindal97.barcodescanner.data.network.model.ServerRepository
import com.arpitjindal97.barcodescanner.data.network.model.Webservice
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideServerRepository(webservice: Webservice): ServerRepository =
            ServerRepository(webservice)

    @Provides
    @Singleton
    @Named("something")
    fun provideString(): String = "same"


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
            Retrofit.Builder()
                    .baseUrl("http://192.168.0.5:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

    @Provides
    @Singleton
    fun provideWebservice(retrofit: Retrofit): Webservice =
           retrofit.create(Webservice::class.java)


}