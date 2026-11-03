package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "creator", // admin, creator, member
    val planId: String = "pro",   // free, basic, pro, enterprise
    val organizationId: String = "org_default",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val niche: String = "",
    val targetAudience: String = "",
    val tone: String = "Profissional e Inspirador",
    val status: String = "draft", // draft, generating, ready, published
    val chapterCount: Long = 5L,
    val price: Double = 97.0,
    val currency: String = "BRL",
    val coverImageUrl: String = "",
    val summary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Chapter(
    @DocumentId val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val title: String = "",
    val sequence: Long = 0L,
    val content: String = "",
    val wordCount: Long = 0L,
    val status: String = "pending", // pending, generating, completed, error
    val isReviewed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LandingPage(
    @DocumentId val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val title: String = "",
    val headline: String = "",
    val subheadline: String = "",
    val benefits: String = "",
    val guarantee: String = "7 dias de garantia incondicional",
    val bonus: String = "Acesso a checklist prático em PDF",
    val theme: String = "Modern Dark",
    val status: String = "draft", // draft, published
    val publicSlug: String = "",
    val callToAction: String = "Garantir Minha Vaga com Desconto",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val amount: Double = 97.0,
    val status: String = "pending", // pending, paid, cancelled, refunded
    val paymentMethod: String = "pix", // pix, credit_card, boleto
    val pixCode: String = "",
    val isDelivered: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Lead(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val stage: String = "new", // new, contacted, negotiating, interested, converted, lost
    val origin: String = "Landing Page",
    val tags: String = "Quente, Ebook",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AiJob(
    @DocumentId val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val type: String = "full_ebook", // outline, chapter, sales_page, campaign, creatives, full_ebook
    val status: String = "pending", // pending, running, completed, failed
    val progress: Long = 0L,
    val currentStep: String = "",
    val resultSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class MarketingCampaign(
    val id: String = "",
    val productId: String = "",
    val title: String = "",
    val targetPersona: String = "",
    val hook: String = "",
    val adCopy: String = "",
    val callToAction: String = "",
    val emailSequence: List<String> = emptyList(),
    val hashtags: List<String> = emptyList()
)

data class CreativeScript(
    val id: String = "",
    val productId: String = "",
    val format: String = "Reels / TikTok (60s)",
    val title: String = "",
    val hook: String = "",
    val body: String = "",
    val visualPrompt: String = "",
    val cta: String = ""
)

data class SaaSPlan(
    val id: String,
    val name: String,
    val monthlyPrice: Double,
    val productLimit: Int,
    val aiTokensDesc: String,
    val features: List<String>
)
