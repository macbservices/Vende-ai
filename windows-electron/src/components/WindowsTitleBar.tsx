import React, { useState, useEffect } from 'react';
import { Minus, Square, Copy, X, Wifi, WifiOff, Sparkles } from 'lucide-react';

export const WindowsTitleBar: React.FC = () => {
  const [isMaximized, setIsMaximized] = useState(false);
  const [isOnline, setIsOnline] = useState(navigator.onLine);

  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    if (window.electronAPI) {
      window.electronAPI.checkNetwork().then((online) => setIsOnline(online));
      const interval = setInterval(() => {
        window.electronAPI?.checkNetwork().then((online) => setIsOnline(online));
      }, 10000);
      return () => {
        window.removeEventListener('online', handleOnline);
        window.removeEventListener('offline', handleOffline);
        clearInterval(interval);
      };
    }

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  const handleMinimize = () => {
    window.electronAPI?.minimize();
  };

  const handleMaximize = async () => {
    if (window.electronAPI) {
      const maximized = await window.electronAPI.maximize();
      setIsMaximized(maximized);
    }
  };

  const handleClose = () => {
    window.electronAPI?.close();
  };

  return (
    <header className="h-10 bg-[#070D1E] border-b border-[#1E293B] flex items-center justify-between select-none titlebar-drag z-50">
      {/* App branding */}
      <div className="flex items-center space-x-2.5 px-3">
        <div className="w-5 h-5 rounded bg-gradient-to-tr from-blue-600 to-cyan-400 flex items-center justify-center shadow">
          <Sparkles className="w-3 h-3 text-white" />
        </div>
        <span className="text-xs font-semibold tracking-wide text-slate-200">
          VendeIA SaaS <span className="text-cyan-400 font-normal">| Windows Desktop (64-bit)</span>
        </span>
      </div>

      {/* Center status */}
      <div className="flex items-center space-x-2 text-[11px] text-slate-400">
        {isOnline ? (
          <span className="flex items-center text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-full border border-emerald-500/20">
            <Wifi className="w-3 h-3 mr-1" /> Conectado (Cloud Sync)
          </span>
        ) : (
          <span className="flex items-center text-amber-400 bg-amber-500/10 px-2 py-0.5 rounded-full border border-amber-500/20">
            <WifiOff className="w-3 h-3 mr-1" /> Modo Offline
          </span>
        )}
      </div>

      {/* Windows window control buttons */}
      <div className="flex items-center h-full titlebar-nodrag">
        <button
          onClick={handleMinimize}
          className="w-11 h-full flex items-center justify-center text-slate-400 hover:text-white hover:bg-slate-800/80 transition-colors"
          title="Minimizar"
        >
          <Minus className="w-3.5 h-3.5" />
        </button>
        <button
          onClick={handleMaximize}
          className="w-11 h-full flex items-center justify-center text-slate-400 hover:text-white hover:bg-slate-800/80 transition-colors"
          title={isMaximized ? "Restaurar" : "Maximizar"}
        >
          {isMaximized ? <Copy className="w-3.5 h-3.5" /> : <Square className="w-3.5 h-3.5" />}
        </button>
        <button
          onClick={handleClose}
          className="w-11 h-full flex items-center justify-center text-slate-400 hover:text-white hover:bg-red-600 transition-colors"
          title="Fechar"
        >
          <X className="w-4 h-4" />
        </button>
      </div>
    </header>
  );
};
