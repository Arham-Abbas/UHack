package com.arham.uhack.data

import android.content.Context
import android.widget.Toast
import com.arham.uhack.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.Source

class FirestoreSyncManager(private val context: Context) {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private var _type = MutableStateFlow<String?>(context.getString(R.string.user_unauthorized))
    private var _assignedTeams = MutableStateFlow<HashMap<String, List<String>>?>(null)
    val type: StateFlow<String?> = _type.asStateFlow()
    val assignedTeams: StateFlow<HashMap<String, List<String>>?> = _assignedTeams.asStateFlow()
    val photoUrl = user?.photoUrl.toString()

    init {
        loadDocument(context.getString(R.string.collection_users),
            context.getString(R.string.field_type)
        )
    }

    private fun loadDocument(collectionId: String, fieldId: String) {
        val documentRef = user?.let { firestore.collection(collectionId).document(it.uid) }

        // Load from cache first
        documentRef?.get(Source.CACHE)?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                setVar(documentSnapshot, fieldId)
                // Attach listener for real-time updates
                attachListenerToDocument(collectionId, fieldId)
            }
        }
            ?.addOnFailureListener{
                documentRef.get(Source.SERVER).addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        setVar(documentSnapshot, fieldId)
                        // Attach listener for real-time updates
                        attachListenerToDocument(collectionId, fieldId)
                    }
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun setVar(documentSnapshot: DocumentSnapshot, fieldId: String) {
        when (fieldId) {
            context.getString(R.string.field_type) -> {
                _type.value = documentSnapshot.getString(fieldId)
            }
            context.getString(R.string.field_assigned_teams) -> {
                _assignedTeams.value = documentSnapshot.get(fieldId) as? HashMap<String, List<String>>
            }
        }
    }

    private fun attachListenerToDocument(collectionId: String, fieldId: String) {
        user?.let { firestore.collection(collectionId).document(it.uid) }
            ?.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle errors
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    setVar(snapshot, fieldId)
                }
            }
    }
    fun update() {
        if (_type.value == context.getString(R.string.type_mentors)) {
            loadDocument(context.getString(R.string.collection_mentors),
                context.getString(R.string.field_assigned_teams)
            )
        }
    }
}