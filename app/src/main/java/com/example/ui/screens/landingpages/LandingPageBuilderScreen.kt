package com.example.ui.screens.landingpages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.GeminiAiEngine
import com.example.data.model.LandingPage
import com.example.data.model.Product
import com.example.data.repository.LandingPageRepository
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

@Composable
fun LandingPageBuilderScreen(
    currentUserId: String,
    initialProductId: String? = null,
    landingPageRepository: LandingPageRepository,
    productRepository: ProductRepository,
    geminiEngine: GeminiAiEngine
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val products by productRepository.observeProducts(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Editor, 1: Prévia Comercial

    // Form fields
    var headline by remember { mutableStateOf("") }
    var subheadline by remember { mutableStateOf("") }
    var benefitsText by remember { mutableStateOf("") }
    var guaranteeText by remember { mutableStateOf("Garantia incondicional de 7 dias com devolução total do valor.") }
    var bonusText by remember { mutableStateOf("Bônus 1: Checklist prático em PDF\nBônus 2: Acesso ao grupo de networking") }
    var ctaButtonText by remember { mutableStateOf("Garantir Minha Vaga com Desconto") }
    var isPublished by remember { mutableStateOf(false) }
    var publicSlug by remember { mutableStateOf("") }

    var isGeneratingCopy by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(products, initialProductId) {
        if (products.isNotEmpty()) {
            val prod = if (!initialProductId.isNullOrBlank()) {
                products.firstOrNull { it.id == initialProductId } ?: products.first()
            } else {
                products.first()
            }
            selectedProduct = prod

            // Fetch existing landing page if available
            scope.launch {
                val existingPage = landingPageRepository.getPageByProduct(prod.id)
                if (existingPage != null) {
                    headline = existingPage.headline
                    subheadline = existingPage.subheadline
                    benefitsText = existingPage.benefits
                    guaranteeText = existingPage.guarantee
                    bonusText = existingPage.bonus
                    ctaButtonText = existingPage.callToAction
                    isPublished = existingPage.status == "published"
                    publicSlug = existingPage.publicSlug
                } else {
                    headline = "Domine ${prod.title} e Transforme Seus Resultados"
                    subheadline = prod.subtitle.ifBlank { "O passo a passo prático para acelerar sua jornada profissional" }
                    benefitsText = "✓ Método testado e validado\n✓ Acesso imediato em qualquer dispositivo\n✓ Exemplos práticos do zero ao avançado"
                    publicSlug = prod.title.lowercase().replace("[^a-z0-9]".toRegex(), "-").take(25)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("landing_page_builder")
    ) {
        // TopBar
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Construtor de Páginas de Vendas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Copywriting comercial gerado pelo Gemini AI",
                        style = MaterialTheme.typography.bodySmall,
                        color = VendePrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val prod = selectedProduct ?: return@Button
                            isGeneratingCopy = true
                            scope.launch {
                                try {
                                    val copy = geminiEngine.generateSalesPageCopy(
                                        productTitle = prod.title,
                                        subtitle = prod.subtitle,
                                        niche = prod.niche,
                                        targetAudience = prod.targetAudience,
                                        price = prod.price
                                    )
                                    headline = "Descubra o Segredo de ${prod.title} que Está Mudando o Mercado"
                                    subheadline = "O método definitivo para dominar ${prod.niche} sem perder tempo."
                                    benefitsText = "✓ Domínio completo em ${prod.chapterCount} módulos didáticos\n✓ Estratégias reais prontas para aplicação imediata\n✓ Economize meses de tentativa e erro"
                                    Toast.makeText(context, "Copy comercial criada com sucesso!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro na IA: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isGeneratingCopy = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendeSecondary),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isGeneratingCopy && selectedProduct != null
                    ) {
                        if (isGeneratingCopy) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color(0xFF0B1120))
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0B1120), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gerar Copy", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val prod = selectedProduct ?: return@Button
                            isSaving = true
                            scope.launch {
                                val page = LandingPage(
                                    productId = prod.id,
                                    userId = currentUserId,
                                    title = prod.title,
                                    headline = headline,
                                    subheadline = subheadline,
                                    benefits = benefitsText,
                                    guarantee = guaranteeText,
                                    bonus = bonusText,
                                    status = if (isPublished) "published" else "draft",
                                    publicSlug = publicSlug.ifBlank { "produto-${prod.id.take(6)}" },
                                    callToAction = ctaButtonText
                                )
                                landingPageRepository.saveLandingPage(page)
                                isSaving = false
                                Toast.makeText(context, "Página salva com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSaving && selectedProduct != null
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color(0xFF0B1120), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salvar", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tabs: Editor vs Preview
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF0F172A),
            contentColor = VendePrimary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Editor Comercial") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Prévia da Página (Mobile)") }
            )
        }

        if (selectedTabIndex == 0) {
            // EDITOR FORM
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Headline de Impacto", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF131D31),
                        unfocusedContainerColor = Color(0xFF131D31)
                    )
                )

                OutlinedTextField(
                    value = subheadline,
                    onValueChange = { subheadline = it },
                    label = { Text("Subheadline (Apoio & Promessa)", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF131D31),
                        unfocusedContainerColor = Color(0xFF131D31)
                    )
                )

                OutlinedTextField(
                    value = benefitsText,
                    onValueChange = { benefitsText = it },
                    label = { Text("Benefícios & O que você vai aprender", color = Color(0xFF94A3B8)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF131D31),
                        unfocusedContainerColor = Color(0xFF131D31)
                    )
                )

                OutlinedTextField(
                    value = guaranteeText,
                    onValueChange = { guaranteeText = it },
                    label = { Text("Garantia & Risco Zero", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF131D31),
                        unfocusedContainerColor = Color(0xFF131D31)
                    )
                )

                OutlinedTextField(
                    value = bonusText,
                    onValueChange = { bonusText = it },
                    label = { Text("Bônus Inclusos na Oferta", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF131D31),
                        unfocusedContainerColor = Color(0xFF131D31)
                    )
                )

                OutlinedTextField(
                    value = ctaButtonText,
                    onValueChange = { ctaButtonText = it },
                    label = { Text("Texto do Botão de Compra (CTA)", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF131D31),
                        unfocusedContainerColor = Color(0xFF131D31)
                    )
                )

                // Publication Status Toggle
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPublished) "Status: Página Publicada Online" else "Status: Rascunho Interno",
                                color = if (isPublished) VendeSuccess else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Link: /p/${publicSlug.ifBlank { "seu-produto" }}",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Button(
                            onClick = { isPublished = !isPublished },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPublished) VendeSuccess else VendePrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isPublished) "Publicada ✓" else "Publicar Página",
                                color = Color(0xFF0B1120),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // PREVIEW TAB (MOBILE SIMULATION)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth()
                        .border(2.dp, VendePrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = VendePrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "⚡ OFERTA EXCLUSIVA DE LANÇAMENTO",
                                color = VendePrimary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = headline,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = subheadline,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Price and Guarantee Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Valor promocional hoje:", color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelMedium)
                                Text(
                                    text = "R$ ${"%.2f".format(selectedProduct?.price ?: 97.0)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = VendeSuccess
                                )
                                Text(text = "ou em até 12x no cartão / Pix à vista", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Benefits
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(text = "O que você vai receber:", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = benefitsText, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bonus Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, VendeSecondary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "🎁 Bônus Especiais:", color = VendeSecondary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = bonusText, color = Color(0xFFE2E8F0), style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // CTA Button
                        Button(
                            onClick = {
                                Toast.makeText(context, "Direcionando para o Checkout Pix...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = ctaButtonText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = VendePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = guaranteeText, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
