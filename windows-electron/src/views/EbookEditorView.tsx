import React, { useState } from 'react';
import { Product, Chapter } from '../types';
import { GeminiService } from '../services/geminiService';
import { PdfExporter } from '../services/pdfExporter';
import { Sparkles, Download, Save, Loader2, ArrowLeft } from 'lucide-react';

interface EbookEditorViewProps {
  product: Product;
  onUpdateProduct: (product: Product) => void;
  onBack: () => void;
}

export const EbookEditorView: React.FC<EbookEditorViewProps> = ({ product, onUpdateProduct, onBack }) => {
  const [selectedChapterIndex, setSelectedChapterIndex] = useState<number>(0);
  const [activeChapter, setActiveChapter] = useState<Chapter>(product.chapters[0] || {
    id: 'ch-default',
    number: 1,
    title: 'Introdução',
    summary: '',
    content: '',
    generatedWithAi: false,
  });

  const [aiLoading, setAiLoading] = useState<boolean>(false);
  const [showAiModal, setShowAiModal] = useState<boolean>(false);
  const [aiInstruction, setAiInstruction] = useState<string>('Expandir com exemplos reais e exercícios práticos');

  const handleSelectChapter = (index: number) => {
    setSelectedChapterIndex(index);
    setActiveChapter(product.chapters[index]);
  };

  const handleContentChange = (content: string) => {
    setActiveChapter({ ...activeChapter, content });
  };

  const handleSave = () => {
    const updatedChapters = [...product.chapters];
    updatedChapters[selectedChapterIndex] = activeChapter;
    onUpdateProduct({
      ...product,
      chapters: updatedChapters,
      updatedAt: Date.now(),
    });
    alert('Capítulo salvo com sucesso!');
  };

  const handleAiImprove = async () => {
    setAiLoading(true);
    try {
      const prompt = `Melhore e reescreva o texto do capítulo "${activeChapter.title}" do livro "${product.title}".
Diretriz de melhoria: ${aiInstruction}.
Texto atual:
${activeChapter.content}`;

      const improved = await GeminiService.generateText(prompt, 'Você é um editor sênior de livros e copywriting de alta conversão.');
      setActiveChapter({ ...activeChapter, content: improved });
      setShowAiModal(false);
    } catch (err: any) {
      alert(`Erro: ${err.message}`);
    } finally {
      setAiLoading(false);
    }
  };

  const handleExportPdf = async () => {
    const res = await PdfExporter.exportEbook(product);
    if (res.success) {
      alert(`PDF exportado com sucesso: ${res.path}`);
    } else {
      alert(`Erro ao exportar PDF: ${res.error}`);
    }
  };

  const wordCount = (activeChapter.content || '').trim().split(/\s+/).filter(Boolean).length;

  return (
    <div className="flex flex-col h-[calc(100vh-40px)]">
      {/* Top action bar */}
      <div className="h-14 bg-[#131D31] border-b border-[#1E293B] px-6 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <button
            onClick={onBack}
            className="p-1.5 hover:bg-slate-800 rounded-lg text-slate-400 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <h1 className="text-sm font-bold text-white truncate max-w-md">{product.title}</h1>
            <span className="text-[11px] text-slate-400">Editor de Conteúdo & Diagramação</span>
          </div>
        </div>

        <div className="flex items-center space-x-3">
          <span className="text-xs text-slate-400 mr-2">{wordCount} palavras</span>
          <button
            onClick={() => setShowAiModal(true)}
            className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-cyan-500/20 text-cyan-300 hover:bg-cyan-500/30 text-xs font-semibold border border-cyan-500/30 transition-all cursor-pointer"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>Melhorar com Gemini</span>
          </button>
          <button
            onClick={handleExportPdf}
            className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold shadow transition-all cursor-pointer"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Exportar PDF</span>
          </button>
          <button
            onClick={handleSave}
            className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow transition-all cursor-pointer"
          >
            <Save className="w-3.5 h-3.5" />
            <span>Salvar</span>
          </button>
        </div>
      </div>

      {/* Main editor container */}
      <div className="flex-1 flex overflow-hidden">
        {/* Chapters list sidebar */}
        <div className="w-64 bg-[#0F172A] border-r border-[#1E293B] overflow-y-auto p-3 space-y-1">
          <span className="text-[10px] font-semibold tracking-wider text-slate-500 uppercase px-2 mb-1 block">
            Capítulos do E-book
          </span>
          {product.chapters.map((chap, idx) => (
            <button
              key={chap.id}
              onClick={() => handleSelectChapter(idx)}
              className={`w-full text-left p-2.5 rounded-lg text-xs transition-all ${
                selectedChapterIndex === idx
                  ? 'bg-blue-600 text-white font-semibold shadow'
                  : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <div className="text-[10px] opacity-75">Capítulo {idx + 1}</div>
              <div className="truncate">{chap.title}</div>
            </button>
          ))}
        </div>

        {/* Text Area */}
        <div className="flex-1 bg-[#0B132B] p-8 overflow-y-auto">
          <div className="max-w-3xl mx-auto space-y-4">
            <input
              type="text"
              value={activeChapter.title}
              onChange={(e) => setActiveChapter({ ...activeChapter, title: e.target.value })}
              className="w-full bg-transparent text-2xl font-bold text-white border-b border-[#1E293B] pb-2 focus:outline-none focus:border-cyan-400"
            />
            <textarea
              value={activeChapter.content}
              onChange={(e) => handleContentChange(e.target.value)}
              placeholder="Escreva ou gere o conteúdo deste capítulo..."
              rows={24}
              className="w-full bg-[#131D31] border border-[#1E293B] rounded-xl p-5 text-sm text-slate-200 leading-relaxed focus:outline-none focus:border-cyan-400 font-sans"
            />
          </div>
        </div>
      </div>

      {/* Modal Melhorar com Gemini */}
      {showAiModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl max-w-md w-full p-6 space-y-4">
            <h3 className="text-base font-bold text-white flex items-center">
              <Sparkles className="w-4 h-4 text-cyan-400 mr-2" />
              Melhorar Capítulo com Gemini AI
            </h3>
            <p className="text-xs text-slate-400">
              Escolha uma instrução ou digite como o Gemini deve aprimorar o texto:
            </p>

            <div className="space-y-2">
              {[
                'Expandir com exemplos reais e exercícios práticos',
                'Tornar o tom mais persuasivo e engajador',
                'Simplificar termos técnicos e melhorar clareza',
                'Corrigir gramática e aprimorar coesão textual',
              ].map((opt) => (
                <div
                  key={opt}
                  onClick={() => setAiInstruction(opt)}
                  className={`p-2.5 rounded-lg border text-xs cursor-pointer ${
                    aiInstruction === opt
                      ? 'border-cyan-400 bg-cyan-500/10 text-white font-medium'
                      : 'border-[#1E293B] bg-[#0B132B] text-slate-400 hover:text-white'
                  }`}
                >
                  {opt}
                </div>
              ))}
            </div>

            <textarea
              value={aiInstruction}
              onChange={(e) => setAiInstruction(e.target.value)}
              rows={2}
              className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-xs text-white"
            />

            <div className="flex justify-end space-x-2 pt-2">
              <button
                onClick={() => setShowAiModal(false)}
                className="px-3 py-1.5 rounded-lg text-xs text-slate-400 hover:text-white"
              >
                Cancelar
              </button>
              <button
                onClick={handleAiImprove}
                disabled={aiLoading}
                className="flex items-center space-x-1 px-4 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold disabled:opacity-50"
              >
                {aiLoading && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
                <span>Executar com Gemini</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
