package com.nrohmen.roomchat.model

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.MessageContentType

import java.util.Date

class Message private constructor(private val id: String, private val user: User, private var text: String?, private var createdAt: Date?) : IMessage {

    constructor(id: String, user: User, text: String) : this(id, user, text, Date())

    override fun getId(): String {
        return id
    }

    override fun getText(): String? {
        return text
    }

    override fun getCreatedAt(): Date? {
        return createdAt
    }

    override fun getUser(): User {
        return this.user
    }

    fun setText(text: String) {
        this.text = text
    }

    fun setCreatedAt(createdAt: Date) {
        this.createdAt = createdAt
    }

}
