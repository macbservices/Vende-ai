import React from 'react';
import { Product, Order } from '../types';
import { Sparkles, DollarSign, BookOpen, ShoppingBag, ArrowUpRight, Download, Edit } from 'lucide-react';
import { PdfExporter } from '../services/pdfExporter';

interface DashboardViewProps {
  products: Product[];
  orders: Order[];
  onNavigate: (tab: any, productId?: string) => void;
}

export const DashboardView: React.FC<DashboardViewProps> = ({ products, orders, onNavigate }) => {
  const totalRevenue = orders.reduce((sum, o) => sum + (o.status === 'PAID' ? o.amount : 0), 0);
  const paidOrdersCount = orders.filter((o) => o.status === 'PAID').length;

  const handleExportPdf = async (product: Product) => {
    const res = await PdfExporter.exportEbook(product);
    if (res.success) {
      alert(`E-book exportado com sucesso: ${res.path}`);
    } else {
      alert(`Falha ao exportar PDF: ${res.error}`);
    }
  };

  return (
    <div className="p-8 space-y-8 overflow-y-auto max-h-[calc(100vh-40px)]">
      {/* Top Banner */}
      <div className="rounded-2xl p-6 bg-gradient-to-r from-blue-900/60 via-slate-900 to-indigo-950 border border-blue-500/20 shadow-xl flex items-center justify-between">
        <div>
          <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-cyan-500/20 text-cyan-300 border border-cyan-500/30">
            Plataforma Windows 64-bit • Gemini 2.5 Flash
          </span>
          <h1 className="text-2xl font-bold text-white mt-2">Painel de Operações VendeIA</h1>
          <p className="text-sm text-slate-300 mt-1 max-w-xl">
            Crie, publique e comercialize infoprodutos completos automatizados com inteligência artificial.
          </p>
        </div>
        <button
          onClick={() => onNavigate('creator')}
          className="flex items-center space-x-2 px-5 py-3 rounded-xl bg-gradient-to-r from-blue-600 to-cyan-500 hover:from-blue-500 hover:to-cyan-400 text-white font-semibold shadow-lg shadow-blue-600/30 transition-all cursor-pointer"
        >
          <Sparkles className="w-5 h-5" />
          <span>Criar com IA</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-5">
        <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Faturamento Total</span>
            <div className="w-9 h-9 rounded-lg bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400">
              <DollarSign className="w-5 h-5" />
            </div>
          </div>
          <div className="text-2xl font-bold text-white mt-3">
            R$ {totalRevenue.toFixed(2).replace('.', ',')}
          </div>
          <span className="text-xs text-emerald-400 flex items-center mt-1">
            <ArrowUpRight className="w-3.5 h-3.5 mr-1" /> Pix & Cartão ativo
          </span>
        </div>

        <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Infoprodutos Prontos</span>
            <div className="w-9 h-9 rounded-lg bg-blue-500/10 border border-blue-500/20 flex items-center justify-center text-blue-400">
              <BookOpen className="w-5 h-5" />
            </div>
          </div>
          <div className="text-2xl font-bold text-white mt-3">{products.length}</div>
          <span className="text-xs text-slate-400 mt-1 block">E-books e Guias digitais</span>
        </div>

        <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Vendas Confirmadas</span>
            <div className="w-9 h-9 rounded-lg bg-purple-500/10 border border-purple-500/20 flex items-center justify-center text-purple-400">
              <ShoppingBag className="w-5 h-5" />
            </div>
          </div>
          <div className="text-2xl font-bold text-white mt-3">{paidOrdersCount}</div>
          <span className="text-xs text-slate-400 mt-1 block">Entregas automáticas realizadas</span>
        </div>

        <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Conversão de Checkout</span>
            <div className="w-9 h-9 rounded-lg bg-cyan-500/10 border border-cyan-500/20 flex items-center justify-center text-cyan-400">
              <Sparkles className="w-5 h-5" />
            </div>
          </div>
          <div className="text-2xl font-bold text-white mt-3">68.4%</div>
          <span className="text-xs text-cyan-400 mt-1 block">Otimizado com Pix Instantâneo</span>
        </div>
      </div>

      {/* Recent Infoproducts Table */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-xl p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-lg font-bold text-white">Infoprodutos Recentes</h2>
            <p className="text-xs text-slate-400">Gerencie títulos, capítulos e exportações para PDF</p>
          </div>
          <button
            onClick={() => onNavigate('products')}
            className="text-xs text-blue-400 hover:text-blue-300 font-semibold cursor-pointer"
          >
            Ver todos ({products.length})
          </button>
        </div>

        {products.length === 0 ? (
          <div className="text-center py-12 border border-dashed border-[#1E293B] rounded-xl">
            <BookOpen className="w-10 h-10 text-slate-600 mx-auto mb-2" />
            <p className="text-sm text-slate-400">Nenhum infoproduto criado ainda.</p>
            <button
              onClick={() => onNavigate('creator')}
              className="mt-3 text-xs bg-blue-600 hover:bg-blue-500 text-white font-semibold px-4 py-2 rounded-lg cursor-pointer"
            >
              Criar Primeiro E-book com IA
            </button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-[#0B132B] text-xs uppercase text-slate-400 border-b border-[#1E293B]">
                <tr>
                  <th className="py-3 px-4">Título</th>
                  <th className="py-3 px-4">Nicho</th>
                  <th className="py-3 px-4">Capítulos</th>
                  <th className="py-3 px-4">Preço Oferta</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#1E293B]">
                {products.slice(0, 5).map((prod) => (
                  <tr key={prod.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-4 font-medium text-white">{prod.title}</td>
                    <td className="py-3 px-4 text-xs text-cyan-300">{prod.nicho || prod.niche}</td>
                    <td className="py-3 px-4 text-xs">{prod.chapters?.length || 0} capítulos</td>
                    <td className="py-3 px-4 font-semibold text-emerald-400">R$ {prod.offerPrice?.toFixed(2)}</td>
                    <td className="py-3 px-4">
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                        {prod.status.toUpperCase()}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right space-x-2">
                      <button
                        onClick={() => onNavigate('editor', prod.id)}
                        className="p-1.5 hover:bg-blue-600/20 text-blue-400 rounded transition-colors"
                        title="Editar Capítulos"
                      >
                        <Edit className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleExportPdf(prod)}
                        className="p-1.5 hover:bg-emerald-600/20 text-emerald-400 rounded transition-colors"
                        title="Exportar PDF Profissional"
                      >
                        <Download className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
