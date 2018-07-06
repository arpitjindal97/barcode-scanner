package com.arpitjindal97.barcodescanner.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arpitjindal97.barcodescanner.MyApplication

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyApplication.appComponent.inject(this)
    }

}

