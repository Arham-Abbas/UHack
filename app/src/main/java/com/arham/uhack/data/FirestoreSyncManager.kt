package com.arham.uhack.data

import android.content.Context
import android.widget.Toast
import com.arham.uhack.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.Source
import kotlin.collections.mapValues

class FirestoreSyncManager(private val context: Context) {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private var _type = MutableStateFlow<String?>(context.getString(R.string.user_unauthorized))
    private var _assignedTeams = MutableStateFlow<HashMap<String, List<String>>?>(null)
    private var _marks = MutableStateFlow<Map<String, Map<String, Map<String, Int>>>?>(null)
    val type: StateFlow<String?> = _type.asStateFlow()
    val assignedTeams: StateFlow<HashMap<String, List<String>>?> = _assignedTeams.asStateFlow()
    val marks: StateFlow<Map<String, Map<String, Map<String, Int>>>?> = _marks.asStateFlow()
    val photoUrl = user?.photoUrl.toString()

    init {
        user?.let {
            loadDocument(context.getString(R.string.collection_users), it.uid,
                context.getString(R.string.field_type)
            )
        }
    }

    fun loadDocument(collectionId: String, documentId: String, fieldId: String) {
        val documentRef = firestore.collection(collectionId).document(documentId)

        // Load from cache first
        documentRef.get(Source.CACHE).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                setVar(documentSnapshot, fieldId)
                // Attach listener for real-time updates
                attachListenerToDocument(collectionId, documentId, fieldId)
            }
        }
            .addOnFailureListener{
                documentRef.get(Source.SERVER).addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        setVar(documentSnapshot, fieldId)
                        // Attach listener for real-time updates
                        attachListenerToDocument(collectionId, documentId, fieldId)
                    }
                }
                    .addOnFailureListener {
                        when (fieldId) {
                            context.getString(R.string.field_type) -> {
                                _type.value = context.getString(R.string.user_unauthorized)
                            }
                            context.getString(R.string.field_assigned_teams) -> {
                                _assignedTeams.value = null
                            }
                            context.getString(R.string.field_marks) -> {
                                _marks.value = null
                            }
                        }
                    }
            }
    }

    fun loadCollection(collectionId: String) {
        val collection = firestore.collection(collectionId)
        collection
            .get(Source.CACHE)
            .addOnSuccessListener { querySnapshot ->
                setVar(querySnapshot)
                attachListenerToCollection(collectionId)
            }
            .addOnFailureListener {
                collection
                    .get(Source.SERVER)
                    .addOnSuccessListener { querySnapshot ->
                        setVar(querySnapshot)
                        attachListenerToCollection(collectionId)
                    }
                    .addOnFailureListener {
                        _marks.value = null
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
            context.getString(R.string.key_round1),
            context.getString(R.string.key_round2),
            context.getString(R.string.key_round3) -> {
                    // Safely convert Long to Int
                val marksData = documentSnapshot.get(fieldId) as? Map<String, Any>
                if (marksData != null) {
                    // Convert nested map values to Int (if necessary)
                    val convertedMarksData = marksData.mapValues { (_, value) ->
                            (value as? Long)?.toInt() ?: (value as? Int) ?: 0
                    }
                    // Append to existing data
                    _marks.value = (_marks.value ?: emptyMap()).toMutableMap().apply {
                        this[documentSnapshot.id] = (this[documentSnapshot.id] ?: emptyMap()).toMutableMap().apply {
                            this[fieldId] = convertedMarksData
                        }
                    }
                }
            }
        }
    }

    private fun setVar(querySnapshot: QuerySnapshot) {
        for (documentSnapshot in querySnapshot.documents) {
            val rounds = listOf(context.getString(R.string.key_round1), context.getString(R.string.key_round2), context.getString(R.string.key_round3))
            for (round in rounds) {
                setVar(documentSnapshot, round)
            }
        }
    }

    private fun attachListenerToDocument(collectionId: String, documentID: String, fieldId: String) {
        firestore.collection(collectionId).document(documentID)
            .addSnapshotListener { snapshot, exception ->
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

    private fun attachListenerToCollection(collectionId: String) {
        firestore.collection(collectionId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle errors
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Update _marks.value with the new data from the snapshot
                    setVar(snapshot)
                }
            }
    }

    fun saveDocument(collectionId: String, documentId: String, data: MutableMap<String, Any>) {
        val documentRef = firestore.collection(collectionId).document(documentId)

        documentRef.set(data)
            .addOnSuccessListener {
                // Document saved successfully
                Toast.makeText(context, context.getString(R.string.save_successful), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Toast.makeText(context, context.getString(R.string.save_error) + exception.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun update() {
        if (_type.value == context.getString(R.string.type_mentors)) {
            user?.let {
                loadDocument(context.getString(R.string.collection_mentors), it.uid,
                    context.getString(R.string.field_assigned_teams)
                )
            }
        }
    }
}