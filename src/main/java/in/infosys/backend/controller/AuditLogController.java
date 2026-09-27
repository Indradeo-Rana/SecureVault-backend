package in.infosys.backend.controller;

import in.infosys.backend.dto.AuditLogResponseDto;
import in.infosys.backend.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(
            AuditLogService auditLogService) {

        this.auditLogService =
                auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAuditLogs() {
        return ResponseEntity.ok(auditLogService.getMyAuditLogs());
    }
}
