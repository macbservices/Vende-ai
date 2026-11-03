import React from 'react';
import { ShieldCheck, Check, Users, Server, Database } from 'lucide-react';

export const SaaSAdminView: React.FC = () => {
  const plans = [
    {
      name: 'Starter / Grátis',
      price: 'R$ 0/mês',
      quota: '3 Infoprodutos / mês',
      features: ['Diagramação PDF básica', 'Checkout Pix padrão', 'Suporte por e-mail'],
    },
    {
      name: 'Pro (Mais Popular)',
      price: 'R$ 97/mês',
      quota: '100 Infoprodutos / mês',
      features: ['Gemini 2.5 Flash sem limites', 'Exportação PDF diagramado', 'Checkout Pix + Cartão', 'Automação WhatsApp'],
      popular: true,
    },
    {
      name: 'Enterprise / Agência',
      price: 'R$ 297/mês',
      quota: 'Ilimitado',
      features: ['Múltiplos usuários', 'Domínio próprio nas Landing Pages', 'Webhooks customizados', 'Atendimento prioritário'],
    },
  ];

  const recentUsers = [
    { email: 'marcos.silva@empresa.com', plan: 'PRO', status: 'Ativo', since: 'Hoje' },
    { email: 'fernanda.costa@digital.com', plan: 'ENTERPRISE', status: 'Ativo', since: 'Ontem' },
    { email: 'lucas.almeida@gmail.com', plan: 'FREE', status: 'Ativo', since: '3 dias atrás' },
  ];

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <ShieldCheck className="w-5 h-5 text-cyan-400 mr-2" />
            Painel Administrativo do SaaS
          </h1>
          <p className="text-xs text-slate-400">Gestão de assinaturas, planos de faturamento e infraestrutura Windows/Cloud</p>
        </div>
      </div>

      {/* Plans Pricing Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {plans.map((p) => (
          <div
            key={p.name}
            className={`rounded-2xl p-6 border flex flex-col justify-between ${
              p.popular
                ? 'bg-[#131D31] border-cyan-400 shadow-xl shadow-cyan-500/10'
                : 'bg-[#131D31] border-[#1E293B]'
            }`}
          >
            <div>
              {p.popular && (
                <span className="text-[10px] font-bold text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded-full border border-cyan-500/30 uppercase tracking-wider mb-2 inline-block">
                  Recomendado
                </span>
              )}
              <h3 className="text-base font-bold text-white">{p.name}</h3>
              <div className="text-2xl font-extrabold text-white mt-2">{p.price}</div>
              <p className="text-xs text-slate-400 mt-1">Cota: {p.quota}</p>

              <div className="mt-4 pt-4 border-t border-[#1E293B] space-y-2">
                {p.features.map((f, i) => (
                  <div key={i} className="flex items-center space-x-2 text-xs text-slate-300">
                    <Check className="w-3.5 h-3.5 text-cyan-400 shrink-0" />
                    <span>{f}</span>
                  </div>
                ))}
              </div>
            </div>

            <button
              className={`w-full mt-6 py-2.5 rounded-xl text-xs font-bold transition-colors cursor-pointer ${
                p.popular
                  ? 'bg-blue-600 hover:bg-blue-500 text-white'
                  : 'bg-slate-800 hover:bg-slate-700 text-slate-200'
              }`}
            >
              Configurar Plano
            </button>
          </div>
        ))}
      </div>

      {/* Users and Tenants Table */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2">
            <Users className="w-4 h-4 text-blue-400" />
            <h2 className="text-sm font-bold text-white">Inquilinos e Assinantes Ativos</h2>
          </div>
          <span className="text-xs text-slate-400">Total: 1.420 usuários</span>
        </div>

        <table className="w-full text-left text-xs text-slate-300">
          <thead className="bg-[#0B132B] uppercase text-slate-400 border-b border-[#1E293B]">
            <tr>
              <th className="py-2.5 px-3">E-mail</th>
              <th className="py-2.5 px-3">Plano</th>
              <th className="py-2.5 px-3">Status</th>
              <th className="py-2.5 px-3">Membro Desde</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#1E293B]">
            {recentUsers.map((u, i) => (
              <tr key={i} className="hover:bg-slate-800/30">
                <td className="py-2.5 px-3 font-medium text-white">{u.email}</td>
                <td className="py-2.5 px-3 font-semibold text-cyan-400">{u.plan}</td>
                <td className="py-2.5 px-3 text-emerald-400 font-bold">{u.status}</td>
                <td className="py-2.5 px-3 text-slate-400">{u.since}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
