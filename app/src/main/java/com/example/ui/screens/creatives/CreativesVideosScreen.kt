package com.example.ui.screens.creatives

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.GeminiAiEngine
import com.example.data.model.Product
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

@Composable
fun CreativesVideosScreen(
    currentUserId: String,
    productRepository: ProductRepository,
    geminiEngine: GeminiAiEngine
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val products by productRepository.observeProducts(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedFormat by remember { mutableStateOf("Instagram Reels") }
    var videoTopic by remember { mutableStateOf("Os 3 maiores erros que você comete ao começar") }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedScript by remember { mutableStateOf("") }

    LaunchedEffect(products) {
        if (products.isNotEmpty() && selectedProduct == null) {
            selectedProduct = products.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("creatives_videos_screen")
    ) {
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Criativos & Roteiros de Vídeo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Roteiros para Reels, TikTok, Shorts e Storyboards Comerciais",
                    style = MaterialTheme.typography.bodySmall,
                    color = VendePrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Configuration card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Formato do Vídeo Viral:", color = Color.White, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Instagram Reels", "TikTok Viral", "YouTube Shorts").forEach { format ->
                            FilterChip(
                                selected = selectedFormat == format,
                                onClick = { selectedFormat = format },
                                label = { Text(format) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VendePrimary,
                                    selectedLabelColor = Color(0xFF0B1120),
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = videoTopic,
                        onValueChange = { videoTopic = it },
                        label = { Text("Tema ou Gancho do Vídeo", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VendePrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0B1120),
                            unfocusedContainerColor = Color(0xFF0B1120)
                        )
                    )

                    Button(
                        onClick = {
                            val prod = selectedProduct
                            isGenerating = true
                            scope.launch {
                                try {
                                    generatedScript = geminiEngine.generateVideoScript(
                                        productTitle = prod?.title ?: "Meu Infoproduto",
                                        format = selectedFormat,
                                        topic = videoTopic
                                    )
                                    Toast.makeText(context, "Roteiro gerado com sucesso!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    generatedScript = "Erro na IA: ${e.localizedMessage}"
                                } finally {
                                    isGenerating = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF0B1120))
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0B1120), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gerar Roteiro e Storyboard com Gemini", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (generatedScript.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, VendeSecondary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = VendeSecondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Roteiro & Direção de Cena", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(generatedScript))
                                Toast.makeText(context, "Roteiro copiado!", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = VendePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = generatedScript,
                            color = Color(0xFFE2E8F0),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
