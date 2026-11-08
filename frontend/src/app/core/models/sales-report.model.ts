// ============================================================
// MODELOS DEL MÓDULO DE REPORTES GERENCIALES (HU02)
// Endpoint: GET /api/reports/monthly-sales
// ============================================================

/**
 * Estado del comparativo contra el mes anterior para un KPI.
 */
export type ComparisonState = 'NORMAL' | 'SIN_COMPARATIVO' | 'SIN_DATOS';

/**
 * Métrica de un KPI con su valor actual, el del mes anterior
 * y las variaciones (absoluta y porcentual) correspondientes.
 * `variacionPorcentual` es `null` cuando no hay comparativo previo (S/C).
 */
export interface KpiMetric {
  valorActual: number;
  valorAnterior: number;
  variacionAbsoluta: number;
  variacionPorcentual: number | null;
  estadoComparacion: ComparisonState;
}

/**
 * Punto de la serie temporal de ventas diarias.
 */
export interface DailySalesPoint {
  fecha: string; // ISO date, ej: "2026-11-01"
  ingresos: number;
}

/**
 * Respuesta completa del informe mensual de ventas y rendimiento.
 */
export interface MonthlySalesReportResponse {
  year: number;
  month: number;
  generatedAt: string; // ISO 8601
  sinDatos: boolean;
  mensaje: string | null;
  ingresosTotales: KpiMetric;
  ordenesExitosas: KpiMetric;
  ticketPromedio: KpiMetric;
  tasaAprobacionPagos: KpiMetric;
  tendenciaVentasDiarias: DailySalesPoint[];
}
