package com.nrohmen.roomchat.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.User
import com.squareup.picasso.Picasso

/**
 * Created by root on 1/16/18.
 */
class ContactAdapter(private val context: Context, private val items: List<User>, private val listener: (User) -> Unit)
    : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val name = view.findViewById<TextView>(R.id.name)
        private val phone = view.findViewById<TextView>(R.id.phone)
        private val email = view.findViewById<TextView>(R.id.email)
        private val image = view.findViewById<ImageView>(R.id.image_user)

        fun bindItem(items: User, listener: (User) -> Unit) {
            name.text = items.name
            phone.text = items.phone
            email.text = items.email
            ContextCompat.getDrawable(itemView.context, R.drawable.profile_holder)?.let { Picasso.get().load(items.image).placeholder(it).into(image) }
        }
    }
}