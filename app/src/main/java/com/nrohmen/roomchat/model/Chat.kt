package com.nrohmen.roomchat.model

import java.util.*

data class Chat (val roomId: String, val senderId: String, val senderName: String, val senderAvatar: String, var text: String, var createdAt: Date?)