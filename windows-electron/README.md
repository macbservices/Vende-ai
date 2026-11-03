# VendeIA SaaS — Aplicativo Desktop Windows (10/11 64-bit)

Aplicativo oficial para Windows 10 e Windows 11 desenvolvido com **Electron**, **React 19**, **TypeScript** e integrado ao **Google Gemini AI**.

---

## 🚀 Requisitos e Tecnologias
- **Electron:** Processo principal seguro para Windows com controle de janelas, diálogo nativo de exportação de arquivos e isolamento de contexto (`contextBridge`).
- **React + TypeScript + Tailwind CSS:** Interface gráfica de alta performance com design Fluent Windows.
- **Google Gemini Engine:** Conexão segura via processo principal (sem expor chave no renderer).
- **jsPDF:** Diagramação profissional e exportação nativa de e-books em formato PDF.
- **Electron Builder:** Empacotamento para Windows 64-bit (`.exe` NSIS installer e executável portable).

---

## 📦 Como Executar e Compilar no Windows

### 1. Instalar dependências
```bash
cd windows-electron
npm install
```

### 2. Modo de Desenvolvimento (Hot Reload)
```bash
npm run dev
```

### 3. Gerar o Instalador `.exe` para Windows (x64)
Para gerar o instalador executável do Windows:
```bash
npm run dist:win
```
O arquivo de instalação `VendeIA SaaS-Setup-1.0.0-x64.exe` será gerado automaticamente dentro do diretório `dist-release/`.

---

## 🛠️ Módulos Implementados
1. **Visão Geral (Dashboard):** Métricas de vendas, pedidos e atalhos rápidos.
2. **Criador com IA (5 Etapas):** Nicho → Análise de Demanda → Títulos e Preços → Sumário → Redação de Capítulos com Gemini.
3. **Meus Infoprodutos:** Biblioteca de e-books com filtros e status.
4. **Editor de E-books:** Editor rico com ferramenta "Melhorar com Gemini" e exportação nativa em PDF.
5. **Páginas de Vendas:** Construtor com geração de headlines persuasivas e preview responsivo (Mobile/Desktop).
6. **Checkout & Pix:** Gateways de pagamento (Mercado Pago, Asaas, Efí), QR Code dinâmico, simulador de webhook e entrega digital imediata.
7. **Marketing IA:** Criação de copies para Meta Ads, Google Ads e sequências de e-mail marketing.
8. **Roteiros de Vídeo:** Ganchos e roteiros estruturados para gravação de Reels e TikTok.
9. **CRM & Leads:** Quadro Kanban interativo com estágios de funil de vendas.
10. **Automação WhatsApp:** Disparo de links de download, recuperação de carrinhos abandonados e pós-venda.
11. **Relatórios Financeiros:** Ticket médio, taxas de conversão e rastreamento de consumo de cota Gemini.
12. **Assistente Gemini Integrado:** Chat contextual com atalhos estratégicos.
13. **Painel SaaS Admin:** Tabela de planos e gerenciamento de assinantes multi-inquilino.
14. **Configurações:** Gerenciamento seguro de chave de API Gemini, informações de build e atualizador.
