package in.infosys.backend.repository;

import in.infosys.backend.entity.LoginActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginActivityRepository extends JpaRepository<LoginActivity, Long> {

    // Current user's complete login history
    List<LoginActivity> findAllByUsernameOrderByTimestampDesc(
            String username
    );

    // Current user's login activity within date/time range
    List<LoginActivity> findAllByUsernameAndTimestampBetweenOrderByTimestampDesc(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );

    // Count failed login attempts for suspicious activity detection
    long countByUsernameAndIpAddressAndSuccessFalseAndTimestampAfter(
            String username,
            String ipAddress,
            LocalDateTime timestamp
    );


}
