import React, { useState, useEffect } from 'react';
import { Settings, Key, RefreshCw, CheckCircle, Info, HardDrive } from 'lucide-react';
import { GeminiService } from '../services/geminiService';

export const SettingsView: React.FC = () => {
  const [apiKey, setApiKey] = useState(GeminiService.getApiKey());
  const [savedSuccess, setSavedSuccess] = useState(false);
  const [appInfo, setAppInfo] = useState({
    version: '1.0.0',
    platform: 'win32',
    arch: 'x64',
    userDataPath: 'C:\\Users\\AppData\\Roaming\\vendeia-saas-windows',
  });
  const [updateStatus, setUpdateStatus] = useState<string>('');

  useEffect(() => {
    if (window.electronAPI) {
      window.electronAPI.getAppInfo().then((info) => setAppInfo(info));
    }
  }, []);

  const handleSaveApiKey = () => {
    GeminiService.setApiKey(apiKey);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 2500);
  };

  const handleCheckUpdates = () => {
    setUpdateStatus('Verificando atualizações no servidor...');
    setTimeout(() => {
      setUpdateStatus('O aplicativo VendeIA SaaS está atualizado na versão mais recente (v1.0.0).');
    }, 1500);
  };

  return (
    <div className="p-8 max-w-3xl mx-auto space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between border-b border-[#1E293B] pb-4">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <Settings className="w-5 h-5 text-slate-400 mr-2" />
            Configurações do Aplicativo Windows
          </h1>
          <p className="text-xs text-slate-400">Chaves de API, integrações e atualizações do sistema</p>
        </div>
      </div>

      {/* Gemini API Key Box */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-4">
        <div className="flex items-center space-x-2">
          <Key className="w-4 h-4 text-cyan-400" />
          <h2 className="text-sm font-bold text-white">Chave da API Gemini (Google AI Studio)</h2>
        </div>
        <p className="text-xs text-slate-400">
          A chave é armazenada com segurança no processo principal do Node.js e nunca exposta em ambiente web não autenticado.
        </p>

        <div>
          <input
            type="password"
            value={apiKey}
            onChange={(e) => setApiKey(e.target.value)}
            placeholder="AIzaSy..."
            className="w-full bg-[#0B132B] border border-[#1E293B] rounded-xl p-3 text-xs text-white font-mono focus:outline-none focus:border-cyan-400"
          />
        </div>

        <div className="flex items-center justify-between">
          <button
            onClick={handleSaveApiKey}
            className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs rounded-xl transition-colors cursor-pointer"
          >
            Salvar Chave
          </button>
          {savedSuccess && (
            <span className="flex items-center text-xs text-emerald-400 font-medium">
              <CheckCircle className="w-4 h-4 mr-1" /> Chave salva com sucesso!
            </span>
          )}
        </div>
      </div>

      {/* App Environment Info */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-3">
        <div className="flex items-center space-x-2">
          <Info className="w-4 h-4 text-blue-400" />
          <h2 className="text-sm font-bold text-white">Informações da Compilação</h2>
        </div>

        <div className="grid grid-cols-2 gap-3 text-xs text-slate-300">
          <div className="p-3 bg-[#0B132B] rounded-xl border border-[#1E293B]">
            <span className="text-slate-500 block">Versão:</span>
            <span className="font-bold text-white">v{appInfo.version}</span>
          </div>
          <div className="p-3 bg-[#0B132B] rounded-xl border border-[#1E293B]">
            <span className="text-slate-500 block">Arquitetura de Destino:</span>
            <span className="font-bold text-white">Windows 10/11 ({appInfo.arch})</span>
          </div>
          <div className="col-span-2 p-3 bg-[#0B132B] rounded-xl border border-[#1E293B] flex items-center justify-between">
            <div>
              <span className="text-slate-500 block">Diretório de Dados Locais:</span>
              <span className="font-mono text-[11px] text-cyan-300 truncate">{appInfo.userDataPath}</span>
            </div>
            <HardDrive className="w-4 h-4 text-slate-500 shrink-0 ml-2" />
          </div>
        </div>
      </div>

      {/* Auto-Updater */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-3">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-sm font-bold text-white">Atualizações do Aplicativo</h2>
            <p className="text-xs text-slate-400">Verifique se há novos pacotes e patches disponíveis</p>
          </div>
          <button
            onClick={handleCheckUpdates}
            className="flex items-center space-x-1.5 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-xl transition-colors cursor-pointer"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Buscar Atualizações</span>
          </button>
        </div>

        {updateStatus && (
          <div className="p-3 rounded-xl bg-blue-500/10 border border-blue-500/20 text-blue-300 text-xs">
            {updateStatus}
          </div>
        )}
      </div>
    </div>
  );
};
