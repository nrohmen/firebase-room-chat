package com.nrohmen.roomchat.networking

import com.nrohmen.roomchat.model.BroadcastMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface APIService {
//    @Headers(
//            ServerConstants.AUTH,
//            ServerConstants.TYPE
//    )
//    @POST("fcm/send")
//    fun sendReminder(@Body reminder: Reminder) : Call<Void>

    @Headers(
            ServerConstants.AUTH,
            ServerConstants.TYPE
    )
    @POST("fcm/send")
    fun broadcastReminder(@Body broadcastMessage: BroadcastMessage) : Call<Void>
}

