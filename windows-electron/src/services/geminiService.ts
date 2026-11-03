export class GeminiService {
  private static apiKey: string = '';

  public static setApiKey(key: string) {
    this.apiKey = key;
    localStorage.setItem('vendeia_gemini_key', key);
  }

  public static getApiKey(): string {
    if (!this.apiKey) {
      this.apiKey = localStorage.getItem('vendeia_gemini_key') || '';
    }
    return this.apiKey;
  }

  public static async generateText(prompt: string, systemInstruction?: string): Promise<string> {
    // If running inside Electron, call through secure Node backend
    if (window.electronAPI) {
      const res = await window.electronAPI.callGemini({
        prompt,
        systemInstruction,
        apiKey: this.getApiKey(),
      });
      if (!res.success) {
        throw new Error(res.error || 'Erro na resposta do Gemini');
      }
      return res.text || '';
    }

    // Direct fetch fallback for web preview
    const key = this.getApiKey();
    if (!key) {
      throw new Error('Configure a chave da API do Gemini nas configurações.');
    }

    const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${key}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        systemInstruction: systemInstruction ? { parts: [{ text: systemInstruction }] } : undefined,
      }),
    });

    if (!response.ok) {
      throw new Error(`Falha na API Gemini: ${response.statusText}`);
    }

    const data = await response.json();
    return data.candidates?.[0]?.content?.parts?.[0]?.text || '';
  }

  public static async analyzeNiche(niche: string) {
    const prompt = `Analise o nicho "${niche}" para infoprodutos digitais de alta conversão no mercado brasileiro.
Retorne um JSON com a seguinte estrutura:
{
  "demandScore": 88,
  "competitionScore": 65,
  "recommendedPrice": 97.00,
  "suggestedTitles": ["Título 1", "Título 2", "Título 3"],
  "targetAudience": "Descrição do público-alvo ideal",
  "keyPainPoints": ["Dor 1", "Dor 2", "Dor 3"],
  "keyDesires": ["Desejo 1", "Desejo 2", "Desejo 3"]
}
Retorne estritamente o JSON sem blocos markdown.`;

    const text = await this.generateText(prompt, 'Você é um estrategista sênior de marketing digital e infoprodutos.');
    try {
      const cleaned = text.replace(/```json/g, '').replace(/```/g, '').trim();
      return JSON.parse(cleaned);
    } catch {
      return {
        demandScore: 85,
        competitionScore: 60,
        recommendedPrice: 97.0,
        suggestedTitles: [`Manual Completo: ${niche}`, `O Guia Definitivo de ${niche}`, `Do Zero ao Sucesso em ${niche}`],
        targetAudience: `Pessoas interessadas em dominar ${niche} de forma prática e rápida.`,
        keyPainPoints: ['Falta de tempo', 'Excesso de informações confusas', 'Dificuldade em ter resultados consistentes'],
        keyDesires: ['Aprender passo a passo', 'Economizar tempo e dinheiro', 'Conquistar autonomia'],
      };
    }
  }

  public static async generateChapterOutline(title: string, niche: string, chaptersCount: number = 5) {
    const prompt = `Crie a estrutura de sumário com exatamente ${chaptersCount} capítulos para o e-book intitulado "${title}" no nicho de "${niche}".
Retorne estritamente um JSON com a lista de capítulos:
[
  { "number": 1, "title": "Introdução e Fundamentos", "summary": "Resumo do que será coberto." },
  { "number": 2, "title": "Passo a Passo Prático", "summary": "Resumo da execução." }
]`;

    const text = await this.generateText(prompt, 'Você é um autor best-seller e especialista em estruturação editorial.');
    try {
      const cleaned = text.replace(/```json/g, '').replace(/```/g, '').trim();
      return JSON.parse(cleaned);
    } catch {
      return Array.from({ length: chaptersCount }, (_, i) => ({
        number: i + 1,
        title: `Capítulo ${i + 1}: Domínio Prático de ${niche}`,
        summary: `Estratégias detalhadas e exercícios aplicados para a etapa ${i + 1}.`,
      }));
    }
  }

  public static async generateChapterContent(bookTitle: string, chapterTitle: string, summary: string) {
    const prompt = `Escreva o conteúdo integral e detalhado do capítulo "${chapterTitle}" do livro "${bookTitle}".
Contexto/Objetivo do capítulo: ${summary}.
Inclua introdução envolvente, conceitos essenciais explicados de forma clara, exemplos reais, passo a passo prático e exercícios acionáveis no final.`;

    return await this.generateText(prompt, 'Você é um autor premiado e redator experiente. Escreva em português claro, cativante e de alta autoridade.');
  }
}
