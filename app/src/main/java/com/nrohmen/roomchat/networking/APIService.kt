package com.nrohmen.roomchat.networking

import com.nrohmen.roomchat.model.BroadcastMessage
import com.nrohmen.roomchat.model.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface APIService {
    @Headers(
            ServerConstants.AUTH,
            ServerConstants.TYPE
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: Notification) : Call<Void>

    @Headers(
            ServerConstants.AUTH,
            ServerConstants.TYPE
    )
    @POST("fcm/send")
    fun broadcastReminder(@Body broadcastMessage: BroadcastMessage) : Call<Void>
}

