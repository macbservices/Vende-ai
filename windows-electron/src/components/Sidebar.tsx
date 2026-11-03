import React from 'react';
import {
  LayoutDashboard,
  Sparkles,
  BookOpen,
  Edit3,
  Globe,
  CreditCard,
  Megaphone,
  Video,
  Users,
  MessageSquare,
  BarChart3,
  Bot,
  ShieldCheck,
  Settings,
} from 'lucide-react';

export type NavTab =
  | 'dashboard'
  | 'creator'
  | 'products'
  | 'editor'
  | 'landing'
  | 'checkout'
  | 'marketing'
  | 'videos'
  | 'crm'
  | 'whatsapp'
  | 'reports'
  | 'assistant'
  | 'admin'
  | 'settings';

interface SidebarProps {
  currentTab: NavTab;
  onSelectTab: (tab: NavTab) => void;
  productsCount: number;
}

export const Sidebar: React.FC<SidebarProps> = ({ currentTab, onSelectTab, productsCount }) => {
  const menuItems = [
    { id: 'dashboard', label: 'Visão Geral', icon: LayoutDashboard },
    { id: 'creator', label: 'Criador com IA', icon: Sparkles, highlight: true },
    { id: 'products', label: 'Meus Infoprodutos', icon: BookOpen, badge: productsCount },
    { id: 'editor', label: 'Editor de E-books', icon: Edit3 },
    { id: 'landing', label: 'Páginas de Vendas', icon: Globe },
    { id: 'checkout', label: 'Checkout & Pix', icon: CreditCard },
    { id: 'marketing', label: 'Marketing IA', icon: Megaphone },
    { id: 'videos', label: 'Roteiros de Vídeo', icon: Video },
    { id: 'crm', label: 'CRM & Leads', icon: Users },
    { id: 'whatsapp', label: 'Automação WhatsApp', icon: MessageSquare },
    { id: 'reports', label: 'Relatórios Financeiros', icon: BarChart3 },
    { id: 'assistant', label: 'Assistente Gemini', icon: Bot, isAi: true },
  ];

  const bottomItems = [
    { id: 'admin', label: 'Painel SaaS Admin', icon: ShieldCheck },
    { id: 'settings', label: 'Configurações', icon: Settings },
  ];

  return (
    <aside className="w-64 bg-[#0F172A] border-r border-[#1E293B] flex flex-col justify-between h-[calc(100vh-40px)] select-none">
      {/* Top Nav */}
      <div className="p-3 overflow-y-auto space-y-1">
        <div className="px-3 py-2 text-[11px] font-semibold tracking-wider text-slate-400 uppercase">
          Módulos Principais
        </div>

        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;

          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id as NavTab)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                isActive
                  ? 'bg-blue-600 text-white shadow-md shadow-blue-600/30 font-semibold'
                  : item.highlight
                  ? 'text-cyan-400 bg-cyan-500/10 hover:bg-cyan-500/20'
                  : 'text-slate-300 hover:text-white hover:bg-slate-800/60'
              }`}
            >
              <div className="flex items-center space-x-3">
                <Icon className={`w-4 h-4 ${isActive ? 'text-white' : item.isAi ? 'text-cyan-400' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge !== undefined && item.badge > 0 && (
                <span className={`text-[10px] px-1.5 py-0.5 rounded-full font-bold ${
                  isActive ? 'bg-white/20 text-white' : 'bg-slate-800 text-slate-300'
                }`}>
                  {item.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Bottom Nav */}
      <div className="p-3 border-t border-[#1E293B] space-y-1 bg-[#090F1F]">
        <div className="px-3 py-1.5 text-[10px] font-semibold tracking-wider text-slate-500 uppercase">
          Sistema & Gestão
        </div>
        {bottomItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;

          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id as NavTab)}
              className={`w-full flex items-center space-x-3 px-3 py-2 rounded-lg text-xs font-medium transition-colors ${
                isActive
                  ? 'bg-blue-600 text-white font-semibold'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{item.label}</span>
            </button>
          );
        })}
      </div>
    </aside>
  );
};
