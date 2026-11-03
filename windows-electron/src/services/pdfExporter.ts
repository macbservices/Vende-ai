import { jsPDF } from 'jspdf';
import { Product } from '../types';

export class PdfExporter {
  public static async exportEbook(product: Product): Promise<{ success: boolean; path?: string; error?: string }> {
    try {
      const doc = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4',
      });

      const pageWidth = doc.internal.pageSize.getWidth();
      const pageHeight = doc.internal.pageSize.getHeight();
      const margin = 20;
      const contentWidth = pageWidth - margin * 2;

      // 1. Cover Page
      doc.setFillColor(11, 19, 43); // Dark #0B132B
      doc.rect(0, 0, pageWidth, pageHeight, 'F');

      doc.setTextColor(6, 182, 212); // Cyan accent
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(14);
      doc.text('VENDEIA SAAS • INFOPRODUTO OFICIAL', pageWidth / 2, 60, { align: 'center' });

      doc.setTextColor(255, 255, 255);
      doc.setFontSize(28);
      const splitTitle = doc.splitTextToSize(product.title, contentWidth);
      doc.text(splitTitle, pageWidth / 2, 85, { align: 'center' });

      doc.setTextColor(148, 163, 184); // Slate 400
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(14);
      const splitSubtitle = doc.splitTextToSize(product.subtitle || 'Guia Prático e Definitivo', contentWidth);
      doc.text(splitSubtitle, pageWidth / 2, 115, { align: 'center' });

      doc.setTextColor(248, 250, 252);
      doc.setFontSize(11);
      doc.text(`Criado com Inteligência Artificial Gemini • Nicho: ${product.niche}`, pageWidth / 2, pageHeight - 35, { align: 'center' });

      // 2. Chapters
      product.chapters.forEach((chapter, index) => {
        doc.addPage();

        // Header
        doc.setTextColor(37, 99, 235); // Blue primary
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(12);
        doc.text(`CAPÍTULO ${chapter.number || index + 1}`, margin, 25);

        doc.setTextColor(15, 23, 42); // Slate 900
        doc.setFontSize(20);
        const chapterTitleLines = doc.splitTextToSize(chapter.title, contentWidth);
        doc.text(chapterTitleLines, margin, 35);

        // Divider
        doc.setDrawColor(226, 232, 240);
        doc.setLineWidth(0.5);
        doc.line(margin, 42, pageWidth - margin, 42);

        // Body Text
        doc.setTextColor(51, 65, 85);
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(11);

        const bodyLines = doc.splitTextToSize(chapter.content || 'Conteúdo em elaboração.', contentWidth);
        let cursorY = 50;

        bodyLines.forEach((line: string) => {
          if (cursorY > pageHeight - margin) {
            doc.addPage();
            cursorY = margin;
          }
          doc.text(line, margin, cursorY);
          cursorY += 6;
        });

        // Page number
        doc.setFontSize(9);
        doc.setTextColor(148, 163, 184);
        doc.text(`Página ${doc.getNumberOfPages()}`, pageWidth / 2, pageHeight - 10, { align: 'center' });
      });

      // Save using Electron native Windows dialog or Web fallback
      const base64Pdf = doc.output('datauristring').split(',')[1];
      const filename = `${product.title.replace(/[^a-zA-Z0-9]/g, '_')}.pdf`;

      if (window.electronAPI) {
        const result = await window.electronAPI.saveFile({
          defaultPath: filename,
          filters: [{ name: 'Documento PDF', extensions: ['pdf'] }],
          contentBase64: base64Pdf,
        });

        if (result.success) {
          return { success: true, path: result.filePath };
        } else if (result.canceled) {
          return { success: false, error: 'Exportação cancelada pelo usuário.' };
        } else {
          return { success: false, error: result.error };
        }
      } else {
        // Fallback for browser preview
        doc.save(filename);
        return { success: true, path: filename };
      }
    } catch (err: any) {
      return { success: false, error: err.message };
    }
  }
}
