package in.infosys.backend.repository;

import in.infosys.backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    // current user's complete audit log history
    List<AuditLog> findAllByUsernameOrderByTimestampDesc(
            String username
    );

    // Current user's audit logs within a selected date/time range
    List<AuditLog> findAllByUsernameAndTimestampBetweenOrderByTimestampDesc(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );
}
