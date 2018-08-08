package com.nrohmen.roomchat.model

class MessagesFixtures private constructor() {
    init {
        throw AssertionError()
    }

    companion object {

        fun getTextMessage(text: String): Message {
            return Message("0", user, text)
        }

        private val user: User
            get() = User(
                    "0",
                    "Nur",
                    "http://i.imgur.com/pv1tBmT.png",
                    " ",
                    " ",
                    "",
                    " ")
    }
}
