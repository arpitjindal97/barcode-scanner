package com.arpitjindal97.barcodescanner.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.arpitjindal97.barcodescanner.MyApplication
import com.arpitjindal97.barcodescanner.data.network.model.ServerRepository
import com.arpitjindal97.barcodescanner.utils.ResultHolder
import com.arpitjindal97.barcodescanner.utils.ServerStatus
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import javax.inject.Inject

class MainViewModel : ViewModel(), ZXingScannerView.ResultHandler {

    private var flash = MutableLiveData<Boolean>()
    private var result = MutableLiveData<String>()
    private var serverStatus = MutableLiveData<ServerStatus>()
    private var progressCount = MutableLiveData<String>()

    @Inject
    lateinit var serverRepository: ServerRepository

    @Inject
    lateinit var resultHolder: ResultHolder

    fun init() {
        flash.value = false
        serverStatus.value = ServerStatus("sent")
        progressCount.value = "0/0"
        MyApplication.appComponent.inject(this)
    }

    fun clickFlash() {
        flash.value = !flash.value!!
    }

    fun getFlash(): LiveData<Boolean> {
        return flash
    }

    fun getScanResult(): LiveData<String> {
        return result
    }

    override fun handleResult(p0: Result?) {
        resultHolder.parseResult(p0.toString())
        result.value = resultHolder.getString()
    }

    fun getProgressStatus(): LiveData<ServerStatus> {
        return serverStatus
    }

    fun getProgressCount(): LiveData<String> {
        return progressCount
    }

    fun sendButtonClicked() {
        serverStatus.value = ServerStatus("start_sending")

        serverRepository.postResult(serverStatus, progressCount,resultHolder.getArray())

    }

}