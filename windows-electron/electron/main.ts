import { app, BrowserWindow, ipcMain, shell, dialog } from 'electron';
import * as path from 'path';
import * as fs from 'fs';

let mainWindow: BrowserWindow | null = null;

const isDev = process.env.NODE_ENV === 'development' || !app.isPackaged;

function createWindow() {
  mainWindow = new BrowserWindow({
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
  } else {
    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
  }

  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// Ensure single instance lock for Windows Desktop app
const gotTheLock = app.requestSingleInstanceLock();
if (!gotTheLock) {
  app.quit();
} else {
  app.on('second-instance', () => {
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore();
      mainWindow.focus();
    }
  });

  app.whenReady().then(() => {
    setupIpcHandlers();
    createWindow();

    app.on('activate', () => {
      if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
  });
}

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

function setupIpcHandlers() {
  // Window management handlers (Windows titlebar)
  ipcMain.handle('window:minimize', () => {
    mainWindow?.minimize();
  });

  ipcMain.handle('window:maximize', () => {
    if (mainWindow?.isMaximized()) {
      mainWindow.unmaximize();
    } else {
      mainWindow?.maximize();
    }
    return mainWindow?.isMaximized() ?? false;
  });

  ipcMain.handle('window:close', () => {
    mainWindow?.close();
  });

  ipcMain.handle('window:isMaximized', () => {
    return mainWindow?.isMaximized() ?? false;
  });

  // Shell & External Links
  ipcMain.handle('shell:openExternal', async (_, url: string) => {
    if (url.startsWith('https://') || url.startsWith('http://')) {
      await shell.openExternal(url);
    }
  });

  // Native Save File Dialog for PDF & Assets
  ipcMain.handle('dialog:saveFile', async (_, options: { defaultPath: string; filters: Array<{ name: string; extensions: string[] }>; contentBase64: string }) => {
    if (!mainWindow) return { success: false, error: 'No active window' };

    const { canceled, filePath } = await dialog.showSaveDialog(mainWindow, {
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
    } catch (err: any) {
      return { success: false, error: err.message };
    }
  });

  // Secure Node-side Gemini API call handler (Never exposes API keys in frontend)
  ipcMain.handle('gemini:execute', async (_, { prompt, systemInstruction, apiKey }: { prompt: string; systemInstruction?: string; apiKey?: string }) => {
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

      const data = (await response.json()) as any;
      const generatedText = data.candidates?.[0]?.content?.parts?.[0]?.text || '';
      return { success: true, text: generatedText };
    } catch (err: any) {
      return { success: false, error: err.message || 'Falha de comunicação com o servidor Gemini.' };
    }
  });

  // Network check
  ipcMain.handle('system:checkNetwork', async () => {
    try {
      const res = await fetch('https://www.google.com/generate_204', { method: 'HEAD', signal: AbortSignal.timeout(3000) });
      return res.status === 204;
    } catch {
      return false;
    }
  });

  // App version & updater
  ipcMain.handle('app:getInfo', () => {
    return {
      version: app.getVersion(),
      platform: process.platform,
      arch: process.arch,
      userDataPath: app.getPath('userData'),
    };
  });
}
