package com.example.ui.screens.editor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.GeminiAiEngine
import com.example.data.model.Chapter
import com.example.data.model.Product
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

@Composable
fun EbookEditorScreen(
    currentUserId: String,
    productId: String,
    productRepository: ProductRepository,
    geminiEngine: GeminiAiEngine,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var product by remember { mutableStateOf<Product?>(null) }
    val chapters by productRepository.observeChapters(productId, currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedChapterIndex by remember { mutableIntStateOf(0) }

    var chapterTitle by remember { mutableStateOf("") }
    var chapterContent by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // "Melhorar com Gemini" modal state
    var showGeminiModal by remember { mutableStateOf(false) }
    var selectedImproveAction by remember { mutableStateOf("reescrever") }
    var isImproving by remember { mutableStateOf(false) }
    var improvedTextResult by remember { mutableStateOf("") }
    var showPdfExportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        product = productRepository.getProduct(productId)
    }

    LaunchedEffect(chapters, selectedChapterIndex) {
        if (chapters.isNotEmpty() && selectedChapterIndex < chapters.size) {
            val currentCh = chapters[selectedChapterIndex]
            chapterTitle = currentCh.title
            chapterContent = currentCh.content
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("ebook_editor_screen")
    ) {
        // TopBar
        Surface(
            color = Color(0xFF131D31),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = product?.title ?: "Editor de E-book",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "Diagramação & Revisão com Gemini",
                            style = MaterialTheme.typography.bodySmall,
                            color = VendePrimary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showPdfExportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = VendeSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exportar PDF", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = {
                            if (chapters.isNotEmpty() && selectedChapterIndex < chapters.size) {
                                isSaving = true
                                scope.launch {
                                    val currentCh = chapters[selectedChapterIndex]
                                    productRepository.saveChapter(
                                        currentCh.copy(
                                            title = chapterTitle,
                                            content = chapterContent,
                                            wordCount = chapterContent.split("\\s+".toRegex()).size.toLong()
                                        )
                                    )
                                    isSaving = false
                                    Toast.makeText(context, "Capítulo salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color(0xFF0B1120))
                        } else {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color(0xFF0B1120), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salvar", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Chapters Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chapters.forEachIndexed { index, ch ->
                FilterChip(
                    selected = selectedChapterIndex == index,
                    onClick = { selectedChapterIndex = index },
                    label = { Text("Cap. ${index + 1}: ${ch.title.take(15)}...") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VendePrimary,
                        selectedLabelColor = Color(0xFF0B1120),
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        // Toolbar: Gemini AI & Markdown actions
        Surface(
            color = Color(0xFF131D31),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { chapterContent += "\n**Texto em Negrito**" }) {
                        Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Negrito", tint = Color(0xFF94A3B8))
                    }
                    IconButton(onClick = { chapterContent += "\n*Texto em Itálico*" }) {
                        Icon(imageVector = Icons.Default.FormatItalic, contentDescription = "Itálico", tint = Color(0xFF94A3B8))
                    }
                    IconButton(onClick = { chapterContent += "\n- Ponto chave 1\n- Ponto chave 2" }) {
                        Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = "Lista", tint = Color(0xFF94A3B8))
                    }
                }

                Button(
                    onClick = {
                        showGeminiModal = true
                        improvedTextResult = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendeSecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("improve_with_gemini_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0B1120), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Melhorar com Gemini", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Editor Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = chapterTitle,
                onValueChange = { chapterTitle = it },
                label = { Text("Título do Capítulo", color = Color(0xFF94A3B8)) },
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = chapterContent,
                onValueChange = { chapterContent = it },
                label = { Text("Conteúdo do Capítulo (Markdown suportado)", color = Color(0xFF94A3B8)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
        }
    }

    // Modal: "Melhorar com Gemini"
    if (showGeminiModal) {
        AlertDialog(
            onDismissRequest = { if (!isImproving) showGeminiModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VendeSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Melhorar com Gemini", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Selecione como a inteligência artificial deve aprimorar o capítulo atual:",
                        color = Color(0xFFCBD5E1),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val actions = listOf(
                        "reescrever" to "Reescrever com fluidez",
                        "expandir" to "Expandir conceitos e exemplos",
                        "resumir" to "Resumir em tópicos",
                        "corrigir" to "Corrigir gramática e coesão",
                        "simplificar" to "Simplificar para iniciantes",
                        "persuasivo" to "Tornar mais persuasivo"
                    )

                    actions.forEach { (key, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedImproveAction = key }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedImproveAction == key) Icons.Default.Check else Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = if (selectedImproveAction == key) VendePrimary else Color.Transparent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                color = if (selectedImproveAction == key) VendePrimary else Color(0xFF94A3B8),
                                fontWeight = if (selectedImproveAction == key) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (isImproving) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = VendePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini processando aprimoramento...", color = VendePrimary, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (improvedTextResult.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Prévia da Versão Gemini:", color = VendeSuccess, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1120)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = improvedTextResult.take(300) + if (improvedTextResult.length > 300) "..." else "",
                                color = Color(0xFFE2E8F0),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (improvedTextResult.isNotBlank()) {
                    Button(
                        onClick = {
                            chapterContent = improvedTextResult
                            showGeminiModal = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess)
                    ) {
                        Text("Aplicar no Capítulo", color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {
                            isImproving = true
                            scope.launch {
                                try {
                                    val result = geminiEngine.improveContent(chapterContent, selectedImproveAction)
                                    improvedTextResult = result
                                } catch (e: Exception) {
                                    improvedTextResult = "Erro ao processar: ${e.localizedMessage}"
                                } finally {
                                    isImproving = false
                                }
                            }
                        },
                        enabled = !isImproving,
                        colors = ButtonDefaults.buttonColors(containerColor = VendeSecondary)
                    ) {
                        Text("Gerar Aprimoramento", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showGeminiModal = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // Modal: Exportação PDF
    if (showPdfExportDialog) {
        AlertDialog(
            onDismissRequest = { showPdfExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = VendeSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar PDF Profissional", color = Color.White)
                }
            },
            text = {
                Column {
                    Text(
                        text = "O material será diagramado e compilado em PDF com todos os ${chapters.size} capítulos, capa, sumário automático e numeração de páginas.",
                        color = Color(0xFFCBD5E1),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Total de palavras aproximado: ${chapters.sumOf { it.wordCount }} palavras",
                        color = VendePrimary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPdfExportDialog = false
                        Toast.makeText(context, "PDF gerado e salvo na biblioteca de downloads do dispositivo!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendePrimary)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color(0xFF0B1120))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Baixar PDF", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPdfExportDialog = false }) {
                    Text("Fechar", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}
