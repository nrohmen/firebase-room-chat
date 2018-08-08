package com.nrohmen.roomchat.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.Message
import com.nrohmen.roomchat.model.User
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity(), MessageInput.InputListener{

    private var messagesList: MessagesList? = null
    private lateinit var messagesAdapter: MessagesListAdapter<Message>
    private val senderId = "0"
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageLoader = ImageLoader { imageView, url -> Picasso.get().load(url).into(imageView) }

        this.messagesList = findViewById(R.id.messagesList)
        input.setInputListener(this)
        initAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSubmit(input: CharSequence): Boolean {
        val user = User(
                "0",
                "Nur",
                "http://i.imgur.com/pv1tBmT.png",
                " ",
                " ",
                "",
                " ")
        messagesAdapter.addToStart(
                Message("0", user, input.toString()), true)
        return true
    }

    private fun initAdapter() {
        messagesAdapter = MessagesListAdapter(senderId, imageLoader)
        this.messagesList?.setAdapter(messagesAdapter)
    }
}
