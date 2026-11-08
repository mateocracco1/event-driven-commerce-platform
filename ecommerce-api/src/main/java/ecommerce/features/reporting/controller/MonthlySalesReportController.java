package ecommerce.features.reporting.controller;

import ecommerce.features.reporting.dto.MonthlySalesReportResponse;
import ecommerce.features.reporting.service.MonthlySalesReportService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class MonthlySalesReportController {

    private final MonthlySalesReportService monthlySalesReportService;

    @GetMapping("/monthly-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlySalesReportResponse> getMonthlySalesReport(
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        return ResponseEntity.ok(monthlySalesReportService.generate(year, month));
    }
}
