package com.kudziechase.travelmantics.utils

import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kudziechase.travelmantics.InvalidateListener
import com.kudziechase.travelmantics.MainActivity
import com.kudziechase.travelmantics.model.TravelDeal


object FirebaseHelper {
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatabaseRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var mStorage: FirebaseStorage
    lateinit var mStorageRef: StorageReference
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mTravelDeals: ArrayList<TravelDeal>
    val RC_SIGN_IN = 213
    lateinit var activity: MainActivity
    var isAdmin: Boolean = false
    lateinit var invalidateListener: InvalidateListener


    fun initFBReference(ref: String, activity: MainActivity, listener: InvalidateListener) {
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        this.activity = activity

        invalidateListener = listener

        mAuthListener = FirebaseAuth.AuthStateListener {
            if (mAuth.currentUser == null) {
                signIn()
            }
            Toast.makeText(activity, "Welcome!", Toast.LENGTH_SHORT).show()
        }
        connectStorage()

        mTravelDeals = ArrayList()
        mDatabaseRef = mFirebaseDatabase.reference.child(ref)

    }

    private fun signIn() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            , RC_SIGN_IN
        )
    }

    fun signOut() {
        AuthUI.getInstance().signOut(activity)
            .addOnCompleteListener {
                attachListener()
            }
        detachListener()
    }

    fun attachListener() {
        mAuth.addAuthStateListener(mAuthListener)
    }

    fun detachListener() {
        mAuth.removeAuthStateListener(mAuthListener)
    }

    fun connectStorage() {
        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage.reference.child("travel_deals_images")
    }


}