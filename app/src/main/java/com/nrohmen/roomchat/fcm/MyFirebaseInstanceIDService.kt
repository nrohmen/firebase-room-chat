package com.nrohmen.roomchat.fcm

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: $refreshedToken")

        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String?) {
        val db = FirebaseFirestore.getInstance()
        FirebaseAuth.getInstance().currentUser?.uid?.let { it ->
            db.collection("users")
                .document(it)
                .update("token", token)
                .addOnSuccessListener {
                    Log.e("user", "token refreshed")
                }
                    .addOnFailureListener { e ->
                        Log.e("error", e.message)
                    }
        }
    }

    companion object {

        private val TAG = "MyFirebaseIIDService"
    }
}