package com.nrohmen.roomchat.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.*
import com.nrohmen.roomchat.networking.APIService
import com.nrohmen.roomchat.networking.ServerConstants
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_update_profile.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*


class ChatActivity : AppCompatActivity(), MessageInput.InputListener{
    private val db = FirebaseFirestore.getInstance()
    private var messagesList: MessagesList? = null
    private lateinit var messagesAdapter: MessagesListAdapter<Message>
    private lateinit var imageLoader: ImageLoader
    private lateinit var message: Message
    private lateinit var senderId: String
    private lateinit var user: User
    private lateinit var roomId: String
    private lateinit var role:String
    private lateinit var roomName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        roomName = intent.getStringExtra("name")
        supportActionBar?.title = roomName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        roomId = intent.getStringExtra("id")
        role = intent.getStringExtra("role")
        senderId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        imageLoader = ImageLoader { imageView, url -> Picasso.get().load(url).into(imageView) }
        subscribeTopic()
        getData()
        messagesList = findViewById(R.id.messagesList)
        input.setInputListener(this)
        initAdapter()
    }

    private fun subscribeTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(roomId).addOnCompleteListener {
            var msg = "Topics subscribed"
            if (!it.isSuccessful) {
                msg = "Failed to subscribe topics"
            }
            Log.e("subs", msg)

        }
    }

    private fun getServerService(): APIService {
        val retrofit = Retrofit.Builder()
                .baseUrl(ServerConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(APIService::class.java)
    }

    private fun sendNotif(msg:String){
        val service = getServerService()
        val data = BroadcastMessage(Data(msg, roomId, roomName, role), "'$roomId' in topics")
        val reminderCall = service.broadcastReminder(data)
        reminderCall.enqueue(object: Callback<Void> {

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                Log.i("data", "Request to %s returned %s: %s".format(
                        response?.raw()?.request()?.url(),
                        response?.code(),
                        response?.message()
                ))
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                Log.e("data", "Request returned error %s".format(t?.message))
            }

        })
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
                    sendNotif(chat.text)
                    updateRoom(chat.text, chat.createdAt.toString())
                }
                .addOnFailureListener { e -> e.message?.let { it1 -> snackbar(btn_save, it1).show() } }
    }

    private fun updateRoom(msg:String, time:String){
        db.collection("rooms")
                .document(roomId)
                .update("message", msg, "time", time)
                .addOnFailureListener { e ->
                    Log.e("error", e.message)
                }
    }

    private fun getData(){
        db.collection("chat")
                .whereEqualTo("roomId", roomId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w("error", "listen:error", e)
                        return@EventListener
                    }

                    if (snapshots != null) {
                        for (dc in snapshots.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                user =  User(
                                        dc.document.data["senderId"].toString(),
                                        dc.document.data["senderName"].toString(),
                                        dc.document.data["senderAvatar"].toString(),
                                        "",
                                        "",
                                        "",
                                        "")

                                message = Message(dc.document.id, user, dc.document.data["text"].toString(), dc.document.data["createdAt"] as Date?)
                                messagesAdapter.addToStart(
                                        message, true)
                            }
                        }
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (role == "Admin"){
            menuInflater.inflate(R.menu.menu_room, menu)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.member -> {
                startActivity<RoomMemberActivity>("id" to roomId)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
