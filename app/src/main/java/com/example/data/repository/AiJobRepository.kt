package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AiJob
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

class AiJobRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun observeJobs(userId: String): Flow<List<AiJob>> {
        return db.collection("aiJobs")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(AiJob::class.java).sortedByDescending { it.createdAt } }
    }

    suspend fun createJob(job: AiJob): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Usuário não autenticado"))
        val docRef = db.collection("aiJobs").document()
        val data = mapOf(
            "userId" to uid,
            "productId" to job.productId,
            "type" to job.type,
            "status" to job.status,
            "progress" to job.progress,
            "currentStep" to job.currentStep,
            "resultSummary" to job.resultSummary,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateJobProgress(jobId: String, status: String, progress: Long, step: String, summary: String = ""): Result<Unit> {
        val docRef = db.collection("aiJobs").document(jobId)
        val data = mutableMapOf<String, Any>(
            "status" to status,
            "progress" to progress,
            "currentStep" to step,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (summary.isNotBlank()) {
            data["resultSummary"] = summary
        }
        return try {
            docRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }
}
