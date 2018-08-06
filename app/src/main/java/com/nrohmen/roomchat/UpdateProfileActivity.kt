package com.nrohmen.roomchat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.nrohmen.roomchat.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_update_profile.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity


class UpdateProfileActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        supportActionBar?.title = "Update Profile"
        name.text = FirebaseAuth.getInstance().currentUser?.displayName
        email.text = FirebaseAuth.getInstance().currentUser?.email
        phone.setText(FirebaseAuth.getInstance().currentUser?.phoneNumber)
        Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl).into(profile_image)
        btn_save.onClick {
            doRegister()
        }
    }

    private fun doRegister(){
        progress_bar.visibility = View.VISIBLE
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val data = User(userId.toString(), name.text.toString(), email.text.toString(), phone.text.toString(), "User",
                FirebaseAuth.getInstance().currentUser?.photoUrl.toString(), FirebaseInstanceId.getInstance().token.toString())
        data.let { it ->
            db.collection("users").document(userId.toString())
                    .set(it)
                    .addOnSuccessListener {
                        startActivity<MainActivity>()
                        finish()
                    }
                    .addOnFailureListener { e -> e.message?.let { it1 -> snackbar(btn_save, it1).show() } }
        }
    }
}
