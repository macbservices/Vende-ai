import { contextBridge, ipcRenderer } from 'electron';

export interface ElectronAPI {
  minimize: () => Promise<void>;
  maximize: () => Promise<boolean>;
  close: () => Promise<void>;
  isMaximized: () => Promise<boolean>;
  openExternal: (url: string) => Promise<void>;
  saveFile: (options: { defaultPath: string; filters: Array<{ name: string; extensions: string[] }>; contentBase64: string }) => Promise<{ success: boolean; filePath?: string; canceled?: boolean; error?: string }>;
  callGemini: (params: { prompt: string; systemInstruction?: string; apiKey?: string }) => Promise<{ success: boolean; text?: string; error?: string }>;
  checkNetwork: () => Promise<boolean>;
  getAppInfo: () => Promise<{ version: string; platform: string; arch: string; userDataPath: string }>;
}

const electronAPI: ElectronAPI = {
  minimize: () => ipcRenderer.invoke('window:minimize'),
  maximize: () => ipcRenderer.invoke('window:maximize'),
  close: () => ipcRenderer.invoke('window:close'),
  isMaximized: () => ipcRenderer.invoke('window:isMaximized'),
  openExternal: (url: string) => ipcRenderer.invoke('shell:openExternal', url),
  saveFile: (options) => ipcRenderer.invoke('dialog:saveFile', options),
  callGemini: (params) => ipcRenderer.invoke('gemini:execute', params),
  checkNetwork: () => ipcRenderer.invoke('system:checkNetwork'),
  getAppInfo: () => ipcRenderer.invoke('app:getInfo'),
};

contextBridge.exposeInMainWorld('electronAPI', electronAPI);
