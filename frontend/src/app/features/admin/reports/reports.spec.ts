import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ReportsComponent } from './reports';
import { SalesReportService } from '../../../services/sales-report.service';
import { MonthlySalesReportResponse } from '../../../core/models/sales-report.model';

vi.mock('chart.js', () => {
  class MockChart {
    static register(..._args: unknown[]) {}
    destroy() {}
  }
  return { Chart: MockChart, registerables: [] };
});

function buildReport(overrides: Partial<MonthlySalesReportResponse> = {}): MonthlySalesReportResponse {
  return {
    year: 2026,
    month: 11,
    generatedAt: '2026-11-07T14:30:00Z',
    sinDatos: false,
    mensaje: null,
    ingresosTotales: {
      valorActual: 1240500,
      valorAnterior: 980000,
      variacionAbsoluta: 260500,
      variacionPorcentual: 27.55,
      estadoComparacion: 'NORMAL',
    },
    ordenesExitosas: {
      valorActual: 320,
      valorAnterior: 280,
      variacionAbsoluta: 40,
      variacionPorcentual: 14.29,
      estadoComparacion: 'NORMAL',
    },
    ticketPromedio: {
      valorActual: 3876.56,
      valorAnterior: 3500,
      variacionAbsoluta: 376.56,
      variacionPorcentual: 10.76,
      estadoComparacion: 'NORMAL',
    },
    tasaAprobacionPagos: {
      valorActual: 84.5,
      valorAnterior: 80,
      variacionAbsoluta: 4.5,
      variacionPorcentual: 5.63,
      estadoComparacion: 'NORMAL',
    },
    tendenciaVentasDiarias: [
      { fecha: '2026-11-01', ingresos: 1240500 },
      { fecha: '2026-11-02', ingresos: 0 },
    ],
    ...overrides,
  };
}

describe('ReportsComponent', () => {
  let component: ReportsComponent;
  let fixture: ComponentFixture<ReportsComponent>;
  let mockService: { getMonthlySalesReport: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    mockService = {
      getMonthlySalesReport: vi.fn().mockReturnValue(of(buildReport())),
    };

    await TestBed.configureTestingModule({
      imports: [ReportsComponent],
      providers: [{ provide: SalesReportService, useValue: mockService }],
    }).compileComponents();

    fixture = TestBed.createComponent(ReportsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('renderiza los 4 KPIs con sus valores formateados', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const text = ((fixture.nativeElement as HTMLElement).textContent ?? '').replace(/\u00A0/g, ' ');

    expect(text).toContain('Ingresos Totales');
    expect(text).toContain('Órdenes Exitosas');
    expect(text).toContain('Ticket Promedio');
    expect(text).toContain('Aprobación de Pagos');
    expect(text).toContain('$ 1.240.500,00');
  });

  it('muestra el badge de variación con el toggle activo y lo oculta al desactivarlo', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.compareEnabled()).toBe(true);
    expect((fixture.nativeElement as HTMLElement).querySelector('.kpi-variation')).toBeTruthy();

    component.compareEnabled.set(false);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('.kpi-variation')).toBeNull();
  });

  it('cuando sinDatos es true muestra el mensaje y oculta el gráfico', async () => {
    mockService.getMonthlySalesReport = vi.fn().mockReturnValue(
      of(buildReport({ sinDatos: true, mensaje: 'No se registraron ventas aprobadas en el período seleccionado', tendenciaVentasDiarias: [] })),
    );

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('No se registraron ventas aprobadas en el período seleccionado');
    expect(element.querySelector('canvas')).toBeNull();
  });
});
