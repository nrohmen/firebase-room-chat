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
import com.nrohmen.roomchat.model.*
import com.nrohmen.roomchat.networking.APIService
import com.nrohmen.roomchat.networking.ServerConstants
import com.nrohmen.roomchat.ui.adapter.ContactAdapter
import kotlinx.android.synthetic.main.activity_room_member.*
import org.jetbrains.anko.support.v4.onRefresh
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class AddMemberActivity : AppCompatActivity() {
    private var users: MutableList<User> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var roomId:String
    private lateinit var name:String
    private lateinit var role:String
    private var device: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)
        name = intent.getStringExtra("name")
        roomId = intent.getStringExtra("id")
        role = intent.getStringExtra("role")
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
        device.add(id)
        db.collection("users/$id/rooms")
                .document(roomId)
                .set(data)
                .addOnSuccessListener {
                    sendNotification()
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

    private fun getServerService(): APIService {
        val retrofit = Retrofit.Builder()
                .baseUrl(ServerConstants.URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(APIService::class.java)
    }

    private fun sendNotification(){
        val service = getServerService()
        val data = Notification(Data("Anda belum menyelesaikan pekerjaan", roomId, name, role), device)
        val reminderCall = service.sendNotification(data)
        reminderCall.enqueue(object: Callback<Void> {

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                finish()
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
}
