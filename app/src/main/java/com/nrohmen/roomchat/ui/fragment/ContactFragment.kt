package com.nrohmen.roomchat.ui.fragment

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.User
import com.nrohmen.roomchat.ui.adapter.ContactAdapter
import kotlinx.android.synthetic.main.fragment_contact.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh

class ContactFragment : Fragment() {
    private var users: MutableList<User> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getData()
        swipe_refresh.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)
        swipe_refresh.onRefresh {
            getData()
        }
    }

    private fun getData(){
        users.clear()
        progress_bar.visibility = View.VISIBLE
        db.collection("users")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_refresh.isRefreshing = false
                        progress_bar.visibility = View.INVISIBLE
                        for (document in task.result) {
                            users.add(User(document.data["userId"].toString(),
                                    document.data["name"].toString(),
                                    document.data["email"].toString(),
                                    document.data["phone"].toString(),
                                    document.data["role"].toString(),
                                    document.data["image"].toString(),
                                    document.data["token"].toString()))
                        }

                        list_contact.layoutManager = LinearLayoutManager(ctx)
                        list_contact.adapter = ContactAdapter(ctx, users) {

                        }

                        list_contact.adapter.notifyDataSetChanged()
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
    }


}
