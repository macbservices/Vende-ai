import React, { useState } from 'react';
import { MessageSquare, Send, CheckCircle, Smartphone } from 'lucide-react';

export const WhatsAppAutomationView: React.FC = () => {
  const [targetNumber, setTargetNumber] = useState('(11) 98765-4321');
  const [messageType, setMessageType] = useState<'delivery' | 'abandoned' | 'upsell'>('delivery');
  const [sentStatus, setSentStatus] = useState<string>('');

  const templates = {
    delivery: `🎉 Olá! Seu pedido do e-book foi confirmado com sucesso!\n\nSeu material já está disponível para download imediato através do link seguro abaixo:\nhttps://vendeia.com/download/ebook-pdf-liberado\n\nQualquer dúvida, conte conosco!`,
    abandoned: `👋 Olá! Notamos que você iniciou o pedido do seu e-book mas não concluiu o Pix.\n\nSeparamos seu acesso exclusivo com 20% de desconto adicional se finalizar nas próximas 2 horas:\nhttps://vendeia.com/checkout/recuperacao`,
    upsell: `🚀 Olá! Esperamos que esteja aproveitando a leitura do seu novo e-book.\n\nLiberamos para você uma consultoria rápida de implementação com 70% de desconto por tempo limitado!`,
  };

  const handleTestSend = () => {
    setSentStatus('Enviando mensagem via API de Automação WhatsApp...');
    setTimeout(() => {
      setSentStatus(`Mensagem entregue com sucesso para ${targetNumber}!`);
    }, 1500);
  };

  return (
    <div className="p-8 max-w-4xl mx-auto space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between border-b border-[#1E293B] pb-4">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <MessageSquare className="w-5 h-5 text-emerald-400 mr-2" />
            Automação de Mensagens no WhatsApp
          </h1>
          <p className="text-xs text-slate-400">Disparo automático de links de entrega, recuperação de carrinhos e suporte</p>
        </div>
      </div>

      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-5">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold text-white uppercase tracking-wider">Status do Bot WhatsApp</span>
          <span className="flex items-center text-xs text-emerald-400 bg-emerald-500/10 px-2.5 py-1 rounded-full border border-emerald-500/20">
            <CheckCircle className="w-3.5 h-3.5 mr-1" /> Conectado & Operacional
          </span>
        </div>

        <div>
          <label className="text-xs text-slate-400 block mb-1.5 font-semibold">Gatilho de Automação:</label>
          <div className="grid grid-cols-3 gap-3">
            {[
              { id: 'delivery', label: 'Entrega Imediata no Pix' },
              { id: 'abandoned', label: 'Recuperação de Carrinho' },
              { id: 'upsell', label: 'Pós-Venda & Upsell' },
            ].map((t) => (
              <button
                key={t.id}
                onClick={() => setMessageType(t.id as any)}
                className={`py-2 rounded-xl text-xs font-medium border ${
                  messageType === t.id
                    ? 'border-emerald-400 bg-emerald-500/10 text-white font-bold'
                    : 'border-[#1E293B] bg-[#0B132B] text-slate-400'
                }`}
              >
                {t.label}
              </button>
            ))}
          </div>
        </div>

        <div>
          <label className="text-xs text-slate-400 block mb-1 font-semibold">Modelo de Mensagem (com placeholders dinâmicos):</label>
          <textarea
            value={templates[messageType]}
            readOnly
            rows={6}
            className="w-full bg-[#0B132B] border border-[#1E293B] rounded-xl p-3 text-xs text-slate-200 leading-relaxed font-sans"
          />
        </div>

        {/* Test Dispatch Form */}
        <div className="pt-4 border-t border-[#1E293B] flex items-end space-x-3">
          <div className="flex-1">
            <label className="text-xs text-slate-400 block mb-1 font-semibold">Número para Disparo de Teste:</label>
            <input
              type="text"
              value={targetNumber}
              onChange={(e) => setTargetNumber(e.target.value)}
              className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-xs text-white"
            />
          </div>
          <button
            onClick={handleTestSend}
            className="flex items-center space-x-1.5 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold rounded-xl transition-all shadow cursor-pointer"
          >
            <Send className="w-3.5 h-3.5" />
            <span>Testar Envio</span>
          </button>
        </div>

        {sentStatus && (
          <div className="p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-300 text-xs">
            {sentStatus}
          </div>
        )}
      </div>
    </div>
  );
};
