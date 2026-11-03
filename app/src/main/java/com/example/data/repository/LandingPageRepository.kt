package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.LandingPage
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

class LandingPageRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun observeLandingPages(userId: String): Flow<List<LandingPage>> {
        return db.collection("landingPages")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(LandingPage::class.java) }
    }

    suspend fun getPageByProduct(productId: String): LandingPage? {
        val query = db.collection("landingPages")
            .whereEqualTo("productId", productId)
            .limit(1)
            .get()
            .await()
        return query.documents.firstOrNull()?.toObject(LandingPage::class.java)
    }

    suspend fun saveLandingPage(page: LandingPage): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Usuário não autenticado"))
        val docRef = if (page.id.isNotBlank()) {
            db.collection("landingPages").document(page.id)
        } else {
            db.collection("landingPages").document()
        }
        val data = mapOf(
            "productId" to page.productId,
            "userId" to uid,
            "title" to page.title,
            "headline" to page.headline,
            "subheadline" to page.subheadline,
            "benefits" to page.benefits,
            "guarantee" to page.guarantee,
            "bonus" to page.bonus,
            "theme" to page.theme,
            "status" to page.status,
            "publicSlug" to page.publicSlug,
            "callToAction" to page.callToAction,
            "createdAt" to (page.createdAt ?: FieldValue.serverTimestamp()),
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
}
