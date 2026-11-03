import React, { useState } from 'react';
import { Lead } from '../types';
import { Users, Plus, ArrowRight, Phone, Mail, CheckCircle } from 'lucide-react';

interface CrmLeadsViewProps {
  leads: Lead[];
  onUpdateLeads: (leads: Lead[]) => void;
}

export const CrmLeadsView: React.FC<CrmLeadsViewProps> = ({ leads, onUpdateLeads }) => {
  const [showAddModal, setShowAddModal] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');

  const stages: Array<{ id: Lead['stage']; label: string; color: string }> = [
    { id: 'NOVO', label: 'Novos Contatos', color: 'border-blue-500' },
    { id: 'INTERESSADO', label: 'Interessados', color: 'border-cyan-500' },
    { id: 'CHECKOUT', label: 'Checkout Aberto', color: 'border-amber-500' },
    { id: 'CLIENTE', label: 'Clientes (Comprou)', color: 'border-emerald-500' },
  ];

  const handleAdvanceStage = (leadId: string) => {
    const stageOrder: Lead['stage'][] = ['NOVO', 'INTERESSADO', 'CHECKOUT', 'CLIENTE'];
    const updated = leads.map((l) => {
      if (l.id === leadId) {
        const currentIndex = stageOrder.indexOf(l.stage);
        const nextStage = stageOrder[Math.min(currentIndex + 1, stageOrder.length - 1)];
        return { ...l, stage: nextStage };
      }
      return l;
    });
    onUpdateLeads(updated);
  };

  const handleAddLead = () => {
    if (!name.trim()) return;
    const newLead: Lead = {
      id: `lead-${Date.now()}`,
      name,
      email,
      phone,
      stage: 'NOVO',
      notes: 'Capturado pelo formulário da Landing Page',
      createdAt: Date.now(),
    };
    onUpdateLeads([newLead, ...leads]);
    setName('');
    setEmail('');
    setPhone('');
    setShowAddModal(false);
  };

  return (
    <div className="p-8 space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <Users className="w-5 h-5 text-blue-400 mr-2" />
            CRM & Funil de Leads de Infoprodutos
          </h1>
          <p className="text-xs text-slate-400">Gerencie contatos, estágios de decisão e recuperação de vendas</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center space-x-1.5 px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs shadow transition-all cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          <span>Novo Lead</span>
        </button>
      </div>

      {/* Kanban Board Columns */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {stages.map((stage) => {
          const stageLeads = leads.filter((l) => l.stage === stage.id);

          return (
            <div key={stage.id} className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-4 flex flex-col h-[560px]">
              {/* Column Header */}
              <div className={`border-b-2 ${stage.color} pb-2.5 mb-3 flex items-center justify-between`}>
                <span className="text-xs font-bold text-white">{stage.label}</span>
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-800 text-slate-300">
                  {stageLeads.length}
                </span>
              </div>

              {/* Leads Cards */}
              <div className="flex-1 overflow-y-auto space-y-2.5 pr-1">
                {stageLeads.map((lead) => (
                  <div key={lead.id} className="bg-[#0B132B] border border-[#1E293B] rounded-xl p-3.5 space-y-2 shadow-sm hover:border-slate-600 transition-colors">
                    <div className="text-xs font-bold text-white">{lead.name}</div>
                    <div className="text-[11px] text-slate-400 space-y-0.5">
                      <div className="flex items-center"><Mail className="w-3 h-3 mr-1 text-slate-500" /> {lead.email}</div>
                      <div className="flex items-center"><Phone className="w-3 h-3 mr-1 text-slate-500" /> {lead.phone}</div>
                    </div>
                    <div className="pt-2 border-t border-[#1E293B] flex items-center justify-between">
                      <span className="text-[9px] text-slate-500">{lead.notes}</span>
                      {lead.stage !== 'CLIENTE' ? (
                        <button
                          onClick={() => handleAdvanceStage(lead.id)}
                          className="flex items-center text-[10px] text-cyan-400 hover:text-cyan-300 font-semibold cursor-pointer"
                          title="Avançar no funil"
                        >
                          <span>Avançar</span>
                          <ArrowRight className="w-3 h-3 ml-0.5" />
                        </button>
                      ) : (
                        <span className="flex items-center text-[10px] text-emerald-400 font-semibold">
                          <CheckCircle className="w-3 h-3 mr-1" /> Venda Feita
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>

      {/* Add Lead Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl max-w-sm w-full p-6 space-y-4">
            <h3 className="text-sm font-bold text-white">Cadastrar Novo Lead</h3>
            <div>
              <label className="text-xs text-slate-400 block mb-1">Nome:</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2 text-xs text-white"
              />
            </div>
            <div>
              <label className="text-xs text-slate-400 block mb-1">E-mail:</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2 text-xs text-white"
              />
            </div>
            <div>
              <label className="text-xs text-slate-400 block mb-1">WhatsApp:</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2 text-xs text-white"
              />
            </div>
            <div className="flex justify-end space-x-2 pt-2">
              <button
                onClick={() => setShowAddModal(false)}
                className="px-3 py-1.5 rounded-lg text-xs text-slate-400 hover:text-white"
              >
                Cancelar
              </button>
              <button
                onClick={handleAddLead}
                className="px-4 py-1.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold"
              >
                Salvar Lead
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
