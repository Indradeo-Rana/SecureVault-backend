package in.infosys.backend.service;

import in.infosys.backend.dto.AuditLogResponseDto;
import in.infosys.backend.entity.AuditLog;
import in.infosys.backend.repository.AuditLogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository =
                auditLogRepository;
    }

    // --> log method
    public void log(
            String action,
            String resourceType,
            Long resourceId,
            String description) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        AuditLog auditLog =
                new AuditLog();

        auditLog.setUsername(username);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setDescription(description);
        auditLog.setTimestamp(
                LocalDateTime.now()
        );

        auditLogRepository.save(auditLog);
    }

    // 2 method to get list of audit logs
    public List<AuditLogResponseDto>
    getMyAuditLogs() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return auditLogRepository
                .findAllByUsernameOrderByTimestampDesc(
                        username
                )
                .stream()
                .map(log ->
                        new AuditLogResponseDto(
                                log.getId(),
                                log.getUsername(),
                                log.getAction(),
                                log.getResourceType(),
                                log.getResourceId(),
                                log.getDescription(),
                                log.getTimestamp()
                        )
                )
                .toList();
    }
}
