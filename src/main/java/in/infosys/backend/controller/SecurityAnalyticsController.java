package in.infosys.backend.controller;

import in.infosys.backend.dto.SecurityAnalyticsRequestDto;
import in.infosys.backend.dto.SecurityAnalyticsResponseDto;
import in.infosys.backend.service.SecurityAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/security")
public class SecurityAnalyticsController {

    private final SecurityAnalyticsService securityAnalyticsService;

    public SecurityAnalyticsController(
            SecurityAnalyticsService securityAnalyticsService) {

        this.securityAnalyticsService = securityAnalyticsService;
    }

    @PostMapping
    public ResponseEntity<SecurityAnalyticsResponseDto> getSecurityAnalytics(
            @RequestBody(required = false)
            SecurityAnalyticsRequestDto request) {

        return ResponseEntity.ok(
                securityAnalyticsService.getSecurityAnalytics(request)
        );
    }
}
