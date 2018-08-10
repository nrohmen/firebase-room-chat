package com.nrohmen.roomchat.ui.activity

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.Member
import com.nrohmen.roomchat.model.User
import com.nrohmen.roomchat.model.UserRoom
import com.nrohmen.roomchat.ui.adapter.ContactAdapter
import kotlinx.android.synthetic.main.activity_room_member.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh

class AddMemberActivity : AppCompatActivity() {
    private var users: MutableList<User> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var roomId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        roomId = intent.getStringExtra("id")
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Member"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getUsers()
        swipe_refresh.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipe_refresh.onRefresh {
            getUsers()
        }
    }

    private fun getUsers(){
        users.clear()
        progress_bar.visibility = View.VISIBLE
        db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {
                            users.add(User(document.data["id"].toString(),
                                    document.data["name"].toString(),
                                    document.data["avatar"].toString(),
                                    document.data["email"].toString(),
                                    document.data["phone"].toString(),
                                    document.data["role"].toString(),
                                    document.data["token"].toString()))
                        }

                        list_member.layoutManager = LinearLayoutManager(this)
                        list_member.adapter = ContactAdapter(this, users) {
                            addMember(it.id)
                        }

                        list_member.adapter.notifyDataSetChanged()
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun addMember(id:String){
        progress_bar.visibility = View.VISIBLE
        val data = Member(id)
        db.collection("rooms/$roomId/member")
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    addUserRoom(id)
                }
                .addOnFailureListener { e ->
                    Log.e("error", e.message)
                }
    }

    private fun addUserRoom(id:String){
        val data = UserRoom(roomId)
        db.collection("users/$id/rooms")
                .document(roomId)
                .set(data)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("error", e.message)
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
