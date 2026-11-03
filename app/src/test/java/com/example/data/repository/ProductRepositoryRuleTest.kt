package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Product
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ProductRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createProduct_authenticatedUser_success() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = ProductRepository(firestore)

        val product = Product(
            title = "Manual de Tráfego Pago",
            subtitle = "Escalando infoprodutos do zero aos 100k",
            niche = "Marketing Digital",
            targetAudience = "Afiliados e produtores iniciantes",
            tone = "Direto e Prático",
            status = "ready",
            chapterCount = 8,
            price = 97.0
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createProduct(product)
        }
        assertTrue("Product creation should succeed", result.isSuccess)
        val productId = result.getOrThrow()
        assertTrue("Generated ID should not be blank", productId.isNotBlank())
    }

    @Test
    fun observeProducts_authenticatedOwner_returnsMatchingProduct() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = ProductRepository(firestore)

        val product = Product(
            title = "Guia Completo de Nutrição",
            subtitle = "Receitas saudáveis e práticas",
            niche = "Saúde e Bem-estar",
            targetAudience = "Pessoas ocupadas",
            tone = "Acolhedor",
            status = "ready",
            chapterCount = 6,
            price = 47.0
        )

        val productId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createProduct(product).getOrThrow()
        }

        val products = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeProducts(uid).first { list -> list.any { it.id == productId } }
        }
        assertTrue("Emitted list should contain created product", products.any { it.id == productId })
    }

    @Test
    fun observeProducts_unauthenticated_fails() = runBlocking {
        auth.signOut()
        val repository = ProductRepository(firestore)

        var caughtException: Exception? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeProducts("some_random_uid").first()
            }
            fail("Expected exception when unauthenticated")
        } catch (e: Exception) {
            caughtException = e
        }

        assertNotNull("Should have caught an exception", caughtException)
        val firestoreException = checkNotNull(caughtException?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caughtException"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    companion object {
        private const val ALICE_EMAIL = "alice@vendeia.test"
        private const val DEFAULT_TIMEOUT_MS = 6000L
        private const val FLOW_TIMEOUT_MS = 4000L
    }
}
