import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MonthlySalesReportResponse } from '../core/models/sales-report.model';

@Injectable({
  providedIn: 'root',
})
export class SalesReportService {
  private http = inject(HttpClient);
  private apiUrl = '/api/reports/monthly-sales';

  // ----------------------------------------------------------
  // GERENCIAL: Informe mensual de ventas y rendimiento
  // GET /api/reports/monthly-sales?year={year}&month={month}
  // ----------------------------------------------------------
  getMonthlySalesReport(year: number, month: number): Observable<MonthlySalesReportResponse> {
    return this.http.get<MonthlySalesReportResponse>(this.apiUrl, {
      params: { year, month },
      withCredentials: true,
    });
  }
}
