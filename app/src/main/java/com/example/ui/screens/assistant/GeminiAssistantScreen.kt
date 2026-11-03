package com.example.ui.screens.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiEngine
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "user" or "gemini"
    val text: String
)

@Composable
fun GeminiAssistantScreen(
    geminiEngine: GeminiAiEngine
) {
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    var isStreaming by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "gemini",
                text = "Olá! Eu sou o seu Assistente Estratégico Gemini da VendeIA. Como posso acelerar o seu infoproduto hoje? Você pode me pedir para criar temas, refinar roteiros de Reels, avaliar páginas de vendas ou sugerir anúncios."
            )
        )
    }

    val suggestionChips = listOf(
        "Crie um e-book sobre produtividade",
        "Gere 3 ganchos para Instagram Reels",
        "Melhore a headline da minha oferta",
        "Como precificar meu infoproduto no Pix?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("gemini_assistant_screen")
    ) {
        // TopBar
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VendeSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VendeSecondary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Assistente Gemini Integrado",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Inteligência Artificial Nativa VendeIA",
                        style = MaterialTheme.typography.bodySmall,
                        color = VendePrimary
                    )
                }
            }
        }

        // Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestionChips.forEach { chipText ->
                FilterChip(
                    selected = false,
                    onClick = {
                        inputText = chipText
                    },
                    label = { Text(chipText, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFFE2E8F0)
                    )
                )
            }
        }

        // Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(VendeSecondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VendeSecondary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) VendePrimary else Color(0xFF131D31)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(if (isUser) 0.8f else 0.88f)
                            .border(
                                1.dp,
                                if (isUser) Color.Transparent else Color(0xFF1E293B),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = msg.text,
                            color = if (isUser) Color(0xFF0B1120) else Color(0xFFF1F5F9),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isUser) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isStreaming) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = VendeSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini digitando...", color = VendeSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Input bar
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Peça algo ao Gemini...", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VendePrimary,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF0B1120),
                        unfocusedContainerColor = Color(0xFF0B1120)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val query = inputText.trim()
                        if (query.isNotBlank() && !isStreaming) {
                            messages.add(ChatMessage(sender = "user", text = query))
                            inputText = ""
                            isStreaming = true
                            scope.launch {
                                var fullAnswer = ""
                                try {
                                    geminiEngine.chatWithAssistantStream(
                                        userMessage = query,
                                        currentContext = "SaaS de infoprodutos digital com foco em e-books de alta conversão e automação Pix."
                                    ).collect { chunk ->
                                        fullAnswer += chunk
                                    }
                                    messages.add(ChatMessage(sender = "gemini", text = fullAnswer))
                                } catch (e: Exception) {
                                    messages.add(ChatMessage(sender = "gemini", text = "Desculpe, ocorreu uma oscilação na conexão com o Gemini: ${e.localizedMessage}"))
                                } finally {
                                    isStreaming = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(VendePrimary)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar", tint = Color(0xFF0B1120))
                }
            }
        }
    }
}
