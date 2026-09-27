package in.infosys.backend.controller;

import in.infosys.backend.dto.LoginAnalyticsResponseDto;
import in.infosys.backend.dto.PasswordHealthResponseDto;
import in.infosys.backend.dto.PasswordReuseResponseDto;
import in.infosys.backend.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/password-health")
    public ResponseEntity<List<PasswordHealthResponseDto>> getPasswordHealth() {
            List<PasswordHealthResponseDto> passwordHealth = Collections.singletonList(analyticsService.getPasswordHealth());
            return ResponseEntity.ok(passwordHealth);
    }

    @GetMapping("/password-reuse")
    public ResponseEntity<List<PasswordReuseResponseDto>>
    getPasswordReuse() {

        return ResponseEntity.ok(
                analyticsService.getPasswordReuse()
        );
    }

    @GetMapping("/login")
    public ResponseEntity<LoginAnalyticsResponseDto>
    getLoginAnalytics() {

        return ResponseEntity.ok(
                analyticsService.getLoginAnalytics()
        );
    }
}
