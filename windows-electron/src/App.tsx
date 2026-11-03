import React, { useState, useEffect } from 'react';
import { WindowsTitleBar } from './components/WindowsTitleBar';
import { Sidebar, NavTab } from './components/Sidebar';
import { DashboardView } from './views/DashboardView';
import { CreateProductWizardView } from './views/CreateProductWizardView';
import { ProductListView } from './views/ProductListView';
import { EbookEditorView } from './views/EbookEditorView';
import { LandingPageBuilderView } from './views/LandingPageBuilderView';
import { CheckoutPaymentsView } from './views/CheckoutPaymentsView';
import { MarketingAiView } from './views/MarketingAiView';
import { CreativesVideosView } from './views/CreativesVideosView';
import { CrmLeadsView } from './views/CrmLeadsView';
import { WhatsAppAutomationView } from './views/WhatsAppAutomationView';
import { ReportsView } from './views/ReportsView';
import { GeminiAssistantView } from './views/GeminiAssistantView';
import { SaaSAdminView } from './views/SaaSAdminView';
import { SettingsView } from './views/SettingsView';
import { Product, Order, Lead, LandingPage } from './types';

const INITIAL_PRODUCTS: Product[] = [
  {
    id: 'prod-1',
    userId: 'user-default',
    title: 'Guia Definitivo de Negócios com Inteligência Artificial',
    subtitle: 'Como automatizar processos, vendas e atendimento usando modelos de linguagem modernos.',
    niche: 'Negócios & IA',
    targetAudience: 'Empreendedores e profissionais liberais',
    offerPrice: 97.00,
    status: 'completed',
    chapters: [
      {
        id: 'chap-1-1',
        number: 1,
        title: 'Introdução à Revolução da IA nos Negócios',
        summary: 'Panorama do mercado e oportunidades imediatas.',
        content: 'A inteligência artificial não é mais uma promessa para o futuro distante; ela é a ferramenta mais transformadora de produtividade e geração de receita do século XXI...\n\nNeste capítulo, você entenderá os pilares fundamentais para transformar rotinas operacionais manuais em fluxos hiper-eficientes.',
        generatedWithAi: true,
      },
      {
        id: 'chap-1-2',
        number: 2,
        title: 'Mapeamento de Processos e Oportunidades',
        summary: 'Como auditar onde a IA traz maior retorno financeiro.',
        content: 'Antes de aplicar qualquer ferramenta de automação, é imprescindível auditar os gargalos do seu negócio. As maiores alavancas estão no suporte ao cliente, na produção de conteúdo e no fechamento de vendas.',
        generatedWithAi: true,
      },
      {
        id: 'chap-1-3',
        number: 3,
        title: 'Implementação de Agentes e Automações',
        summary: 'Passo a passo técnico sem complicações.',
        content: 'Integrar modelos com seus dados internos é a chave para o diferencial competitivo. Aqui detalhamos como construir fluxos de resposta imediata conectados ao WhatsApp e e-mail.',
        generatedWithAi: true,
      },
    ],
    createdAt: Date.now() - 86400000 * 2,
    updatedAt: Date.now() - 86400000,
  },
  {
    id: 'prod-2',
    userId: 'user-default',
    title: 'Manual Prático de Tráfego Pago e Copywriting Direto',
    subtitle: 'Estratégias validadas para vender produtos digitais com alto retorno sobre investimento.',
    niche: 'Marketing Digital',
    targetAudience: 'Afiliados e produtores de infoprodutos',
    offerPrice: 67.00,
    status: 'completed',
    chapters: [
      {
        id: 'chap-2-1',
        number: 1,
        title: 'Fundamentos de Tráfego de Alta Conversão',
        summary: 'Estruturação de criativos e públicos que compram.',
        content: 'Vender no digital depende da sincronia perfeita entre uma oferta irresistível, um público consciente e um criativo com gancho forte.',
        generatedWithAi: true,
      },
    ],
    createdAt: Date.now() - 86400000 * 5,
    updatedAt: Date.now() - 86400000 * 3,
  },
];

const INITIAL_ORDERS: Order[] = [
  {
    id: 'ord-101',
    productId: 'prod-1',
    productTitle: 'Guia Definitivo de Negócios com Inteligência Artificial',
    customerName: 'Rodrigo Medeiros',
    customerEmail: 'rodrigo.medeiros@exemplo.com.br',
    customerPhone: '(11) 99876-5432',
    amount: 97.00,
    paymentMethod: 'PIX',
    status: 'PAID',
    delivered: true,
    createdAt: Date.now() - 3600000 * 4,
  },
  {
    id: 'ord-102',
    productId: 'prod-2',
    productTitle: 'Manual Prático de Tráfego Pago e Copywriting Direto',
    customerName: 'Beatriz Vasconcelos',
    customerEmail: 'beatriz.v@exemplo.com.br',
    customerPhone: '(21) 98765-1234',
    amount: 67.00,
    paymentMethod: 'PIX',
    status: 'PAID',
    delivered: true,
    createdAt: Date.now() - 3600000 * 18,
  },
];

const INITIAL_LEADS: Lead[] = [
  {
    id: 'lead-1',
    name: 'Ana Carolina Dias',
    email: 'ana.dias@exemplo.com',
    phone: '(31) 98765-4321',
    stage: 'CHECKOUT',
    notes: 'Aguardando pagamento Pix de R$ 97',
    createdAt: Date.now() - 3600000 * 2,
  },
  {
    id: 'lead-2',
    name: 'Felipe Santana',
    email: 'felipe.s@exemplo.com',
    phone: '(41) 99123-4567',
    stage: 'INTERESSADO',
    notes: 'Perguntou sobre suporte e certificado',
    createdAt: Date.now() - 3600000 * 8,
  },
  {
    id: 'lead-3',
    name: 'Juliana Rocha',
    email: 'juliana.rocha@exemplo.com',
    phone: '(19) 98456-7890',
    stage: 'CLIENTE',
    notes: 'Comprou e baixou o PDF imediatamente',
    createdAt: Date.now() - 86400000,
  },
];

export const App: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<NavTab>('dashboard');
  const [selectedProductId, setSelectedProductId] = useState<string>('prod-1');

  // Persistence
  const [products, setProducts] = useState<Product[]>(() => {
    const saved = localStorage.getItem('vendeia_products');
    return saved ? JSON.parse(saved) : INITIAL_PRODUCTS;
  });

  const [orders, setOrders] = useState<Order[]>(() => {
    const saved = localStorage.getItem('vendeia_orders');
    return saved ? JSON.parse(saved) : INITIAL_ORDERS;
  });

  const [leads, setLeads] = useState<Lead[]>(() => {
    const saved = localStorage.getItem('vendeia_leads');
    return saved ? JSON.parse(saved) : INITIAL_LEADS;
  });

  useEffect(() => {
    localStorage.setItem('vendeia_products', JSON.stringify(products));
  }, [products]);

  useEffect(() => {
    localStorage.setItem('vendeia_orders', JSON.stringify(orders));
  }, [orders]);

  useEffect(() => {
    localStorage.setItem('vendeia_leads', JSON.stringify(leads));
  }, [leads]);

  const handleNavigate = (tab: NavTab, productId?: string) => {
    if (productId) {
      setSelectedProductId(productId);
    }
    setCurrentTab(tab);
  };

  const activeProduct = products.find((p) => p.id === selectedProductId) || products[0];

  const handleProductCreated = (newProduct: Product) => {
    setProducts([newProduct, ...products]);
    setSelectedProductId(newProduct.id);
    setCurrentTab('editor');
  };

  const handleUpdateProduct = (updated: Product) => {
    setProducts(products.map((p) => (p.id === updated.id ? updated : p)));
  };

  const handleOrderCompleted = (newOrder: Order) => {
    setOrders([newOrder, ...orders]);
  };

  return (
    <div className="flex flex-col h-screen w-screen overflow-hidden bg-[#0B132B]">
      {/* 1. Native Windows 11 Fluent Titlebar with Drag Region & Window Controls */}
      <WindowsTitleBar />

      {/* 2. Main Workspace Layout */}
      <div className="flex flex-1 overflow-hidden">
        {/* Navigation Rail */}
        <Sidebar
          currentTab={currentTab}
          onSelectTab={handleNavigate}
          productsCount={products.length}
        />

        {/* Content View */}
        <main className="flex-1 bg-[#0B132B] overflow-hidden">
          {currentTab === 'dashboard' && (
            <DashboardView
              products={products}
              orders={orders}
              onNavigate={handleNavigate}
            />
          )}

          {currentTab === 'creator' && (
            <CreateProductWizardView
              onProductCreated={handleProductCreated}
              onCancel={() => setCurrentTab('dashboard')}
            />
          )}

          {currentTab === 'products' && (
            <ProductListView
              products={products}
              onNavigate={handleNavigate}
            />
          )}

          {currentTab === 'editor' && activeProduct && (
            <EbookEditorView
              product={activeProduct}
              onUpdateProduct={handleUpdateProduct}
              onBack={() => setCurrentTab('products')}
            />
          )}

          {currentTab === 'landing' && activeProduct && (
            <LandingPageBuilderView
              product={activeProduct}
              onSaveLandingPage={() => alert('Página de vendas publicada online!')}
            />
          )}

          {currentTab === 'checkout' && (
            <CheckoutPaymentsView
              products={products}
              onOrderCompleted={handleOrderCompleted}
            />
          )}

          {currentTab === 'marketing' && (
            <MarketingAiView products={products} />
          )}

          {currentTab === 'videos' && (
            <CreativesVideosView products={products} />
          )}

          {currentTab === 'crm' && (
            <CrmLeadsView leads={leads} onUpdateLeads={setLeads} />
          )}

          {currentTab === 'whatsapp' && (
            <WhatsAppAutomationView />
          )}

          {currentTab === 'reports' && (
            <ReportsView orders={orders} />
          )}

          {currentTab === 'assistant' && (
            <GeminiAssistantView />
          )}

          {currentTab === 'admin' && (
            <SaaSAdminView />
          )}

          {currentTab === 'settings' && (
            <SettingsView />
          )}
        </main>
      </div>
    </div>
  );
};

export default App;
