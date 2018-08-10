package com.nrohmen.roomchat.ui.activity

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.User
import com.nrohmen.roomchat.ui.adapter.ContactAdapter
import kotlinx.android.synthetic.main.activity_room_member.*
import org.jetbrains.anko.support.v4.onRefresh

class RoomMemberActivity internal constructor() : AppCompatActivity() {
    private var users: MutableList<User> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var roomId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_member)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Room Member"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        roomId = intent.getStringExtra("id")
        getRoom()
        swipe_refresh.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipe_refresh.onRefresh {
            getRoom()
        }
    }

    private fun getRoom(){
        db.collection("rooms").document(roomId).collection("member")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {
                            getMember(document.data["id"].toString())
                        }
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun getMember(id: String){
        users.clear()
        progress_bar.visibility = View.VISIBLE
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {
                            users.add(User(document.data["userId"].toString(),
                                    document.data["name"].toString(),
                                    document.data["avatar"].toString(),
                                    document.data["email"].toString(),
                                    document.data["phone"].toString(),
                                    document.data["role"].toString(),
                                    document.data["token"].toString()))
                        }

                        list_member.layoutManager = LinearLayoutManager(this)
                        list_member.adapter = ContactAdapter(this, users) {

                        }

                        list_member.adapter.notifyDataSetChanged()
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                true
            }
        }
    }
}
