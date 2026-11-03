import React, { useState } from 'react';
import { Product } from '../types';
import { GeminiService } from '../services/geminiService';
import { Megaphone, Sparkles, Copy, Check, Loader2 } from 'lucide-react';

interface MarketingAiViewProps {
  products: Product[];
}

export const MarketingAiView: React.FC<MarketingAiViewProps> = ({ products }) => {
  const [selectedProduct, setSelectedProduct] = useState<Product | undefined>(products[0]);
  const [channel, setChannel] = useState<'instagram' | 'google' | 'email'>('instagram');
  const [loading, setLoading] = useState(false);
  const [campaignCopy, setCampaignCopy] = useState<string>(
    `🔥 Descubra o Segredo para Alavancar Seus Resultados com Inteligência Artificial!\n\nVocê já parou para pensar quanto tempo perde tentando criar conteúdos e estratégias do zero? O Guia Definitivo traz o método passo a passo testado para quem quer resultados reais no piloto automático.\n\n✅ 100% Prático e Direto ao Ponto\n✅ Acesso Imediato no Pix\n✅ Bônus Exclusivos por Tempo Limitado\n\n👉 Toque no link da bio e garanta seu acesso com 50% de desconto agora mesmo!`
  );
  const [copied, setCopied] = useState(false);

  const handleGenerateCampaign = async () => {
    if (!selectedProduct) return;
    setLoading(true);
    try {
      const channelLabel = channel === 'instagram' ? 'Anúncio de Feed e Carrossel para Instagram/Facebook' :
                           channel === 'google' ? 'Anúncios de Pesquisa Google Ads (Títulos e Descrições)' :
                           'Sequência de 3 E-mails de Lançamento e Nutrição';

      const prompt = `Crie uma campanha de marketing de alta performance para o infoproduto "${selectedProduct.title}".
Nicho: ${selectedProduct.niche}. Preço: R$ ${selectedProduct.offerPrice}.
Formato da campanha: ${channelLabel}.
Inclua ganchos emocionais fortes, quebra de objeções e chamada para ação urgente.`;

      const generated = await GeminiService.generateText(prompt, 'Você é o maior especialista em tráfego pago e marketing de resposta direta do Brasil.');
      setCampaignCopy(generated);
    } catch (err: any) {
      alert(`Erro: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(campaignCopy);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <Megaphone className="w-5 h-5 text-cyan-400 mr-2" />
            Campanhas de Divulgação com Gemini IA
          </h1>
          <p className="text-xs text-slate-400">Geração de copies para Meta Ads, Google Ads e E-mail Marketing</p>
        </div>

        <button
          onClick={handleGenerateCampaign}
          disabled={loading || !selectedProduct}
          className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs shadow transition-all cursor-pointer disabled:opacity-50"
        >
          {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4 text-cyan-300" />}
          <span>Gerar Nova Campanha com Gemini</span>
        </button>
      </div>

      {/* Selectors */}
      <div className="grid grid-cols-2 gap-4">
        <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-4">
          <label className="text-xs text-slate-400 block mb-1 font-semibold">Infoproduto Alvo:</label>
          <select
            value={selectedProduct?.id}
            onChange={(e) => setSelectedProduct(products.find((p) => p.id === e.target.value))}
            className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2 text-xs text-white"
          >
            {products.map((p) => (
              <option key={p.id} value={p.id}>{p.title}</option>
            ))}
          </select>
        </div>

        <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-4">
          <label className="text-xs text-slate-400 block mb-1 font-semibold">Canal de Divulgação:</label>
          <div className="grid grid-cols-3 gap-2">
            {[
              { id: 'instagram', label: 'Meta Ads' },
              { id: 'google', label: 'Google Ads' },
              { id: 'email', label: 'E-mails' },
            ].map((c) => (
              <button
                key={c.id}
                onClick={() => setChannel(c.id as any)}
                className={`py-1.5 rounded-lg text-xs font-semibold ${
                  channel === c.id ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40' : 'bg-[#0B132B] text-slate-400'
                }`}
              >
                {c.label}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Campaign Output Box */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 relative">
        <div className="flex items-center justify-between border-b border-[#1E293B] pb-3 mb-4">
          <span className="text-xs font-bold text-white uppercase tracking-wider">Copy Gerada pelo Gemini</span>
          <button
            onClick={handleCopy}
            className="flex items-center space-x-1.5 px-3 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs text-slate-300 transition-colors"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span>{copied ? 'Copiado!' : 'Copiar Texto'}</span>
          </button>
        </div>

        <textarea
          value={campaignCopy}
          onChange={(e) => setCampaignCopy(e.target.value)}
          rows={14}
          className="w-full bg-[#0B132B] border border-[#1E293B] rounded-xl p-4 text-xs text-slate-200 leading-relaxed font-mono focus:outline-none focus:border-cyan-400"
        />
      </div>
    </div>
  );
};
