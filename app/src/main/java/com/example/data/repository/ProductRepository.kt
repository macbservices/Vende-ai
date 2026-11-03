package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Chapter
import com.example.data.model.Product
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

class ProductRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun observeProducts(userId: String): Flow<List<Product>> {
        return db.collection("products")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Product::class.java) }
    }

    suspend fun getProduct(productId: String): Product? {
        val doc = db.collection("products").document(productId).get().await()
        return doc.toObject(Product::class.java)
    }

    suspend fun createProduct(product: Product): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Usuário não autenticado"))
        val docRef = db.collection("products").document()
        val data = mapOf(
            "userId" to uid,
            "title" to product.title,
            "subtitle" to product.subtitle,
            "niche" to product.niche,
            "targetAudience" to product.targetAudience,
            "tone" to product.tone,
            "status" to product.status,
            "chapterCount" to product.chapterCount,
            "price" to product.price,
            "currency" to product.currency,
            "coverImageUrl" to product.coverImageUrl,
            "summary" to product.summary,
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

    suspend fun updateProduct(product: Product): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Usuário não autenticado"))
        val docRef = db.collection("products").document(product.id)
        val data = mapOf(
            "userId" to uid,
            "title" to product.title,
            "subtitle" to product.subtitle,
            "niche" to product.niche,
            "targetAudience" to product.targetAudience,
            "tone" to product.tone,
            "status" to product.status,
            "chapterCount" to product.chapterCount,
            "price" to product.price,
            "currency" to product.currency,
            "coverImageUrl" to product.coverImageUrl,
            "summary" to product.summary,
            "createdAt" to (product.createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        val docRef = db.collection("products").document(productId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // CHAPTERS SUBCOLLECTION
    fun observeChapters(productId: String, userId: String): Flow<List<Chapter>> {
        return db.collection("products").document(productId).collection("chapters")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Chapter::class.java).sortedBy { it.sequence }
            }
    }

    suspend fun saveChapter(chapter: Chapter): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Usuário não autenticado"))
        val docRef = if (chapter.id.isNotBlank()) {
            db.collection("products").document(chapter.productId).collection("chapters").document(chapter.id)
        } else {
            db.collection("products").document(chapter.productId).collection("chapters").document()
        }
        val data = mapOf(
            "productId" to chapter.productId,
            "userId" to uid,
            "title" to chapter.title,
            "sequence" to chapter.sequence,
            "content" to chapter.content,
            "wordCount" to chapter.wordCount,
            "status" to chapter.status,
            "isReviewed" to chapter.isReviewed,
            "createdAt" to (chapter.createdAt ?: FieldValue.serverTimestamp()),
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
