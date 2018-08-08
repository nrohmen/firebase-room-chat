package com.nrohmen.roomchat.model

import com.stfalcon.chatkit.commons.models.IUser


data class User(private val id:String, private val name:String, private val avatar: String, val email:String, val phone:String,
                val role:String, val token:String) : IUser {
    override fun getAvatar(): String {
        return avatar
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }
}