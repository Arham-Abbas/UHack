package com.arham.uhack.data

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager(context: Context) {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private var context: Context? = null
    private var _type = MutableStateFlow<String?>(null)
    val type: StateFlow<String?> = _type.asStateFlow()

    init {
        this.context = context
        val documentRef = user?.let { firestore.collection("users").document(it.uid) }

        documentRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                _type.value = documentSnapshot.getString("type")
                attachListenerToDocument("users", documentSnapshot.id)
            }
        }
    }
    suspend fun synchronizeCollection(collectionName: String) {
        val collectionRef = firestore.collection(collectionName)

        // Read a specific document
        val documentRef = collectionRef.document("documentId")
        val documentSnapshot = documentRef.get().await()
        if (documentSnapshot.exists()) {
            val data = documentSnapshot.data
            // Process the data
        }

        // Write to a specific document
        val newData = mapOf("field1" to "value1", "field2" to "value2")
        documentRef.set(newData).await()

        // Listen for real-time updates to the collection
        collectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle errors
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    // Process document changes
                }
            }
        }
    }

    private fun attachListenerToDocument(collectionId: String, documentId: String) {
        val documentRef = firestore.collection(collectionId).document(documentId)
        documentRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle errors
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val newType = snapshot.getString("type")
                if (newType != _type.value) {
                    _type.value = newType
                    }
            }
        }
    }
}