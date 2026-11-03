import React from 'react';
import { Order } from '../types';
import { BarChart3, TrendingUp, Sparkles, DollarSign, PieChart } from 'lucide-react';

interface ReportsViewProps {
  orders: Order[];
}

export const ReportsView: React.FC<ReportsViewProps> = ({ orders }) => {
  const totalRevenue = orders.reduce((sum, o) => sum + (o.status === 'PAID' ? o.amount : 0), 0);
  const totalOrders = orders.length;
  const paidOrders = orders.filter((o) => o.status === 'PAID').length;
  const averageTicket = paidOrders > 0 ? totalRevenue / paidOrders : 0;

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <BarChart3 className="w-5 h-5 text-emerald-400 mr-2" />
            Relatórios Financeiros & Métricas de IA
          </h1>
          <p className="text-xs text-slate-400">Análise de receita, desempenho de vendas e consumo do motor Gemini</p>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-5">
          <span className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Volume Transacionado</span>
          <div className="text-2xl font-bold text-emerald-400 mt-2">
            R$ {totalRevenue.toFixed(2).replace('.', ',')}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">100% liquidação Pix</span>
        </div>

        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-5">
          <span className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Ticket Médio</span>
          <div className="text-2xl font-bold text-cyan-400 mt-2">
            R$ {averageTicket.toFixed(2).replace('.', ',')}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">Por comprador ativo</span>
        </div>

        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-5">
          <span className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Taxa de Conversão</span>
          <div className="text-2xl font-bold text-blue-400 mt-2">
            {totalOrders > 0 ? ((paidOrders / totalOrders) * 100).toFixed(1) : '100'}%
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">Checkouts finalizados</span>
        </div>
      </div>

      {/* Gemini AI Usage Card */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-4">
        <div className="flex items-center space-x-2 text-sm font-bold text-white">
          <Sparkles className="w-4 h-4 text-cyan-400" />
          <span>Consumo de Inteligência Artificial Gemini 2.5 Flash</span>
        </div>

        <div>
          <div className="flex justify-between text-xs text-slate-400 mb-1.5">
            <span>Gerações de Capítulos & Páginas no Ciclo:</span>
            <span className="font-semibold text-white">42 / 100 (42%)</span>
          </div>
          <div className="w-full bg-[#0B132B] h-2.5 rounded-full overflow-hidden border border-[#1E293B]">
            <div className="bg-gradient-to-r from-blue-500 to-cyan-400 h-full rounded-full" style={{ width: '42%' }}></div>
          </div>
          <span className="text-[10px] text-slate-500 mt-1 block">Plano Pro • Renovação mensal com cota dedicada</span>
        </div>
      </div>

      {/* Recent Transactions List */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6">
        <h2 className="text-sm font-bold text-white mb-4">Extrato Detalhado de Vendas</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-[#0B132B] uppercase text-slate-400 border-b border-[#1E293B]">
              <tr>
                <th className="py-2.5 px-3">Data</th>
                <th className="py-2.5 px-3">Cliente</th>
                <th className="py-2.5 px-3">Infoproduto</th>
                <th className="py-2.5 px-3">Método</th>
                <th className="py-2.5 px-3">Valor</th>
                <th className="py-2.5 px-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#1E293B]">
              {orders.map((ord) => (
                <tr key={ord.id} className="hover:bg-slate-800/30">
                  <td className="py-2.5 px-3 text-slate-400">
                    {new Date(ord.createdAt).toLocaleDateString('pt-BR')}
                  </td>
                  <td className="py-2.5 px-3 font-medium text-white">{ord.customerName}</td>
                  <td className="py-2.5 px-3">{ord.productTitle}</td>
                  <td className="py-2.5 px-3 font-semibold text-emerald-400">{ord.paymentMethod}</td>
                  <td className="py-2.5 px-3 font-bold text-white">R$ {ord.amount.toFixed(2)}</td>
                  <td className="py-2.5 px-3">
                    <span className="text-[9px] font-bold px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                      {ord.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
