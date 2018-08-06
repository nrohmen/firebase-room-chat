package com.nrohmen.roomchat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.sdk25.coroutines.onClick

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null){
            text_name.text = FirebaseAuth.getInstance().currentUser?.displayName
            text_email.text = FirebaseAuth.getInstance().currentUser?.email
            Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl).into(img_profile)
        }

        help.onClick {
            snackbar(help, "On Development").show()
        }

        about.onClick {
            snackbar(about, "On Development").show()
        }
    }


}
