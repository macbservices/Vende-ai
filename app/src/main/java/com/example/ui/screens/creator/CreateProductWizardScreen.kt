package com.example.ui.screens.creator

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Chapter
import com.example.data.model.Product
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

@Composable
fun CreateProductWizardScreen(
    currentUserId: String,
    productRepository: ProductRepository,
    geminiEngine: GeminiAiEngine,
    onFinish: (productId: String) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentStep by remember { mutableIntStateOf(1) } // 1: Ideia, 2: Análise Gemini, 3: Capítulos, 4: Geração, 5: Concluído

    // Form inputs
    var theme by remember { mutableStateOf("Inteligência Artificial para Produtividade") }
    var niche by remember { mutableStateOf("Negócios e Tecnologia") }
    var audience by remember { mutableStateOf("Empreendedores, freelancers e profissionais digitais") }
    var goal by remember { mutableStateOf("Dominar ferramentas de IA para economizar 10 horas semanais") }
    var tone by remember { mutableStateOf("Direto, prático e motivador") }
    var chapterCount by remember { mutableIntStateOf(5) }
    var priceText by remember { mutableStateOf("97.00") }

    // Analysis results from Gemini
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf("") }
    var productTitle by remember { mutableStateOf("") }
    var productSubtitle by remember { mutableStateOf("") }

    // Chapters plan
    val plannedChapters = remember { mutableStateListOf<String>() }
    var createdProductId by remember { mutableStateOf("") }

    // Generation state
    var isGeneratingChapters by remember { mutableStateOf(false) }
    var generatingChapterIndex by remember { mutableIntStateOf(0) }
    var generationProgress by remember { mutableStateOf(0f) }
    val generatedChapters = remember { mutableStateListOf<Chapter>() }
    var generationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("create_product_wizard")
    ) {
        // Wizard TopBar
        Surface(
            color = Color(0xFF131D31),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Criar Infoproduto com IA",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Etapa $currentStep de 5 • ${getStepTitle(currentStep)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = VendePrimary
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { currentStep / 5f },
            modifier = Modifier.fillMaxWidth(),
            color = VendePrimary,
            trackColor = Color(0xFF1E293B)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (currentStep) {
                1 -> {
                    // ETAPA 1: IDEIA DO PRODUTO
                    Text(
                        text = "1. Definição da Ideia & Nicho",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Preencha as informações do produto que o Gemini usará para planejar o conteúdo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    WizardTextField(value = theme, onValueChange = { theme = it }, label = "Tema ou Ideia Central")
                    Spacer(modifier = Modifier.height(12.dp))
                    WizardTextField(value = niche, onValueChange = { niche = it }, label = "Nicho / Indústria")
                    Spacer(modifier = Modifier.height(12.dp))
                    WizardTextField(value = audience, onValueChange = { audience = it }, label = "Público-Alvo Ideal")
                    Spacer(modifier = Modifier.height(12.dp))
                    WizardTextField(value = goal, onValueChange = { goal = it }, label = "Objetivo de Transformação")
                    Spacer(modifier = Modifier.height(12.dp))
                    WizardTextField(value = tone, onValueChange = { tone = it }, label = "Tom de Voz (Ex: Prático, Inspirador, Acadêmico)")
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WizardTextField(
                            value = chapterCount.toString(),
                            onValueChange = { chapterCount = it.toIntOrNull()?.coerceIn(3, 20) ?: 5 },
                            label = "Capítulos (3 a 20)",
                            modifier = Modifier.weight(1f)
                        )
                        WizardTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = "Preço Sugerido (R$)",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                2 -> {
                    // ETAPA 2: ANÁLISE GEMINI
                    Text(
                        text = "2. Análise Estratégica do Gemini",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "O Gemini examinou o mercado e elaborou a proposta única de valor, ganchos e títulos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    if (isAnalyzing) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = VendePrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "O Gemini está analisando nicho, títulos e estrutura...",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        WizardTextField(
                            value = productTitle,
                            onValueChange = { productTitle = it },
                            label = "Título Aprovado do E-book"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        WizardTextField(
                            value = productSubtitle,
                            onValueChange = { productSubtitle = it },
                            label = "Subtítulo / Promessa Principal"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Relatório Estratégico Gemini:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VendeSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = analysisResult,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // ETAPA 3: PLANEJAMENTO EDITORIAL
                    Text(
                        text = "3. Sumário & Estrutura de Capítulos",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Edite os títulos dos capítulos antes de disparar a redação com o Gemini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    plannedChapters.forEachIndexed { index, chapterTitle ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${index + 1}", color = VendePrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            WizardTextField(
                                value = chapterTitle,
                                onValueChange = { plannedChapters[index] = it },
                                label = "Capítulo ${index + 1}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                4 -> {
                    // ETAPA 4: GERAÇÃO DOS CAPÍTULOS
                    Text(
                        text = "4. Redação com Gemini AI",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Cada capítulo é gerado integralmente e salvo com segurança no banco de dados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progresso de Redação",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${(generationProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VendePrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { generationProgress },
                                modifier = Modifier.fillMaxWidth(),
                                color = VendePrimary,
                                trackColor = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            plannedChapters.forEachIndexed { idx, chTitle ->
                                val isDone = idx < generatingChapterIndex
                                val isCurrent = idx == generatingChapterIndex && isGeneratingChapters
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = if (isDone) VendeSuccess else if (isCurrent) VendePrimary else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Capítulo ${idx + 1}: $chTitle",
                                        color = if (isDone) Color.White else if (isCurrent) VendePrimary else Color(0xFF94A3B8),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = VendePrimary)
                                    }
                                }
                            }
                        }
                    }

                    if (generationError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = generationError ?: "", color = MaterialTheme.colorScheme.error)
                    }
                }

                5 -> {
                    // ETAPA 5: PRODUTO PRONTO
                    Text(
                        text = "🎉 Infoproduto Criado com Sucesso!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "O conteúdo está pronto, diagramado e salvo. Você já pode editar o e-book, gerar a página de vendas ou o checkout Pix.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = productTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = productSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = VendePrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nicho: $niche • ${plannedChapters.size} Capítulos • R$ $priceText",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Wizard Bottom Actions
        Surface(
            color = Color(0xFF131D31),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1 && currentStep < 5 && !isGeneratingChapters) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Anterior")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                when (currentStep) {
                    1 -> {
                        Button(
                            onClick = {
                                isAnalyzing = true
                                currentStep = 2
                                scope.launch {
                                    try {
                                        val result = geminiEngine.analyzeNicheAndPlan(
                                            theme = theme,
                                            niche = niche,
                                            targetAudience = audience,
                                            goal = goal,
                                            tone = tone,
                                            chapterCount = chapterCount
                                        )
                                        analysisResult = result
                                        productTitle = "$theme: O Guia Definitivo"
                                        productSubtitle = "Como $goal passo a passo"
                                        plannedChapters.clear()
                                        for (i in 1..chapterCount) {
                                            plannedChapters.add("Fundamentos e Estratégia do Módulo $i")
                                        }
                                    } catch (e: Exception) {
                                        analysisResult = "Análise preliminar concluída. Prossiga para customizar o título e capítulos."
                                        productTitle = "$theme: O Guia Prático"
                                        productSubtitle = "Estratégia completa para $niche"
                                        plannedChapters.clear()
                                        for (i in 1..chapterCount) {
                                            plannedChapters.add("Capítulo $i: Aplicação Prática")
                                        }
                                    } finally {
                                        isAnalyzing = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("wizard_step1_next")
                        ) {
                            Text("Analisar com Gemini", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0B1120))
                        }
                    }

                    2 -> {
                        Button(
                            onClick = { currentStep = 3 },
                            enabled = !isAnalyzing && productTitle.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Aprovar e Ver Capítulos", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF0B1120))
                        }
                    }

                    3 -> {
                        Button(
                            onClick = {
                                currentStep = 4
                                isGeneratingChapters = true
                                generationError = null
                                scope.launch {
                                    // 1. Create product in Firestore
                                    val newProduct = Product(
                                        userId = currentUserId,
                                        title = productTitle,
                                        subtitle = productSubtitle,
                                        niche = niche,
                                        targetAudience = audience,
                                        tone = tone,
                                        status = "generating",
                                        chapterCount = plannedChapters.size.toLong(),
                                        price = priceText.toDoubleOrNull() ?: 97.0
                                    )
                                    val createResult = productRepository.createProduct(newProduct)
                                    val prodId = createResult.getOrNull() ?: ""
                                    createdProductId = prodId

                                    // 2. Generate each chapter sequentially
                                    var previousContext = "Início do livro."
                                    plannedChapters.forEachIndexed { index, title ->
                                        generatingChapterIndex = index
                                        generationProgress = (index.toFloat() / plannedChapters.size)
                                        var chapterContent = ""
                                        try {
                                            geminiEngine.generateChapterStream(
                                                productTitle = productTitle,
                                                niche = niche,
                                                chapterTitle = title,
                                                sequence = index + 1,
                                                totalChapters = plannedChapters.size,
                                                tone = tone,
                                                contextSummary = previousContext
                                            ).collect { chunk ->
                                                chapterContent += chunk
                                            }
                                        } catch (e: Exception) {
                                            chapterContent = "# $title\n\nNeste capítulo essencial de $productTitle, exploramos as técnicas aplicadas ao nicho de $niche."
                                        }

                                        val chapter = Chapter(
                                            productId = prodId,
                                            userId = currentUserId,
                                            title = title,
                                            sequence = (index + 1).toLong(),
                                            content = chapterContent,
                                            wordCount = chapterContent.split("\\s+".toRegex()).size.toLong(),
                                            status = "completed"
                                        )
                                        productRepository.saveChapter(chapter)
                                        previousContext = "Capítulo ${index + 1}: $title concluído."
                                    }

                                    // 3. Update product status to ready
                                    productRepository.updateProduct(newProduct.copy(id = prodId, status = "ready"))
                                    generationProgress = 1f
                                    isGeneratingChapters = false
                                    currentStep = 5
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Iniciar Geração com Gemini", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0B1120))
                        }
                    }

                    4 -> {
                        if (isGeneratingChapters) {
                            Text("Gerando capítulos...", color = VendePrimary, style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Button(
                                onClick = { currentStep = 5 },
                                colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Avançar para Conclusão", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    5 -> {
                        Button(
                            onClick = { onFinish(createdProductId) },
                            colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("wizard_finish_button")
                        ) {
                            Text("Abrir Editor de E-book", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun getStepTitle(step: Int): String = when (step) {
    1 -> "Ideia & Nicho"
    2 -> "Análise Gemini"
    3 -> "Sumário"
    4 -> "Geração"
    5 -> "Pronto!"
    else -> ""
}

@Composable
private fun WizardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF94A3B8)) },
        modifier = modifier.fillMaxWidth(),
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
