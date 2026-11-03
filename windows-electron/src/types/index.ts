export interface Chapter {
  id: string;
  number: number;
  title: string;
  content: string;
  summary: string;
  generatedWithAi: boolean;
}

export interface Product {
  id: string;
  userId: string;
  title: string;
  subtitle: string;
  niche: string;
  targetAudience: string;
  offerPrice: number;
  status: 'draft' | 'in_progress' | 'completed' | 'published';
  chapters: Chapter[];
  createdAt: number;
  updatedAt: number;
  coverPrompt?: string;
  coverImageUrl?: string;
}

export interface LandingPage {
  id: string;
  productId: string;
  headline: string;
  subheadline: string;
  painPoints: string[];
  benefits: string[];
  bonuses: string[];
  testimonials: Array<{ name: string; text: string; role: string }>;
  faq: Array<{ question: string; answer: string }>;
  ctaText: string;
  slug: string;
  published: boolean;
}

export interface Order {
  id: string;
  productId: string;
  productTitle: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  amount: number;
  paymentMethod: 'PIX' | 'CREDIT_CARD';
  status: 'PAID' | 'PENDING' | 'CANCELLED';
  delivered: boolean;
  pixQrCode?: string;
  pixCopyPaste?: string;
  createdAt: number;
}

export interface Lead {
  id: string;
  name: string;
  email: string;
  phone: string;
  stage: 'NOVO' | 'INTERESSADO' | 'CHECKOUT' | 'CLIENTE';
  notes: string;
  createdAt: number;
}

export interface UserProfile {
  id: string;
  email: string;
  name: string;
  plan: 'FREE' | 'PRO' | 'ENTERPRISE';
  monthlyQuota: number;
  usedQuota: number;
  role: 'user' | 'admin';
}
