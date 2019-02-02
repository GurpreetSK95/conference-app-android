package com.mentalmachines.droidcon_boston.views.speaker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mentalmachines.droidcon_boston.data.FirebaseDatabase
import com.mentalmachines.droidcon_boston.firebase.FirebaseHelper

class SpeakerViewModel: ViewModel() {
    private val firebaseHelper: FirebaseHelper by lazy { FirebaseHelper.instance }

    val rows = MutableLiveData<List<FirebaseDatabase.EventSpeaker>>()

    private val dataListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val speakers = mutableListOf<FirebaseDatabase.EventSpeaker>()
            for (speakerSnapshot in dataSnapshot.children) {
                val speaker = speakerSnapshot.getValue(FirebaseDatabase.EventSpeaker::class.java)
                if (speaker != null) {
                    speakers.add(speaker)
                }
            }
            rows.postValue(speakers)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(javaClass.canonicalName, "detailQuery:onCancelled", databaseError.toException())
        }
    }

    fun setup() {
        firebaseHelper
            .speakerDatabase
            .orderByChild("name")
            .addValueEventListener(dataListener)
    }

    fun cleanUp() {
        firebaseHelper.speakerDatabase.removeEventListener(dataListener)
    }
}
