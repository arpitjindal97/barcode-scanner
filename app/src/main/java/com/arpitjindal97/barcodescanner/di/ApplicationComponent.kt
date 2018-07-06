package com.arpitjindal97.barcodescanner.di

import com.arpitjindal97.barcodescanner.MyApplication
import com.arpitjindal97.barcodescanner.ui.base.BaseActivity
import com.arpitjindal97.barcodescanner.ui.base.BaseViewModel
import com.arpitjindal97.barcodescanner.ui.main.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules=[AppModule::class])
interface AppComponent {

    fun inject(myApplication: MyApplication)

    fun inject(baseActivity: BaseActivity)

    fun inject(mainViewModel: MainViewModel)

}