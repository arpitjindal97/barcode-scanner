package com.arpitjindal97.barcodescanner.di

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.arpitjindal97.barcodescanner.data.network.model.ServerRepository
import com.arpitjindal97.barcodescanner.data.network.Webservice
import com.arpitjindal97.barcodescanner.utils.ResultHolder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
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
    fun provideRetrofit(@Named("ServerURL") url: String): Retrofit =
            Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

    @Provides
    @Singleton
    fun provideWebservice(retrofit: Retrofit): Webservice =
            retrofit.create(Webservice::class.java)

    @Provides
    @Singleton
    fun provideResultHolder(context: Context): ResultHolder =
            ResultHolder(context)

    @Provides
    @Named("ServerURL")
    fun provideServerURL(context: Context): String {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val ip = sharedPreferences.getString("ip_address", "default")
        val port = sharedPreferences.getString("port_number", "default")

        return "http://$ip:$port"
    }
}