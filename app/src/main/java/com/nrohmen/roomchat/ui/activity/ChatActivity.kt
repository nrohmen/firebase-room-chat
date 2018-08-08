package com.nrohmen.roomchat.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
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

    private var senderId: String? = null
    private var messagesList: MessagesList? = null
    private lateinit var messagesAdapter: MessagesListAdapter<Message>
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        senderId = FirebaseAuth.getInstance().currentUser?.uid
        imageLoader = ImageLoader { imageView, url -> Picasso.get().load(url).into(imageView) }

        this.messagesList = findViewById(R.id.messagesList)
        input.setInputListener(this)
        initAdapter()
    }

    private fun initAdapter() {
        messagesAdapter = MessagesListAdapter(senderId, imageLoader)
        this.messagesList?.setAdapter(messagesAdapter)
    }

    override fun onSubmit(input: CharSequence): Boolean {
        val user = senderId?.let {
            User(
                    it,
                FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                FirebaseAuth.getInstance().currentUser?.email.toString(),
                " ",
                "",
                FirebaseInstanceId.getInstance().token.toString())
        }
        messagesAdapter.addToStart(
                user?.let { Message("0", it, input.toString()) }, true)

        return true
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
}
