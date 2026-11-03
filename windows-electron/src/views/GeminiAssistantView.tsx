import React, { useState } from 'react';
import { GeminiService } from '../services/geminiService';
import { Bot, Send, Sparkles, User, Loader2 } from 'lucide-react';

interface ChatMessage {
  id: string;
  sender: 'user' | 'gemini';
  text: string;
  timestamp: string;
}

export const GeminiAssistantView: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: 'msg-1',
      sender: 'gemini',
      text: 'Olá! Sou o Assistente Central Gemini da sua plataforma VendeIA no Windows. Posso criar infoprodutos, redigir copies de vendas, gerar roteiros de vídeo e analisar seus resultados. Como posso ajudar agora?',
      timestamp: 'Agora',
    },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  const quickPrompts = [
    'Crie um e-book sobre produtividade e foco',
    'Escreva 5 ideias de anúncios para Instagram',
    'Como aumentar a taxa de conversão do meu checkout Pix?',
    'Crie um roteiro viral de 30s para Reels',
  ];

  const handleSend = async (textToSend?: string) => {
    const text = textToSend || input;
    if (!text.trim() || loading) return;

    const userMsg: ChatMessage = {
      id: `msg-${Date.now()}`,
      sender: 'user',
      text,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const reply = await GeminiService.generateText(text, 'Você é o assistente inteligente oficial da plataforma VendeIA SaaS Windows.');
      const geminiMsg: ChatMessage = {
        id: `msg-${Date.now() + 1}`,
        sender: 'gemini',
        text: reply,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages((prev) => [...prev, geminiMsg]);
    } catch (err: any) {
      const errorMsg: ChatMessage = {
        id: `msg-${Date.now() + 1}`,
        sender: 'gemini',
        text: `Erro ao processar: ${err.message}`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-[calc(100vh-40px)] bg-[#0B132B]">
      {/* Header */}
      <div className="h-14 bg-[#131D31] border-b border-[#1E293B] px-6 flex items-center justify-between">
        <div className="flex items-center space-x-2.5">
          <div className="w-8 h-8 rounded-lg bg-cyan-500/20 text-cyan-400 flex items-center justify-center border border-cyan-500/30">
            <Bot className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-sm font-bold text-white">Assistente Estratégico Gemini</h1>
            <span className="text-[10px] text-emerald-400 flex items-center">
              ● Modelo Oficial Gemini 2.5 Flash Ativo
            </span>
          </div>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-6 space-y-4">
        {messages.map((m) => (
          <div
            key={m.id}
            className={`flex items-start space-x-3 ${m.sender === 'user' ? 'justify-end' : 'justify-start'}`}
          >
            {m.sender === 'gemini' && (
              <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-blue-600 to-cyan-400 flex items-center justify-center shrink-0">
                <Sparkles className="w-4 h-4 text-white" />
              </div>
            )}
            <div
              className={`max-w-xl p-4 rounded-2xl text-xs leading-relaxed ${
                m.sender === 'user'
                  ? 'bg-blue-600 text-white font-medium'
                  : 'bg-[#131D31] border border-[#1E293B] text-slate-200'
              }`}
            >
              <div className="whitespace-pre-wrap">{m.text}</div>
              <div className={`text-[9px] mt-1.5 ${m.sender === 'user' ? 'text-blue-200' : 'text-slate-500'} text-right`}>
                {m.timestamp}
              </div>
            </div>
            {m.sender === 'user' && (
              <div className="w-7 h-7 rounded-lg bg-slate-700 flex items-center justify-center shrink-0">
                <User className="w-4 h-4 text-white" />
              </div>
            )}
          </div>
        ))}
        {loading && (
          <div className="flex items-center space-x-2 text-xs text-slate-400 bg-[#131D31] border border-[#1E293B] p-3 rounded-2xl w-fit">
            <Loader2 className="w-4 h-4 text-cyan-400 animate-spin" />
            <span>O Gemini está pensando...</span>
          </div>
        )}
      </div>

      {/* Quick Prompts & Input */}
      <div className="p-4 bg-[#0F172A] border-t border-[#1E293B] space-y-3">
        <div className="flex items-center space-x-2 overflow-x-auto pb-1">
          {quickPrompts.map((qp, i) => (
            <button
              key={i}
              onClick={() => handleSend(qp)}
              className="text-[11px] whitespace-nowrap bg-[#1E293B] hover:bg-slate-700 text-slate-300 px-3 py-1 rounded-full border border-slate-700 transition-colors cursor-pointer"
            >
              {qp}
            </button>
          ))}
        </div>

        <div className="flex items-center space-x-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Digite sua dúvida ou comando para o Gemini..."
            className="flex-1 bg-[#131D31] border border-[#1E293B] rounded-xl px-4 py-3 text-xs text-white focus:outline-none focus:border-cyan-400"
          />
          <button
            onClick={() => handleSend()}
            disabled={!input.trim() || loading}
            className="p-3 bg-blue-600 hover:bg-blue-500 text-white rounded-xl transition-colors disabled:opacity-50 cursor-pointer shadow"
          >
            <Send className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
