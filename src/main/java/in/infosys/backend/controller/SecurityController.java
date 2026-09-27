package in.infosys.backend.controller;

import in.infosys.backend.dto.LoginActivityResponseDto;
import in.infosys.backend.dto.SecurityAlertResponseDto;
import in.infosys.backend.entity.LoginActivity;
import in.infosys.backend.service.LoginActivityService;
import in.infosys.backend.service.SecurityAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private final LoginActivityService loginActivityService;
    private final SecurityAlertService securityAlertService;

    public SecurityController(
            LoginActivityService loginActivityService, SecurityAlertService securityAlertService) {
        this.loginActivityService = loginActivityService;
        this.securityAlertService = securityAlertService;
    }

    @GetMapping("/login-history")
    public ResponseEntity<List<LoginActivityResponseDto>> getLoginHistory() {

//        System.out.println("LOGIN HISTORY API HIT");

        return ResponseEntity.ok(
                loginActivityService.getLoginHistory()
        );
    }

    // methods from SecurityAlertService
    @GetMapping("/alerts")
    public ResponseEntity<List<SecurityAlertResponseDto>>
    getAlerts() {

        return ResponseEntity.ok(
                securityAlertService.getMyAlerts()
        );
    }

    @PatchMapping("/alerts/{id}/read")
    public ResponseEntity<String> markAlertAsRead(
            @PathVariable Long id) {

        securityAlertService.markAsRead(id);

        return ResponseEntity.ok(
                "Alert marked as read"
        );
    }
}