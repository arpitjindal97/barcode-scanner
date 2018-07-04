package com.arpitjindal97.barcodescanner.data.network.model

import com.arpitjindal97.barcodescanner.utils.ServerResponse
import retrofit2.Call
import retrofit2.http.GET
import com.arpitjindal97.barcodescanner.utils.ServerStatus
import retrofit2.http.Body
import retrofit2.http.POST

interface Webservice {

    @GET("/status")
    fun getStatus(): Call<ServerStatus>

    @POST("/result")
    fun postResult(@Body result: ServerResponse): Call<ServerResponse>
}

