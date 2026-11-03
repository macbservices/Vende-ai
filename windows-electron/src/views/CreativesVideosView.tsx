import React, { useState } from 'react';
import { Product } from '../types';
import { GeminiService } from '../services/geminiService';
import { Video, Sparkles, Copy, Check, Loader2 } from 'lucide-react';

interface CreativesVideosViewProps {
  products: Product[];
}

export const CreativesVideosView: React.FC<CreativesVideosViewProps> = ({ products }) => {
  const [selectedProduct, setSelectedProduct] = useState<Product | undefined>(products[0]);
  const [loading, setLoading] = useState(false);
  const [script, setScript] = useState<string>(
    `🎬 ROTEIRO VIRAL REELS / TIKTOK / SHORTS\n\n⏱️ [00:00 - 00:03] GANCHO VISUAL:\n(Texto na tela: "Pare de perder tempo com isso!")\n"Se você ainda está tentando criar infoprodutos manualmente em 2026, você está deixando muito dinheiro na mesa."\n\n⏱️ [00:03 - 00:15] O PROBLEMA:\n(Mostra tela com pilhas de arquivos e telas confusas)\n"Criar títulos, capítulos, capa e página de vendas costumava levar semanas inteiras de dor de cabeça."\n\n⏱️ [00:15 - 00:35] A SOLUÇÃO (VENDEIA + GEMINI):\n(Corta para o app Windows VendeIA gerando um e-book completo com 1 clique)\n"Com o VendeIA, você digita o seu nicho e a IA escreve o livro inteiro, diagrama em PDF e cria o checkout Pix em minutos."\n\n⏱️ [00:35 - 00:45] CHAMADA PARA AÇÃO (CTA):\n"Quer ter acesso a essa ferramenta agora mesmo? Digite 'QUERO' nos comentários ou acesse o link no perfil!"`
  );
  const [copied, setCopied] = useState(false);

  const handleGenerateScript = async () => {
    if (!selectedProduct) return;
    setLoading(true);
    try {
      const prompt = `Crie um roteiro viral para vídeo curto (Reels, TikTok e Shorts) de até 45 segundos para divulgar o infoproduto "${selectedProduct.title}".
Nicho: ${selectedProduct.niche}.
Estruture detalhadamente em:
1. Gancho Visual dos primeiros 3 segundos.
2. Apresentação da dor do público.
3. Demonstração prática do produto/solução.
4. Chamada para Ação (CTA) direta com oferta.
Inclua instruções visuais e de entonação entre parênteses.`;

      const res = await GeminiService.generateText(prompt, 'Você é um diretor criativo de vídeos virais e roteirista profissional para TikTok e Reels.');
      setScript(res);
    } catch (err: any) {
      alert(`Erro: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(script);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <Video className="w-5 h-5 text-purple-400 mr-2" />
            Gerador de Roteiros para Reels & TikTok com Gemini
          </h1>
          <p className="text-xs text-slate-400">Roteiros virais com ganchos de alta retenção e instruções de cena</p>
        </div>

        <button
          onClick={handleGenerateScript}
          disabled={loading || !selectedProduct}
          className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold text-xs shadow transition-all cursor-pointer disabled:opacity-50"
        >
          {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4 text-cyan-300" />}
          <span>Gerar Novo Roteiro com Gemini</span>
        </button>
      </div>

      <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-4">
        <label className="text-xs text-slate-400 block mb-1 font-semibold">Infoproduto Selecionado:</label>
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

      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 relative">
        <div className="flex items-center justify-between border-b border-[#1E293B] pb-3 mb-4">
          <span className="text-xs font-bold text-white uppercase tracking-wider">Roteiro Completo para Gravação</span>
          <button
            onClick={handleCopy}
            className="flex items-center space-x-1.5 px-3 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs text-slate-300 transition-colors"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span>{copied ? 'Copiado!' : 'Copiar Roteiro'}</span>
          </button>
        </div>

        <textarea
          value={script}
          onChange={(e) => setScript(e.target.value)}
          rows={16}
          className="w-full bg-[#0B132B] border border-[#1E293B] rounded-xl p-4 text-xs text-slate-200 leading-relaxed font-mono focus:outline-none focus:border-purple-400"
        />
      </div>
    </div>
  );
};
