package com.nrohmen.roomchat.ui.activity

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.Chat
import com.nrohmen.roomchat.model.Message
import com.nrohmen.roomchat.model.User
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_update_profile.*
import org.jetbrains.anko.design.snackbar
import java.util.*

class ChatActivity : AppCompatActivity(), MessageInput.InputListener{
    private val db = FirebaseFirestore.getInstance()
    private var messagesList: MessagesList? = null
    private lateinit var messagesAdapter: MessagesListAdapter<Message>
    private lateinit var imageLoader: ImageLoader
    private lateinit var message: Message
    private lateinit var senderId: String
    private lateinit var user: User
    private var msg: MutableList<Message> = mutableListOf()
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        roomId = intent.getStringExtra("id")
        senderId = FirebaseAuth.getInstance().currentUser?.uid.toString()
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
        val chat = Chat(roomId, senderId, FirebaseAuth.getInstance().currentUser?.displayName.toString(), FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                input.toString(), Date())
        sendMessage(chat)

        return true
    }

    private fun sendMessage(chat: Chat){
        val ref = db.collection("chat").document(chat.createdAt.toString())
        ref.set(chat)
                .addOnSuccessListener {
                    user =  User(
                            senderId,
                            FirebaseAuth.getInstance().currentUser?.displayName.toString(),
                            FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                            FirebaseAuth.getInstance().currentUser?.email.toString(),
                            "",
                            "",
                            FirebaseInstanceId.getInstance().token.toString())

                    message = Message(ref.id, user, chat.text)
                    messagesAdapter.addToStart(
                            message, true)
                }
                .addOnFailureListener { e -> e.message?.let { it1 -> snackbar(btn_save, it1).show() } }
    }

    private fun getData(){
        db.collection("chat")
                .whereEqualTo("roomId", roomId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            user =  User(
                                    document.data["senderId"].toString(),
                                    document.data["senderName"].toString(),
                                    document.data["senderAvatar"].toString(),
                                    "",
                                    "",
                                    "",
                                    "")

                            message = Message(document.id, user, document.data["text"].toString(), document.data["createdAt"] as Date?)
                            msg.add(message)
                        }

                        messagesAdapter.addToEnd(msg, true)

                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        getData()
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
