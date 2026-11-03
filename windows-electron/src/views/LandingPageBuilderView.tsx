import React, { useState } from 'react';
import { Product, LandingPage } from '../types';
import { GeminiService } from '../services/geminiService';
import { Sparkles, Globe, Smartphone, Monitor, Check, ExternalLink, Loader2 } from 'lucide-react';

interface LandingPageBuilderViewProps {
  product: Product;
  onSaveLandingPage: (page: LandingPage) => void;
}

export const LandingPageBuilderView: React.FC<LandingPageBuilderViewProps> = ({ product, onSaveLandingPage }) => {
  const [loading, setLoading] = useState<boolean>(false);
  const [devicePreview, setDevicePreview] = useState<'desktop' | 'mobile'>('desktop');
  const [isPublished, setIsPublished] = useState<boolean>(true);

  const [landingPage, setLandingPage] = useState<LandingPage>({
    id: `lp-${product.id}`,
    productId: product.id,
    headline: `Descubra Como Dominar ${product.niche} em Tempo Recorde`,
    subheadline: `O método passo a passo testado e validado para alcançar resultados concretos sem perder tempo.`,
    painPoints: [
      'Cansado de métodos teóricos que não trazem resultados práticos?',
      'Perdendo horas tentando descobrir sozinho por onde começar?',
      'Frustrado com materiais desatualizados e superficiais?',
    ],
    benefits: [
      'Acesso imediato ao material completo em formato digital.',
      'Roteiro acionável com exercícios para aplicar hoje mesmo.',
      'Bônus exclusivo com modelos e templates prontos para uso.',
    ],
    bonuses: [
      'Bônus 1: Checklist de Implementação Acelerada',
      'Bônus 2: Mapa Mental de Estratégias Avançadas',
    ],
    testimonials: [
      { name: 'Carlos Eduardo', text: 'Esse material mudou totalmente meus resultados em apenas duas semanas!', role: 'Empreendedor' },
      { name: 'Mariana Silva', text: 'Direto ao ponto, sem enrolação. Valeu cada centavo investido.', role: 'Profissional Autônoma' },
    ],
    faq: [
      { question: 'Como recebo o acesso?', answer: 'Imediatamente após a confirmação do pagamento via Pix ou Cartão, enviado por e-mail e WhatsApp.' },
      { question: 'Possui garantia?', answer: 'Sim, você tem 7 dias de garantia incondicional de reembolso.' },
    ],
    ctaText: `Quero Garantir Meu Acesso por Apenas R$ ${product.offerPrice?.toFixed(2)}`,
    slug: product.title.toLowerCase().replace(/[^a-z0-9]/g, '-'),
    published: true,
  });

  const handleGenerateWithAi = async () => {
    setLoading(true);
    try {
      const prompt = `Gere os elementos de uma página de vendas de altíssima conversão para o infoproduto "${product.title}" no nicho de "${product.niche}" com preço de R$ ${product.offerPrice}.
Retorne estritamente um JSON com:
{
  "headline": "Headline persuasiva com gatilho mental",
  "subheadline": "Subheadline detalhando a transformação",
  "painPoints": ["Dor 1", "Dor 2", "Dor 3"],
  "benefits": ["Benefício 1", "Benefício 2", "Benefício 3"],
  "bonuses": ["Bônus 1", "Bônus 2"],
  "ctaText": "Texto chamativo para o botão de compra"
}`;

      const text = await GeminiService.generateText(prompt, 'Você é o maior especialista em copywriting e páginas de vendas diretas do Brasil.');
      const cleaned = text.replace(/```json/g, '').replace(/```/g, '').trim();
      const parsed = JSON.parse(cleaned);

      setLandingPage((prev) => ({
        ...prev,
        headline: parsed.headline || prev.headline,
        subheadline: parsed.subheadline || prev.subheadline,
        painPoints: parsed.painPoints || prev.painPoints,
        benefits: parsed.benefits || prev.benefits,
        bonuses: parsed.bonuses || prev.bonuses,
        ctaText: parsed.ctaText || prev.ctaText,
      }));
    } catch (err: any) {
      alert(`Falha ao gerar copy: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = () => {
    onSaveLandingPage({ ...landingPage, published: isPublished });
    alert('Página de vendas salva e publicada com sucesso!');
  };

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <Globe className="w-5 h-5 text-blue-400 mr-2" />
            Construtor de Páginas de Vendas com Gemini
          </h1>
          <p className="text-xs text-slate-400">Páginas de alta conversão responsivas publicadas automaticamente</p>
        </div>

        <div className="flex items-center space-x-3">
          {/* Device toggle */}
          <div className="flex bg-[#131D31] border border-[#1E293B] rounded-lg p-1">
            <button
              onClick={() => setDevicePreview('desktop')}
              className={`p-1.5 rounded ${devicePreview === 'desktop' ? 'bg-blue-600 text-white' : 'text-slate-400'}`}
              title="Pré-visualização Desktop"
            >
              <Monitor className="w-4 h-4" />
            </button>
            <button
              onClick={() => setDevicePreview('mobile')}
              className={`p-1.5 rounded ${devicePreview === 'mobile' ? 'bg-blue-600 text-white' : 'text-slate-400'}`}
              title="Pré-visualização Mobile"
            >
              <Smartphone className="w-4 h-4" />
            </button>
          </div>

          <button
            onClick={handleGenerateWithAi}
            disabled={loading}
            className="flex items-center space-x-1.5 px-3.5 py-2 rounded-xl bg-cyan-500/20 text-cyan-300 hover:bg-cyan-500/30 text-xs font-semibold border border-cyan-500/30 transition-all cursor-pointer"
          >
            {loading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Sparkles className="w-3.5 h-3.5" />}
            <span>Regerar Copy com Gemini</span>
          </button>

          <button
            onClick={handleSave}
            className="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow transition-all cursor-pointer"
          >
            Publicar Página
          </button>
        </div>
      </div>

      {/* URL slug & Live Indicator */}
      <div className="p-3.5 bg-[#131D31] border border-[#1E293B] rounded-xl flex items-center justify-between text-xs text-slate-300">
        <div className="flex items-center space-x-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <span>URL Pública:</span>
          <span className="font-mono text-cyan-400 bg-slate-900 px-2 py-0.5 rounded border border-slate-800">
            https://vendeia.com/p/{landingPage.slug}
          </span>
        </div>
        <button
          onClick={() => window.electronAPI?.openExternal(`https://vendeia.com/p/${landingPage.slug}`)}
          className="flex items-center text-blue-400 hover:underline"
        >
          <ExternalLink className="w-3.5 h-3.5 mr-1" /> Testar no Navegador
        </button>
      </div>

      {/* Live Preview Container */}
      <div className="flex justify-center bg-[#070D1E] border border-[#1E293B] rounded-2xl p-6 min-h-[500px]">
        <div
          className={`bg-white text-slate-900 rounded-2xl shadow-2xl overflow-y-auto transition-all ${
            devicePreview === 'mobile' ? 'w-[375px] border-8 border-slate-800' : 'w-full max-w-4xl'
          }`}
        >
          {/* Landing page body */}
          <div className="p-8 text-center space-y-6">
            <span className="text-xs font-bold uppercase tracking-wider text-blue-600 bg-blue-50 px-3 py-1 rounded-full">
              {product.niche}
            </span>

            <h2 className="text-2xl md:text-3xl font-extrabold text-slate-900 leading-tight">
              {landingPage.headline}
            </h2>

            <p className="text-sm md:text-base text-slate-600 max-w-2xl mx-auto">
              {landingPage.subheadline}
            </p>

            {/* CTA Button */}
            <div className="pt-2">
              <button className="px-6 py-4 rounded-xl bg-gradient-to-r from-emerald-500 to-green-600 text-white font-bold text-base shadow-xl hover:scale-105 transition-transform cursor-pointer">
                {landingPage.ctaText}
              </button>
              <div className="text-[11px] text-slate-500 mt-2">Pagamento Seguro com Pix Instantâneo • Acesso Imediato</div>
            </div>

            {/* Pain Points */}
            <div className="py-6 text-left max-w-lg mx-auto space-y-3">
              <h3 className="font-bold text-slate-900 text-sm">Você se identifica com algum destes desafios?</h3>
              {landingPage.painPoints.map((pain, i) => (
                <div key={i} className="flex items-start space-x-2 text-xs text-red-700 bg-red-50 p-2.5 rounded-lg border border-red-100">
                  <span className="font-bold">✕</span>
                  <span>{pain}</span>
                </div>
              ))}
            </div>

            {/* Benefits */}
            <div className="py-4 text-left max-w-lg mx-auto space-y-3">
              <h3 className="font-bold text-slate-900 text-sm">O que você vai dominar neste material:</h3>
              {landingPage.benefits.map((b, i) => (
                <div key={i} className="flex items-start space-x-2 text-xs text-slate-700 bg-emerald-50 p-2.5 rounded-lg border border-emerald-100">
                  <Check className="w-4 h-4 text-emerald-600 shrink-0" />
                  <span>{b}</span>
                </div>
              ))}
            </div>

            {/* Pricing Box */}
            <div className="p-6 rounded-2xl bg-slate-900 text-white max-w-sm mx-auto shadow-lg space-y-3">
              <span className="text-xs text-cyan-400 font-semibold uppercase">Oferta por Tempo Limitado</span>
              <div className="text-3xl font-extrabold text-white">
                R$ {product.offerPrice?.toFixed(2)}
              </div>
              <p className="text-xs text-slate-400">Acesso vitalício + Atualizações inclusas</p>
              <button className="w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-white font-bold text-sm">
                COMPRAR AGORA
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
