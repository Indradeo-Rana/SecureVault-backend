package in.infosys.backend.controller;

import in.infosys.backend.dto.SecurityAnalyticsRequestDto;
import in.infosys.backend.dto.SecurityReportResponseDto;
import in.infosys.backend.service.SecurityReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/security")
public class SecurityReportController {

    private final SecurityReportService securityReportService;

    public SecurityReportController(SecurityReportService securityReportService) {
        this.securityReportService = securityReportService;
    }

    @PostMapping
    public ResponseEntity<SecurityReportResponseDto> generateReport(
            @RequestBody(required = false)
            SecurityAnalyticsRequestDto request) {

        return ResponseEntity.ok(
                securityReportService.generateReport(request)
        );
    }
}
