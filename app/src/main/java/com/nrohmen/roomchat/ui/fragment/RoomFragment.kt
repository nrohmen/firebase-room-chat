package com.nrohmen.roomchat.ui.fragment

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.Room
import com.nrohmen.roomchat.ui.activity.ChatActivity
import com.nrohmen.roomchat.ui.adapter.RoomAdapter
import kotlinx.android.synthetic.main.fragment_room.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.startActivity

class RoomFragment : Fragment() {
    private var rooms: MutableList<Room> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var role:String
    private lateinit var userId:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        getRole()
        swipe_refresh.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipe_refresh.onRefresh {
            getRole()
        }
    }

    private fun getRole(){
        rooms.clear()
        progress_bar.visibility = View.VISIBLE
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        role = document.data?.get("role").toString()
                        if (role == "User"){
                            getUserRooms()
                        } else{
                            getAdminRooms()
                        }
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun getUserRooms(){
        db.collection("users").document(userId).collection("rooms")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {
                            getRooms(document.data["roomId"].toString())
                        }
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun getRooms(id: String){
        rooms.clear()
        db.collection("rooms")
                .whereEqualTo("roomId", id)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {

                            rooms.add(Room(document.data["roomId"].toString(),
                                    document.data["name"].toString(),
                                    document.data["image"].toString(),
                                    document.data["message"].toString(),
                                    document.data["time"].toString()))
                        }


                        list_room.layoutManager = LinearLayoutManager(ctx)
                        list_room.adapter = RoomAdapter(ctx, rooms) {
                            startActivity<ChatActivity>("id" to it.roomId, "name" to it.name, "role" to role)
                        }
                        list_room.adapter.notifyDataSetChanged()
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun getAdminRooms(){
        rooms.clear()
        db.collection("rooms")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {

                            rooms.add(Room(document.data["roomId"].toString(),
                                    document.data["name"].toString(),
                                    document.data["image"].toString(),
                                    document.data["message"].toString(),
                                    document.data["time"].toString()))
                        }


                        list_room.layoutManager = LinearLayoutManager(ctx)
                        list_room.adapter = RoomAdapter(ctx, rooms) {
                            startActivity<ChatActivity>("id" to it.roomId, "name" to it.name, "role" to role)
                        }
                        list_room.adapter.notifyDataSetChanged()
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }
}
