import React, { useState } from 'react';
import { Product } from '../types';
import { BookOpen, Download, Edit3, Globe, Plus, Search } from 'lucide-react';
import { PdfExporter } from '../services/pdfExporter';

interface ProductListViewProps {
  products: Product[];
  onNavigate: (tab: any, productId?: string) => void;
}

export const ProductListView: React.FC<ProductListViewProps> = ({ products, onNavigate }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredProducts = products.filter((p) =>
    p.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.niche.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleExportPdf = async (product: Product) => {
    const res = await PdfExporter.exportEbook(product);
    if (res.success) {
      alert(`PDF salvo com sucesso: ${res.path}`);
    } else {
      alert(`Erro: ${res.error}`);
    }
  };

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white">Meus Infoprodutos</h1>
          <p className="text-xs text-slate-400">Biblioteca digital de e-books, manuais e guias gerados</p>
        </div>
        <button
          onClick={() => onNavigate('creator')}
          className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs shadow-md transition-all cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Novo Infoproduto</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="relative">
        <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-3" />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Buscar por título ou nicho..."
          className="w-full bg-[#131D31] border border-[#1E293B] rounded-xl pl-10 pr-4 py-2.5 text-white text-xs focus:outline-none focus:border-cyan-400"
        />
      </div>

      {/* Product Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {filteredProducts.map((prod) => (
          <div key={prod.id} className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-5 flex flex-col justify-between hover:border-slate-700 transition-all shadow-sm">
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                  {prod.niche}
                </span>
                <span className="text-sm font-bold text-emerald-400">
                  R$ {prod.offerPrice?.toFixed(2)}
                </span>
              </div>
              <h3 className="text-base font-bold text-white line-clamp-2">{prod.title}</h3>
              <p className="text-xs text-slate-400 mt-1 line-clamp-2">{prod.subtitle}</p>

              <div className="mt-4 pt-3 border-t border-[#1E293B] flex items-center justify-between text-xs text-slate-400">
                <span>{prod.chapters?.length || 0} capítulos</span>
                <span className="text-emerald-400">Pronto para venda</span>
              </div>
            </div>

            <div className="mt-5 grid grid-cols-3 gap-2">
              <button
                onClick={() => onNavigate('editor', prod.id)}
                className="flex items-center justify-center space-x-1 py-2 px-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium transition-colors"
                title="Editar Texto"
              >
                <Edit3 className="w-3.5 h-3.5" />
                <span>Editar</span>
              </button>
              <button
                onClick={() => handleExportPdf(prod)}
                className="flex items-center justify-center space-x-1 py-2 px-2 rounded-lg bg-emerald-600/20 hover:bg-emerald-600/30 text-emerald-400 text-xs font-medium transition-colors"
                title="Exportar PDF"
              >
                <Download className="w-3.5 h-3.5" />
                <span>PDF</span>
              </button>
              <button
                onClick={() => onNavigate('landing', prod.id)}
                className="flex items-center justify-center space-x-1 py-2 px-2 rounded-lg bg-blue-600/20 hover:bg-blue-600/30 text-blue-400 text-xs font-medium transition-colors"
                title="Criar Página de Vendas"
              >
                <Globe className="w-3.5 h-3.5" />
                <span>Página</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
