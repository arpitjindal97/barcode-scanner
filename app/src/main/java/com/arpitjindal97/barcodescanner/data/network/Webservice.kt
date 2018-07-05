package com.arpitjindal97.barcodescanner.data.network

import com.arpitjindal97.barcodescanner.utils.ServerResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface Webservice {

    @POST("/result")
    fun postResult(@Body result: ServerResponse): Observable<ServerResponse>
}

