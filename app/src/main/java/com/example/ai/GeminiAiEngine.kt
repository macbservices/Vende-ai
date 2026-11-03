package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAiEngine"

class GeminiAiEngine {

    // Lazy initialization as required by Firebase AI Logic
    private val standardModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    private val creativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.85f
            }
        )
    }

    /**
     * Etapa 2 — Análise de Oportunidade e Ideação de Infoproduto
     */
    suspend fun analyzeNicheAndPlan(
        theme: String,
        niche: String,
        targetAudience: String,
        goal: String,
        tone: String,
        chapterCount: Int
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Você é o arquiteto sênior de infoprodutos e estrategista de marketing do VendeIA SaaS.
            Analise a oportunidade de infoproduto abaixo e elabore o plano mestre:
            
            - Tema: $theme
            - Nicho: $niche
            - Público-Alvo: $targetAudience
            - Objetivo do Comprador: $goal
            - Tom de Voz: $tone
            - Quantidade de Capítulos: $chapterCount
            
            Retorne um documento estratégico completo em formato Markdown contendo:
            # 🎯 Análise de Oportunidade & Posicionamento
            ## Sugestões de Título de Alto Impacto
            1. [Título Principal com Promessa Forte]
            2. [Título Alternativo Focado em Rapidez]
            3. [Título Alternativo Focado em Exclusividade]
            
            ## Proposta Única de Valor (UVP)
            ## Principais Dores & Desejos do Público
            ## Diferenciais Competitivos & Oferta Irresistível
            
            # 📚 Estrutura Editorial Recomendada ($chapterCount Capítulos)
            (Liste cada um dos $chapterCount capítulos com título atraente e objetivo de aprendizado detalhado)
            
            # 💰 Estratégia de Precificação & Formato
            Preço sugerido em BRL, formato recomendado e bônus estratégico.
        """.trimIndent()

        try {
            withTimeout(60_000L) {
                val response = standardModel.generateContent(prompt)
                response.text ?: "Erro: Modelo retornou resposta vazia."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Falha na análise de nicho", e)
            throw e
        }
    }

    /**
     * Etapa 4 — Geração Completa de Capítulo em Streaming
     */
    fun generateChapterStream(
        productTitle: String,
        niche: String,
        chapterTitle: String,
        sequence: Int,
        totalChapters: Int,
        tone: String,
        contextSummary: String
    ): Flow<String> = flow {
        val prompt = """
            Você é um autor best-seller e educador especialista no nicho de $niche.
            Escreva o conteúdo integral e profundo do Capítulo $sequence de $totalChapters para o infoproduto:
            
            Título do E-book: $productTitle
            Capítulo: $chapterTitle
            Tom de voz: $tone
            Contexto dos capítulos anteriores: $contextSummary
            
            Diretrizes editoriais obrigatórias:
            1. Linguagem fluida, didática, envolvente e altamente aplicável.
            2. Comece com uma introdução marcante, conectando com a realidade do leitor.
            3. Desenvolva tópicos claros com subtítulos formatados em Markdown (## e ###).
            4. Inclua exemplos práticos, passos de ação e estudos de caso ou analogias.
            5. Finalize com um "Resumo Prático do Capítulo" e uma "Missão de Ação".
            6. Produza um texto rico, detalhado e sem enrolação, com cerca de 600 a 1000 palavras.
        """.trimIndent()

        try {
            withTimeout(90_000L) {
                standardModel.generateContentStream(prompt).collect { chunk ->
                    val text = chunk.text
                    if (!text.isNullOrEmpty()) {
                        emit(text)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Erro no streaming do capítulo $sequence", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Etapa 5 & 7 — Melhorar com Gemini (Editor de E-books)
     */
    suspend fun improveContent(
        selectedText: String,
        actionType: String // "reescrever", "expandir", "resumir", "corrigir", "simplificar", "persuasivo"
    ): String = withContext(Dispatchers.IO) {
        val actionInstruction = when (actionType.lowercase()) {
            "expandir" -> "Expanda o texto aprofundando os conceitos, adicionando exemplos práticos e explicações ricas, mantendo a coesão."
            "resumir" -> "Sintetize o trecho em pontos-chave diretos e concisos, preservando 100% da essência."
            "corrigir" -> "Revise e corrija rigorosamente gramática, ortografia, concordância, pontuação e clareza, mantendo o tom original."
            "simplificar" -> "Reescreva em linguagem simples e acessível, facilitando o entendimento de qualquer leitor sem perder valor."
            "persuasivo" -> "Torne o texto altamente persuasivo, aplicando gatilhos mentais de autoridade, curiosidade e transformação."
            else -> "Reescreva com qualidade editorial superior, melhorando ritmo e clareza."
        }

        val prompt = """
            Você é o revisor e copywriter sênior do VendeIA.
            Tarefa: $actionInstruction
            
            Texto original:
            \"\"\"
            $selectedText
            \"\"\"
            
            Retorne APENAS a versão aprimorada, sem introduções explicativas.
        """.trimIndent()

        try {
            withTimeout(45_000L) {
                val response = standardModel.generateContent(prompt)
                response.text ?: selectedText
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao melhorar texto", e)
            throw e
        }
    }

    /**
     * Etapa 8 — Construtor de Páginas de Vendas (Copywriting Comercial)
     */
    suspend fun generateSalesPageCopy(
        productTitle: String,
        subtitle: String,
        niche: String,
        targetAudience: String,
        price: Double
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Você é o mestre do copywriting de conversão da VendeIA.
            Crie a copy completa da página de vendas para o seguinte infoproduto:
            
            - Título: $productTitle
            - Subtítulo: $subtitle
            - Nicho: $niche
            - Público-Alvo: $targetAudience
            - Preço de Oferta: R$ $price
            
            Gere a estrutura comercial completa em Markdown:
            # 💎 Headline Principal (Chamada Magnética)
            ## Subheadline (Promessa Irresistível)
            
            ## 🛑 O Grande Problema (Identificação das Dores Reais)
            ## 💡 A Descoberta / O Método Revolucionário
            
            ## 📦 O Que Você Vai Aprender (Benefícios em Tópicos)
            - Benefício 1: ...
            - Benefício 2: ...
            - Benefício 3: ...
            - Benefício 4: ...
            
            ## 🎁 Bônus Exclusivos de Lançamento
            - Bônus 1: Checklist de Aplicação
            - Bônus 2: Mapa Mental de Execução
            
            ## 🛡️ Garantia Blindada de 7 Dias
            Texto explicando o risco zero para o comprador.
            
            ## ❓ Perguntas Frequentes (FAQ)
            Q1: Como recebo o material?
            A1: Imediatamente após a confirmação do pagamento por e-mail e na tela de entrega.
            Q2: Funciona para iniciantes?
            A2: Sim, foi desenvolvido passo a passo com didática direta.
            
            ## 🚀 Chamada para Ação (CTA)
            Botão: "Quero Garantir Minha Cópia com Desconto"
        """.trimIndent()

        try {
            withTimeout(60_000L) {
                val response = creativeModel.generateContent(prompt)
                response.text ?: "Erro ao gerar copy da página."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Erro na geração da página de vendas", e)
            throw e
        }
    }

    /**
     * Módulo 7 — Campanhas de Marketing com IA
     */
    suspend fun generateMarketingStrategy(
        productTitle: String,
        niche: String,
        targetAudience: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Elabore uma campanha de divulgação multi-canal para o infoproduto:
            - Produto: $productTitle
            - Nicho: $niche
            - Público: $targetAudience
            
            Gere em Markdown:
            # 📢 Estratégia de Tráfego e Divulgação
            ## 3 Variações de Anúncios para Instagram/Facebook (Hook, Corpo e CTA)
            ## Roteiro de Sequência de E-mails de Vendas (E-mail 1: Curiosidade, E-mail 2: Prova & Conteúdo, E-mail 3: Escassez)
            ## Ideias de Posts em Carrossel para Tráfego Orgânico
            ## Melhores Hashtags e Segmentações de Público Sugeridas
        """.trimIndent()

        try {
            withTimeout(60_000L) {
                val response = creativeModel.generateContent(prompt)
                response.text ?: "Erro ao gerar estratégia de marketing."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Erro na estratégia de marketing", e)
            throw e
        }
    }

    /**
     * Módulo 8 — Roteiros de Vídeo (Reels, TikTok, Shorts)
     */
    suspend fun generateVideoScript(
        productTitle: String,
        format: String,
        topic: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Crie um roteiro viral no formato $format para divulgar o infoproduto "$productTitle".
            Tema específico do vídeo: $topic
            
            Formate em:
            - ⏱️ Duração: 45-60 segundos
            - 🪝 Gancho Inicial (0-3s): Texto falado e ação visual para prender a atenção
            - 🎬 Roteiro Dividido (Cena | O que falar | O que mostrar na tela)
            - 🎯 Chamada para Ação (CTA) Final convidando a comprar o infoproduto
            - 📝 Legenda Pronta para Publicação com Hashtags
        """.trimIndent()

        try {
            withTimeout(45_000L) {
                val response = creativeModel.generateContent(prompt)
                response.text ?: "Erro ao gerar roteiro de vídeo."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Erro no roteiro de vídeo", e)
            throw e
        }
    }

    /**
     * Módulo 13 — Assistente Gemini Integrado (Chat)
     */
    fun chatWithAssistantStream(
        userMessage: String,
        currentContext: String
    ): Flow<String> = flow {
        val systemContextPrompt = """
            Você é o Assistente Gemini Inteligente da plataforma VendeIA SaaS.
            Seu propósito é guiar o usuário em todas as etapas da criação, publicação, automação e venda de infoprodutos.
            
            Contexto atual do usuário no aplicativo:
            $currentContext
            
            Mensagem do usuário:
            $userMessage
            
            Responda de forma prestativa, altamente estratégica, encorajadora e estruturada com formatação clara.
        """.trimIndent()

        try {
            withTimeout(60_000L) {
                standardModel.generateContentStream(systemContextPrompt).collect { chunk ->
                    val chunkText = chunk.text
                    if (!chunkText.isNullOrEmpty()) {
                        emit(chunkText)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Erro no chat do assistente", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)
}
