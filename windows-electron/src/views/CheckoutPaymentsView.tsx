import React, { useState } from 'react';
import { Product, Order } from '../types';
import { CreditCard, QrCode, CheckCircle, Copy, Check, ArrowRight, ShieldCheck, Download } from 'lucide-react';
import { PdfExporter } from '../services/pdfExporter';

interface CheckoutPaymentsViewProps {
  products: Product[];
  onOrderCompleted: (order: Order) => void;
}

export const CheckoutPaymentsView: React.FC<CheckoutPaymentsViewProps> = ({ products, onOrderCompleted }) => {
  const selectedProduct = products[0] || {
    id: 'prod-demo',
    title: 'Guia Definitivo de IA para Negócios',
    offerPrice: 97.00,
    chapters: [],
  };

  const [customerName, setCustomerName] = useState('João Victor Santos');
  const [customerEmail, setCustomerEmail] = useState('joao.victor@exemplo.com.br');
  const [customerPhone, setCustomerPhone] = useState('(11) 98765-4321');
  const [gateway, setGateway] = useState<'mercadopago' | 'asaas' | 'efi'>('mercadopago');

  const [checkoutStep, setCheckoutStep] = useState<'form' | 'pix' | 'delivered'>('form');
  const [copiedPix, setCopiedPix] = useState(false);

  const pixKey = "00020126580014BR.GOV.BCB.PIX0136a629b3c4-1234-5678-90ab-cdef12345678520400005303986540597.005802BR5915VendeIA Servicos6009Sao Paulo62070503***6304ABCD";

  const handleGeneratePix = () => {
    setCheckoutStep('pix');
  };

  const handleSimulateWebhookPayment = () => {
    const newOrder: Order = {
      id: `ord-${Date.now()}`,
      productId: selectedProduct.id,
      productTitle: selectedProduct.title,
      customerName,
      customerEmail,
      customerPhone,
      amount: selectedProduct.offerPrice || 97.0,
      paymentMethod: 'PIX',
      status: 'PAID',
      delivered: true,
      createdAt: Date.now(),
    };

    onOrderCompleted(newOrder);
    setCheckoutStep('delivered');
  };

  const handleCopyPix = () => {
    navigator.clipboard.writeText(pixKey);
    setCopiedPix(true);
    setTimeout(() => setCopiedPix(false), 2500);
  };

  const handleDownloadDeliveredPdf = async () => {
    const res = await PdfExporter.exportEbook(selectedProduct as any);
    if (res.success) {
      alert(`Download concluído: ${res.path}`);
    }
  };

  return (
    <div className="p-8 max-w-4xl mx-auto space-y-6 overflow-y-auto max-h-[calc(100vh-40px)]">
      <div className="flex items-center justify-between border-b border-[#1E293B] pb-4">
        <div>
          <h1 className="text-xl font-bold text-white flex items-center">
            <CreditCard className="w-5 h-5 text-emerald-400 mr-2" />
            Checkout & Processamento Pix com Entrega Automática
          </h1>
          <p className="text-xs text-slate-400">Gateway de pagamento com confirmação instantânea por webhook</p>
        </div>
      </div>

      {/* Gateway selector */}
      <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-5 space-y-3">
        <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider block">
          Selecione o Gateway de Pagamentos Conectado:
        </label>
        <div className="grid grid-cols-3 gap-3">
          {[
            { id: 'mercadopago', name: 'Mercado Pago Pix', desc: 'Taxa 0.99% • Instantâneo' },
            { id: 'asaas', name: 'Asaas Pagamentos', desc: 'Split automático de comissões' },
            { id: 'efi', name: 'Efí (Gerencianet)', desc: 'Certificado mTLS seguro' },
          ].map((g) => (
            <div
              key={g.id}
              onClick={() => setGateway(g.id as any)}
              className={`p-3.5 rounded-xl border text-xs cursor-pointer transition-all ${
                gateway === g.id
                  ? 'border-emerald-400 bg-emerald-500/10 text-white font-medium'
                  : 'border-[#1E293B] bg-[#0B132B] text-slate-400 hover:border-slate-700'
              }`}
            >
              <div className="font-bold text-white">{g.name}</div>
              <div className="text-[11px] text-slate-400 mt-1">{g.desc}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Step 1: Form */}
      {checkoutStep === 'form' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 space-y-4">
            <h2 className="text-sm font-bold text-white">Dados do Comprador</h2>

            <div>
              <label className="text-xs text-slate-300 block mb-1">Nome Completo:</label>
              <input
                type="text"
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-xs text-white"
              />
            </div>

            <div>
              <label className="text-xs text-slate-300 block mb-1">E-mail para Recebimento do E-book:</label>
              <input
                type="email"
                value={customerEmail}
                onChange={(e) => setCustomerEmail(e.target.value)}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-xs text-white"
              />
            </div>

            <div>
              <label className="text-xs text-slate-300 block mb-1">WhatsApp para Notificação:</label>
              <input
                type="text"
                value={customerPhone}
                onChange={(e) => setCustomerPhone(e.target.value)}
                className="w-full bg-[#0B132B] border border-[#1E293B] rounded-lg p-2.5 text-xs text-white"
              />
            </div>
          </div>

          <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-6 flex flex-col justify-between">
            <div className="space-y-3">
              <h2 className="text-sm font-bold text-white">Resumo do Pedido</h2>
              <div className="p-3 bg-[#0B132B] rounded-xl border border-[#1E293B]">
                <div className="text-xs text-slate-400">Produto Digital:</div>
                <div className="text-sm font-bold text-white">{selectedProduct.title}</div>
                <div className="text-xs text-emerald-400 font-semibold mt-2">
                  Total: R$ {selectedProduct.offerPrice?.toFixed(2)}
                </div>
              </div>

              <div className="text-xs text-slate-400 space-y-1">
                <div className="flex items-center text-emerald-400">
                  <ShieldCheck className="w-3.5 h-3.5 mr-1" /> Checkout Criptografado SSL
                </div>
                <div>Liberação automática no Pix em menos de 10 segundos</div>
              </div>
            </div>

            <button
              onClick={handleGeneratePix}
              className="w-full mt-4 flex items-center justify-center space-x-2 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs transition-all shadow-lg cursor-pointer"
            >
              <QrCode className="w-4 h-4" />
              <span>Gerar QR Code Pix de R$ {selectedProduct.offerPrice?.toFixed(2)}</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 2: Pix QR Code display & simulator */}
      {checkoutStep === 'pix' && (
        <div className="bg-[#131D31] border border-[#1E293B] rounded-2xl p-8 text-center space-y-5">
          <div className="inline-block p-4 bg-white rounded-2xl shadow-xl">
            {/* Render realistic QR code placeholder visual */}
            <div className="w-48 h-48 bg-slate-900 rounded-lg flex flex-col items-center justify-center text-white p-4">
              <QrCode className="w-24 h-24 text-emerald-400" />
              <span className="text-[10px] text-slate-400 mt-2">PIX INSTANTÂNEO BACEN</span>
            </div>
          </div>

          <div>
            <h2 className="text-lg font-bold text-white">Aponte o app do seu banco e pague via Pix</h2>
            <p className="text-xs text-slate-400 mt-1">Valor: R$ {selectedProduct.offerPrice?.toFixed(2)}</p>
          </div>

          {/* Pix Copy and Paste */}
          <div className="max-w-md mx-auto flex items-center space-x-2">
            <input
              type="text"
              readOnly
              value={pixKey}
              className="flex-1 bg-[#0B132B] border border-[#1E293B] rounded-xl px-3 py-2 text-xs text-slate-400 font-mono truncate"
            />
            <button
              onClick={handleCopyPix}
              className="flex items-center space-x-1 px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-xl cursor-pointer"
            >
              {copiedPix ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copiedPix ? 'Copiado!' : 'Copiar'}</span>
            </button>
          </div>

          {/* Webhook test simulator */}
          <div className="pt-4 border-t border-[#1E293B] max-w-md mx-auto">
            <button
              onClick={handleSimulateWebhookPayment}
              className="w-full flex items-center justify-center space-x-2 py-3 rounded-xl bg-gradient-to-r from-emerald-600 to-green-500 hover:from-emerald-500 hover:to-green-400 text-white font-bold text-xs shadow-lg cursor-pointer"
            >
              <CheckCircle className="w-4 h-4" />
              <span>Simular Confirmação Bancária (Webhook Pago)</span>
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Delivered Confirmation */}
      {checkoutStep === 'delivered' && (
        <div className="bg-[#131D31] border border-emerald-500/30 rounded-2xl p-8 text-center space-y-6">
          <div className="w-16 h-16 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center mx-auto border border-emerald-500/30">
            <CheckCircle className="w-8 h-8" />
          </div>

          <div>
            <h2 className="text-2xl font-bold text-white">Pagamento Confirmado!</h2>
            <p className="text-xs text-slate-300 mt-1 max-w-md mx-auto">
              O infoproduto "{selectedProduct.title}" foi entregue automaticamente para {customerEmail} e o comprovante foi despachado via WhatsApp.
            </p>
          </div>

          <div className="flex justify-center space-x-3">
            <button
              onClick={handleDownloadDeliveredPdf}
              className="flex items-center space-x-2 px-6 py-3 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-semibold text-xs shadow transition-all cursor-pointer"
            >
              <Download className="w-4 h-4" />
              <span>Baixar Arquivo PDF Liberado</span>
            </button>

            <button
              onClick={() => setCheckoutStep('form')}
              className="px-5 py-3 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold text-xs transition-all cursor-pointer"
            >
              Novo Pagamento
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
