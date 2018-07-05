package com.arpitjindal97.barcodescanner.data.network.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.arpitjindal97.barcodescanner.utils.ServerResponse
import com.arpitjindal97.barcodescanner.utils.ServerStatus
import com.arpitjindal97.barcodescanner.data.network.Webservice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ServerRepository @Inject constructor(private val webservice: Webservice) {

    fun postResult(serverStatus: LiveData<ServerStatus>,
                   progressCount: LiveData<String>,
                   resultArray: Array<String>) {

        val list = listOf<Observable<ServerResponse>>().toMutableList()

        for (result in resultArray) {
            list.add(webservice.postResult(ServerResponse(result)))
        }
        var count = 0

        Observable.zip(list) {}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            count += 1
                            (progressCount as MutableLiveData<String>).value =
                                    count.toString() + "/" + resultArray.size.toString()
                        },
                        onError = {
                            (serverStatus as MutableLiveData<ServerStatus>).value = ServerStatus("failed")
                            it.printStackTrace()
                        },
                        onComplete = {
                            (serverStatus as MutableLiveData<ServerStatus>).value = ServerStatus("sent")
                        }
                )

        /*
         webservice.postResult(result).enqueue(object : Callback<ServerResponse> {

             override fun onFailure(call: Call<ServerResponse>?, t: Throwable?) {
                 (serverStatus as MutableLiveData<ServerStatus>).value = ServerStatus("failed")
             }

             override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                 (serverStatus as MutableLiveData<ServerStatus>).value = ServerStatus("sent")
                 (progressCount as MutableLiveData<String>).value = "1/1"
             }
         })
         */

    }

}