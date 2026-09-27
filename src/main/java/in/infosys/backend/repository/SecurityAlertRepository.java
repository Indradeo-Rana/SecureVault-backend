package in.infosys.backend.repository;

import in.infosys.backend.entity.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SecurityAlertRepository
    extends JpaRepository<SecurityAlert , Long> {

    // All alerts of a specific user
    List<SecurityAlert>
    findAllByUsernameOrderByCreatedAtDesc(String username);

    // Alerts of a specific user within a date/time range
    List<SecurityAlert> findAllByUsernameAndCreatedAtBetweenOrderByCreatedAtDesc(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );

    // All alerts - keep this if your existing code uses it
    List<SecurityAlert> findAllByOrderByCreatedAtDesc();

    // Check whether a similar alert already exists
    boolean existsByUsernameAndIpAddressAndTypeAndCreatedAtAfter(
            String username,
            String ipAddress,
            String type,
            LocalDateTime time
    );

    // Current user's unread/active alerts
    List<SecurityAlert> findAllByUsernameAndReadFalseOrderByCreatedAtDesc(
            String username
    );

    // Current user's unread/active alerts within date range
    List<SecurityAlert>
    findAllByUsernameAndReadFalseAndCreatedAtBetweenOrderByCreatedAtDesc(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );
}