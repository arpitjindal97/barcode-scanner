package com.arpitjindal97.barcodescanner.di

import com.arpitjindal97.barcodescanner.MyApplication
import com.arpitjindal97.barcodescanner.ui.main.MainActivity
import com.arpitjindal97.barcodescanner.ui.main.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[AppModule::class])
interface AppComponent {

    fun inject(myApplication: MyApplication)

    fun inject(mainActivity: MainActivity)

    fun inject(mainViewModel: MainViewModel)

}