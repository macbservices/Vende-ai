import React, { useState } from 'react';
import { Product, Chapter } from '../types';
import { GeminiService } from '../services/geminiService';
import { Sparkles, ArrowRight, CheckCircle2, Loader2, BookOpen } from 'lucide-react';

interface CreateProductWizardViewProps {
  onProductCreated: (product: Product) => void;
  onCancel: () => void;
}

export const CreateProductWizardView: React.FC<CreateProductWizardViewProps> = ({ onProductCreated, onCancel }) => {
  const [step, setStep] = useState<number>(1);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>('');

  // Form states
  const [niche, setNiche] = useState<string>('Marketing Digital & Vendas com IA');
  const [analysis, setAnalysis] = useState<any>(null);
  const [selectedTitle, setSelectedTitle] = useState<string>('');
  const [offerPrice, setOfferPrice] = useState<number>(97.00);
  const [chaptersCount, setChaptersCount] = useState<number>(5);
  const [chapters, setChapters] = useState<Chapter[]>([]);

  // Step 1 -> 2: Gemini Niche Analysis
  const handleAnalyzeNiche = async () => {
    if (!niche.trim()) return;
    setLoading(true);
    setError('');
    try {
      const result = await GeminiService.analyzeNiche(niche);
      setAnalysis(result);
      setSelectedTitle(result.suggestedTitles?.[0] || `Manual Completo de ${niche}`);
      setOfferPrice(result.recommendedPrice || 97.00);
      setStep(2);
    } catch (err: any) {
      setError(err.message || 'Falha ao analisar nicho com Gemini.');
    } finally {
      setLoading(false);
    }
  };

  // Step 2 -> 3: Generate Chapters Outline
  const handleGenerateOutline = async () => {
    setLoading(true);
    setError('');
    try {
      const outline = await GeminiService.generateChapterOutline(selectedTitle, niche, chaptersCount);
      const generatedChapters: Chapter[] = outline.map((item: any, idx: number) => ({
        id: `chap-${Date.now()}-${idx}`,
        number: item.number || idx + 1,
        title: item.title,
        summary: item.summary,
        content: '',
        generatedWithAi: true,
      }));
      setChapters(generatedChapters);
      setStep(3);
    } catch (err: any) {
      setError(err.message || 'Falha ao estruturar capítulos.');
    } finally {
      setLoading(false);
    }
  };

  // Step 3 -> 4: Write All Chapters with Gemini
  const handleWriteChapters = async () => {
    setLoading(true);
    setError('');
    setStep(4);
    try {
      const writtenChapters: Chapter[] = [];
      for (const chap of chapters) {
        const content = await GeminiService.generateChapterContent(selectedTitle, chap.title, chap.summary);
        writtenChapters.push({
          ...chap,
          content,
        });
      }
      setChapters(writtenChapters);
      setStep(5);
    } catch (err: any) {
      setError(err.message || 'Falha ao redigir capítulos.');
    } finally {
      setLoading(false);
    }
  };

  // Step 5: Finalize & Save
  const handleFinalize = () => {
    const newProduct: Product = {
      id: `prod-${Date.now()}`,
      userId: 'user-default',
      title: selectedTitle,
      subtitle: analysis?.targetAudience || `Guia Prático e Definitivo sobre ${niche}`,
      niche: niche,
      targetAudience: analysis?.targetAudience || 'Profissionais e entusiastas',
      offerPrice: offerPrice,
      status: 'completed',
      chapters: chapters,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    onProductCreated(newProduct);
  };

  return (
    <div className="p-8 max-w-4xl mx-auto overflow-y-auto max-h-[calc(100vh-40px)] space-y-6">
      {/* Wizard Header */}
      <div className="flex items-center justify-between border-b border-[#1E293B] pb-4">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <Sparkles className="w-5 h-5 text-cyan-400 mr-2" />
            Assistente Criador de Infoprodutos com Gemini
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Etapa {step} de 5: {
              step === 1 ? 'Definição de Nicho e Oportunidade' :
              step === 2 ? 'Análise de Mercado e Oferta' :
              step === 3 ? 'Estruturação do Sumário' :
              step === 4 ? 'Redação dos Capítulos com IA' : 'Revisão e Conclusão'
            }
          </p>
        </div>
        <button
          onClick={onCancel}
          className="text-xs text-slate-400 hover:text-white px-3 py-1.5 rounded-lg border border-[#1E293B] hover:bg-slate-800 transition-colors"
        >
          Cancelar
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-300 text-sm">
          {error}
        </div>
      )}

      {/* Step 1: Input Niche */}
      {step === 1 && (
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-5">
          <div>
            <label className="block text-sm font-semibold text-white mb-1.5">
              Qual é o tema, nicho ou ideia do seu infoproduto?
            </label>
            <input
              type="text"
              value={niche}
              onChange={(e) => setNiche(e.target.value)}
              placeholder="Ex: Emagrecimento Saudável, Produtividade, Marketing Digital..."
              className="w-full bg-[#0B132B] border border-[#1E293B] rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-cyan-400"
            />
          </div>

          <div className="bg-slate-900/60 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-2">
            <span className="font-semibold text-cyan-300 block">Como o Gemini AI atua nesta etapa:</span>
            <p>• O motor avalia a viabilidade comercial e demanda de busca no Brasil.</p>
            <p>• Sugere os melhores títulos com psicologia de copywriting e preço ideal de ticket.</p>
          </div>

          <button
            onClick={handleAnalyzeNiche}
            disabled={loading || !niche.trim()}
            className="w-full flex items-center justify-center space-x-2 py-3 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-sm transition-all disabled:opacity-50 cursor-pointer"
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4 text-cyan-300" />}
            <span>Analisar Oportunidade com Gemini</span>
          </button>
        </div>
      )}

      {/* Step 2: Niche Analysis Results & Title Selection */}
      {step === 2 && analysis && (
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-6">
          <div className="grid grid-cols-3 gap-4">
            <div className="p-4 rounded-xl bg-[#0B132B] border border-[#1E293B]">
              <span className="text-xs text-slate-400">Demanda Estimada</span>
              <div className="text-xl font-bold text-cyan-400 mt-1">{analysis.demandScore || 90}/100</div>
            </div>
            <div className="p-4 rounded-xl bg-[#0B132B] border border-[#1E293B]">
              <span className="text-xs text-slate-400">Concorrência</span>
              <div className="text-xl font-bold text-amber-400 mt-1">{analysis.competitionScore || 60}/100</div>
            </div>
            <div className="p-4 rounded-xl bg-[#0B132B] border border-[#1E293B]">
              <span className="text-xs text-slate-400">Preço Sugerido</span>
              <div className="text-xl font-bold text-emerald-400 mt-1">R$ {analysis.recommendedPrice || 97}</div>
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold text-white mb-2">Escolha o Título do seu Infoproduto:</label>
            <div className="space-y-2">
              {analysis.suggestedTitles?.map((t: string) => (
                <div
                  key={t}
                  onClick={() => setSelectedTitle(t)}
                  className={`p-3.5 rounded-xl border text-sm cursor-pointer transition-all ${
                    selectedTitle === t
                      ? 'bg-blue-600/20 border-cyan-400 text-white font-semibold'
                      : 'bg-[#0B132B] border-[#1E293B] text-slate-300 hover:border-slate-600'
                  }`}
                >
                  {t}
                </div>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Preço de Venda (R$):</label>
              <input
                type="number"
                value={offerPrice}
                onChange={(e) => setOfferPrice(Number(e.target.value))}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-white text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Número de Capítulos:</label>
              <select
                value={chaptersCount}
                onChange={(e) => setChaptersCount(Number(e.target.value))}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-white text-sm"
              >
                <option value={3}>3 Capítulos (Guia Rápido)</option>
                <option value={5}>5 Capítulos (E-book Padrão)</option>
                <option value={8}>8 Capítulos (Livro Completo)</option>
              </select>
            </div>
          </div>

          <button
            onClick={handleGenerateOutline}
            disabled={loading}
            className="w-full flex items-center justify-center space-x-2 py-3 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-sm transition-all cursor-pointer"
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <ArrowRight className="w-4 h-4" />}
            <span>Estruturar Sumário com Gemini</span>
          </button>
        </div>
      )}

      {/* Step 3: Review Chapter Outline */}
      {step === 3 && (
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-6">
          <h2 className="text-base font-bold text-white">Sumário Proposto para o E-book:</h2>

          <div className="space-y-3">
            {chapters.map((chap, idx) => (
              <div key={chap.id} className="p-4 rounded-xl bg-[#0B132B] border border-[#1E293B] flex items-start space-x-3">
                <span className="w-6 h-6 rounded-full bg-blue-600 text-white flex items-center justify-center text-xs font-bold shrink-0">
                  {idx + 1}
                </span>
                <div className="flex-1">
                  <h3 className="text-sm font-semibold text-white">{chap.title}</h3>
                  <p className="text-xs text-slate-400 mt-0.5">{chap.summary}</p>
                </div>
              </div>
            ))}
          </div>

          <button
            onClick={handleWriteChapters}
            disabled={loading}
            className="w-full flex items-center justify-center space-x-2 py-3.5 rounded-xl bg-gradient-to-r from-blue-600 to-cyan-500 hover:from-blue-500 hover:to-cyan-400 text-white font-bold text-sm shadow-lg shadow-blue-600/30 transition-all cursor-pointer"
          >
            <Sparkles className="w-4 h-4" />
            <span>Redigir Todo o E-book Automaticamente com Gemini</span>
          </button>
        </div>
      )}

      {/* Step 4: Writing Progress */}
      {step === 4 && (
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-12 text-center space-y-4">
          <Loader2 className="w-12 h-12 text-cyan-400 animate-spin mx-auto" />
          <h2 className="text-lg font-bold text-white">O Gemini está escrevendo seus capítulos...</h2>
          <p className="text-xs text-slate-400 max-w-md mx-auto">
            Processamento assíncrono em lote: estruturação editorial, introdução persuasiva, conceitos didáticos e exercícios práticos.
          </p>
        </div>
      )}

      {/* Step 5: Completed */}
      {step === 5 && (
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-8 text-center space-y-6">
          <CheckCircle2 className="w-16 h-16 text-emerald-400 mx-auto" />
          <div>
            <h2 className="text-2xl font-bold text-white">E-book Gerado com Sucesso!</h2>
            <p className="text-sm text-slate-300 mt-1">
              "{selectedTitle}" agora possui {chapters.length} capítulos completos prontos para revisão, exportação em PDF e venda.
            </p>
          </div>

          <div className="flex justify-center space-x-4">
            <button
              onClick={handleFinalize}
              className="px-6 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-sm transition-all cursor-pointer shadow-lg shadow-emerald-600/20"
            >
              Abrir no Editor & Salvar
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
