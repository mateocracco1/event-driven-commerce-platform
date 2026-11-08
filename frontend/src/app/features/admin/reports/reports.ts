import { Component, ElementRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule, MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Chart, registerables } from 'chart.js';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';
import { SalesReportService } from '../../../services/sales-report.service';
import { KpiMetric, MonthlySalesReportResponse } from '../../../core/models/sales-report.model';

Chart.register(...registerables);

interface MonthOption {
  value: number;
  label: string;
}

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    MatTooltipModule,
  ],
  templateUrl: './reports.html',
  styleUrl: './reports.scss',
})
export class ReportsComponent implements OnInit {
  private service = inject(SalesReportService);

  @ViewChild('chartCanvas') chartCanvas?: ElementRef<HTMLCanvasElement>;
  private chart?: Chart;

  report = signal<MonthlySalesReportResponse | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  year = signal(new Date().getFullYear());
  month = signal(new Date().getMonth() + 1);
  compareEnabled = signal(true);

  years: number[] = [];

  readonly months: MonthOption[] = [
    { value: 1, label: 'Enero' },
    { value: 2, label: 'Febrero' },
    { value: 3, label: 'Marzo' },
    { value: 4, label: 'Abril' },
    { value: 5, label: 'Mayo' },
    { value: 6, label: 'Junio' },
    { value: 7, label: 'Julio' },
    { value: 8, label: 'Agosto' },
    { value: 9, label: 'Septiembre' },
    { value: 10, label: 'Octubre' },
    { value: 11, label: 'Noviembre' },
    { value: 12, label: 'Diciembre' },
  ];

  private readonly monthShortNames = [
    'ene', 'feb', 'mar', 'abr', 'may', 'jun',
    'jul', 'ago', 'sep', 'oct', 'nov', 'dic',
  ];

  private moneyFormatter = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  private compactFormatter = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
    notation: 'compact',
    maximumFractionDigits: 1,
  });

  private integerFormatter = new Intl.NumberFormat('es-AR', { maximumFractionDigits: 0 });

  private percentFormatter = new Intl.NumberFormat('es-AR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  ngOnInit(): void {
    const currentYear = new Date().getFullYear();
    this.years = Array.from({ length: 6 }, (_, i) => currentYear - i);
    this.loadReport();
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }

  loadReport(): void {
    this.loading.set(true);
    this.error.set(null);

    this.service.getMonthlySalesReport(this.year(), this.month()).subscribe({
      next: (data) => {
        this.report.set(data);
        this.loading.set(false);
        setTimeout(() => this.renderChart(), 0);
      },
      error: () => {
        this.loading.set(false);
        this.report.set(null);
        this.error.set('No se pudo cargar el informe. Intente nuevamente.');
        this.destroyChart();
      },
    });
  }

  onMonthChange(event: Event): void {
    const value = Number((event.target as HTMLSelectElement).value);
    this.month.set(value);
    this.loadReport();
  }

  onYearChange(event: Event): void {
    const value = Number((event.target as HTMLSelectElement).value);
    this.year.set(value);
    this.loadReport();
  }

  onCompareChange(event: MatSlideToggleChange): void {
    this.compareEnabled.set(event.checked);
  }

  // ── Formato ──────────────────────────────────────────────

  formatMoney(value: number): string {
    return this.moneyFormatter.format(value);
  }

  formatCount(value: number): string {
    return this.integerFormatter.format(value);
  }

  formatPercent(value: number): string {
    return this.percentFormatter.format(value) + '%';
  }

  variationInfo(metric: KpiMetric): { label: string; cssClass: string; icon: string } {
    const pct = metric.variacionPorcentual;

    if (metric.estadoComparacion === 'SIN_COMPARATIVO' || pct == null) {
      return { label: 'S/C', cssClass: 'variation-neutral', icon: '' };
    }

    if (pct > 0) {
      return { label: '+' + this.percentFormatter.format(pct) + '%', cssClass: 'variation-up', icon: 'arrow_upward' };
    }

    if (pct < 0) {
      return { label: this.percentFormatter.format(pct) + '%', cssClass: 'variation-down', icon: 'arrow_downward' };
    }

    return { label: '0,00%', cssClass: 'variation-neutral', icon: '' };
  }

  monthName(month: number): string {
    return this.months[month - 1]?.label ?? String(month);
  }

  subtitleText(): string {
    const r = this.report();
    const generated = r ? new Date(r.generatedAt) : new Date();
    return `${this.monthName(this.month())} ${this.year()} · Generado el ${generated.toLocaleString('es-AR')}`;
  }

  // ── Gráfico ──────────────────────────────────────────────

  private renderChart(): void {
    this.destroyChart();

    const data = this.report();
    const canvas = this.chartCanvas?.nativeElement;
    if (!data || data.sinDatos || !canvas) {
      return;
    }

    this.chart = new Chart(canvas, {
      type: 'line',
      data: {
        labels: data.tendenciaVentasDiarias.map((p) => this.formatDayLabel(p.fecha)),
        datasets: [
          {
            label: 'Ingresos aprobados',
            data: data.tendenciaVentasDiarias.map((p) => p.ingresos),
            borderColor: '#1976d2',
            backgroundColor: 'rgba(25, 118, 210, 0.12)',
            fill: true,
            tension: 0.3,
            borderWidth: 2,
            pointRadius: 2,
            pointBackgroundColor: '#1976d2',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => this.formatMoney(Number(ctx.parsed.y)),
            },
          },
        },
        scales: {
          x: { grid: { display: false } },
          y: {
            beginAtZero: true,
            ticks: { callback: (value) => this.formatMoneyCompact(Number(value)) },
          },
        },
      },
    });
  }

  private formatMoneyCompact(value: number): string {
    return this.compactFormatter.format(value);
  }

  private formatDayLabel(fecha: string): string {
    const [year, month, day] = fecha.split('-').map(Number);
    const dayStr = String(day).padStart(2, '0');
    return `${dayStr} ${this.monthShortNames[month - 1]}`;
  }

  private destroyChart(): void {
    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }
  }

  // ── PDF ──────────────────────────────────────────────────

  async downloadPdf(): Promise<void> {
    const element = document.getElementById('report-export');
    if (!element) {
      return;
    }

    const canvas = await html2canvas(element, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff',
    });

    const imgData = canvas.toDataURL('image/png');
    const pdf = new jsPDF('p', 'mm', 'a4');
    const pageWidth = pdf.internal.pageSize.getWidth();

    pdf.setFontSize(16);
    pdf.text('Informe Mensual de Ventas y Rendimiento', pageWidth / 2, 15, { align: 'center' });

    pdf.setFontSize(10);
    pdf.setTextColor(90);
    pdf.text(this.subtitleText(), pageWidth / 2, 22, { align: 'center' });

    const margin = 10;
    const imgWidth = pageWidth - margin * 2;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;

    pdf.addImage(imgData, 'PNG', margin, 30, imgWidth, imgHeight);
    pdf.save(`informe-ventas-${this.year()}-${this.month()}.pdf`);
  }
}
