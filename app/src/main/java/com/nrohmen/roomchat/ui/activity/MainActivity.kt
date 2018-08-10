package com.nrohmen.roomchat.ui.activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nrohmen.roomchat.R
import com.nrohmen.roomchat.R.id.*
import com.nrohmen.roomchat.ui.fragment.ContactFragment
import com.nrohmen.roomchat.ui.fragment.ProfileFragment
import com.nrohmen.roomchat.ui.fragment.RoomFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private var isSignIn: Boolean = false
    private val RC_SIGN_IN = 9001
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                contact -> {
                    loadContact(savedInstanceState)
                }
                chat -> {
                    loadRoom(savedInstanceState)
                }
                profile -> {
                    loadProfile(savedInstanceState)
                }
            }
            true
        }
        bottom_navigation.selectedItemId = chat
    }

    private fun loadContact(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportActionBar?.show()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_layout, ContactFragment(), ContactFragment::class.java.simpleName)
                    .commit()
        }
    }

    private fun loadRoom(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportActionBar?.show()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_layout, RoomFragment(), RoomFragment::class.java.simpleName)
                    .commit()
        }
    }

    private fun loadProfile(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportActionBar?.hide()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_layout, ProfileFragment(), ProfileFragment::class.java.simpleName)
                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        loginState()
    }

    private fun  loginState(){
        if (shouldStartSignIn()) {
            startSignIn()
        }
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(listOf<AuthUI.IdpConfig>(AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .setIsSmartLockEnabled(false)
                .build()

        startActivityForResult(intent, RC_SIGN_IN)
        isSignIn = true
    }

    private fun shouldStartSignIn(): Boolean {
        return !isSignIn && FirebaseAuth.getInstance().currentUser == null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            isSignIn = false
            loginState()
            snackbar(bottom_navigation, "Welcome, "+FirebaseAuth.getInstance().currentUser?.displayName).show()
            val query = db.collection("users")
                    .whereEqualTo("id", FirebaseAuth.getInstance().currentUser?.uid)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.documents.size == 0) {
                        startActivity<UpdateProfileActivity>()
                    } else{
                        supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.main_layout, RoomFragment(), RoomFragment::class.java.simpleName)
                                .commit()
                    }
                } else {
                    Log.d(ContentValues.TAG, "get failed with ", task.exception)
                }
            }

            if (resultCode != Activity.RESULT_OK && shouldStartSignIn()) {
                snackbar(bottom_navigation, "Failed to login, please try again.").show()
                startSignIn()
            }
        }
    }
}
