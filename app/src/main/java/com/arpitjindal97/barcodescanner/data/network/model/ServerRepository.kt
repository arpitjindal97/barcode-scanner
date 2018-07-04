package com.arpitjindal97.barcodescanner.data.network.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.arpitjindal97.barcodescanner.utils.ServerResponse
import com.arpitjindal97.barcodescanner.utils.ServerStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ServerRepository @Inject constructor(private val webservice: Webservice) {

    fun postResult(serverStatus: LiveData<ServerStatus>,
                   progressCount: LiveData<String>,
                   result: ServerResponse) {

        webservice.postResult(result).enqueue(object : Callback<ServerResponse> {

            override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                (serverStatus as MutableLiveData<ServerStatus>).value = ServerStatus("failed")
            }

            override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                (serverStatus as MutableLiveData<ServerStatus>).value = ServerStatus("sent")
                (progressCount as MutableLiveData<String>).value = "1/1"
            }
        })
    }

}