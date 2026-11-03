package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Order
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

class OrderRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun observeOrders(userId: String): Flow<List<Order>> {
        return db.collection("orders")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Order::class.java).sortedByDescending { it.createdAt } }
    }

    suspend fun createOrder(order: Order): Result<String> {
        val docRef = db.collection("orders").document()
        val data = mapOf(
            "productId" to order.productId,
            "userId" to order.userId,
            "customerName" to order.customerName,
            "customerEmail" to order.customerEmail,
            "customerPhone" to order.customerPhone,
            "amount" to order.amount,
            "status" to order.status,
            "paymentMethod" to order.paymentMethod,
            "pixCode" to order.pixCode,
            "isDelivered" to order.isDelivered,
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

    suspend fun updateOrderStatus(orderId: String, status: String, isDelivered: Boolean): Result<Unit> {
        val docRef = db.collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to status,
                    "isDelivered" to isDelivered,
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
