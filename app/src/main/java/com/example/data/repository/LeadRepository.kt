package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Lead
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class LeadRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun observeLeads(userId: String): Flow<List<Lead>> {
        return db.collection("leads")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Lead::class.java).sortedByDescending { it.createdAt } }
    }

    suspend fun saveLead(lead: Lead): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Usuário não autenticado"))
        val docRef = if (lead.id.isNotBlank()) {
            db.collection("leads").document(lead.id)
        } else {
            db.collection("leads").document()
        }
        val data = mapOf(
            "userId" to uid,
            "name" to lead.name,
            "email" to lead.email,
            "phone" to lead.phone,
            "stage" to lead.stage,
            "origin" to lead.origin,
            "tags" to lead.tags,
            "notes" to lead.notes,
            "createdAt" to (lead.createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateLeadStage(leadId: String, newStage: String): Result<Unit> {
        val docRef = db.collection("leads").document(leadId)
        return try {
            docRef.update(
                mapOf(
                    "stage" to newStage,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }
}
