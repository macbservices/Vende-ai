"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
const electronAPI = {
    minimize: () => electron_1.ipcRenderer.invoke('window:minimize'),
    maximize: () => electron_1.ipcRenderer.invoke('window:maximize'),
    close: () => electron_1.ipcRenderer.invoke('window:close'),
    isMaximized: () => electron_1.ipcRenderer.invoke('window:isMaximized'),
    openExternal: (url) => electron_1.ipcRenderer.invoke('shell:openExternal', url),
    saveFile: (options) => electron_1.ipcRenderer.invoke('dialog:saveFile', options),
    callGemini: (params) => electron_1.ipcRenderer.invoke('gemini:execute', params),
    checkNetwork: () => electron_1.ipcRenderer.invoke('system:checkNetwork'),
    getAppInfo: () => electron_1.ipcRenderer.invoke('app:getInfo'),
};
electron_1.contextBridge.exposeInMainWorld('electronAPI', electronAPI);
