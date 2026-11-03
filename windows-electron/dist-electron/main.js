"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
const path = __importStar(require("path"));
const fs = __importStar(require("fs"));
let mainWindow = null;
const isDev = process.env.NODE_ENV === 'development' || !electron_1.app.isPackaged;
function createWindow() {
    mainWindow = new electron_1.BrowserWindow({
        width: 1366,
        height: 850,
        minWidth: 1080,
        minHeight: 700,
        frame: false, // Custom modern Windows 11 Fluent titlebar with window controls
        backgroundColor: '#0B132B',
        title: 'VendeIA SaaS — Windows Desktop',
        webPreferences: {
            preload: path.join(__dirname, 'preload.js'),
            nodeIntegration: false,
            contextIsolation: true,
            sandbox: false,
        },
    });
    // Safe window state management
    if (isDev && process.env.VITE_DEV_SERVER_URL) {
        mainWindow.loadURL(process.env.VITE_DEV_SERVER_URL);
    }
    else {
        mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
    }
    mainWindow.webContents.setWindowOpenHandler(({ url }) => {
        electron_1.shell.openExternal(url);
        return { action: 'deny' };
    });
    mainWindow.on('closed', () => {
        mainWindow = null;
    });
}
// Ensure single instance lock for Windows Desktop app
const gotTheLock = electron_1.app.requestSingleInstanceLock();
if (!gotTheLock) {
    electron_1.app.quit();
}
else {
    electron_1.app.on('second-instance', () => {
        if (mainWindow) {
            if (mainWindow.isMinimized())
                mainWindow.restore();
            mainWindow.focus();
        }
    });
    electron_1.app.whenReady().then(() => {
        setupIpcHandlers();
        createWindow();
        electron_1.app.on('activate', () => {
            if (electron_1.BrowserWindow.getAllWindows().length === 0)
                createWindow();
        });
    });
}
electron_1.app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        electron_1.app.quit();
    }
});
function setupIpcHandlers() {
    // Window management handlers (Windows titlebar)
    electron_1.ipcMain.handle('window:minimize', () => {
        mainWindow?.minimize();
    });
    electron_1.ipcMain.handle('window:maximize', () => {
        if (mainWindow?.isMaximized()) {
            mainWindow.unmaximize();
        }
        else {
            mainWindow?.maximize();
        }
        return mainWindow?.isMaximized() ?? false;
    });
    electron_1.ipcMain.handle('window:close', () => {
        mainWindow?.close();
    });
    electron_1.ipcMain.handle('window:isMaximized', () => {
        return mainWindow?.isMaximized() ?? false;
    });
    // Shell & External Links
    electron_1.ipcMain.handle('shell:openExternal', async (_, url) => {
        if (url.startsWith('https://') || url.startsWith('http://')) {
            await electron_1.shell.openExternal(url);
        }
    });
    // Native Save File Dialog for PDF & Assets
    electron_1.ipcMain.handle('dialog:saveFile', async (_, options) => {
        if (!mainWindow)
            return { success: false, error: 'No active window' };
        const { canceled, filePath } = await electron_1.dialog.showSaveDialog(mainWindow, {
            defaultPath: options.defaultPath,
            filters: options.filters,
        });
        if (canceled || !filePath) {
            return { success: false, canceled: true };
        }
        try {
            const buffer = Buffer.from(options.contentBase64, 'base64');
            fs.writeFileSync(filePath, buffer);
            return { success: true, filePath };
        }
        catch (err) {
            return { success: false, error: err.message };
        }
    });
    // Secure Node-side Gemini API call handler (Never exposes API keys in frontend)
    electron_1.ipcMain.handle('gemini:execute', async (_, { prompt, systemInstruction, apiKey }) => {
        const key = apiKey || process.env.GEMINI_API_KEY;
        if (!key) {
            return { success: false, error: 'Chave da API Gemini não configurada no backend seguro.' };
        }
        try {
            const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${key}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    contents: [{ parts: [{ text: prompt }] }],
                    systemInstruction: systemInstruction ? { parts: [{ text: systemInstruction }] } : undefined,
                    generationConfig: {
                        temperature: 0.7,
                        topK: 40,
                        topP: 0.95,
                    },
                }),
            });
            if (!response.ok) {
                const errorText = await response.text();
                return { success: false, error: `Gemini API error (${response.status}): ${errorText}` };
            }
            const data = (await response.json());
            const generatedText = data.candidates?.[0]?.content?.parts?.[0]?.text || '';
            return { success: true, text: generatedText };
        }
        catch (err) {
            return { success: false, error: err.message || 'Falha de comunicação com o servidor Gemini.' };
        }
    });
    // Network check
    electron_1.ipcMain.handle('system:checkNetwork', async () => {
        try {
            const res = await fetch('https://www.google.com/generate_204', { method: 'HEAD', signal: AbortSignal.timeout(3000) });
            return res.status === 204;
        }
        catch {
            return false;
        }
    });
    // App version & updater
    electron_1.ipcMain.handle('app:getInfo', () => {
        return {
            version: electron_1.app.getVersion(),
            platform: process.platform,
            arch: process.arch,
            userDataPath: electron_1.app.getPath('userData'),
        };
    });
}
