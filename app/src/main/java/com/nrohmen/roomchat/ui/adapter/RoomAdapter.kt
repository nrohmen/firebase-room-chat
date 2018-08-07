package com.nrohmen.roomchat.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.model.Room

/**
 * Created by root on 1/16/18.
 */
class RoomAdapter(private val context: Context, private val items: List<Room>, private val listener: (Room) -> Unit)
    : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_room, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val name = view.findViewById<TextView>(R.id.name)
        private val image = view.findViewById<ImageView>(R.id.image_room)

        fun bindItem(items: Room, listener: (Room) -> Unit) {
            name.text = items.name
//            ContextCompat.getDrawable(itemView.context, R.drawable.profile_holder)?.let { Picasso.get().load(items.image).placeholder(it).into(image) }
        }
    }
}