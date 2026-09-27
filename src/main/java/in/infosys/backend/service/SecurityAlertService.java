package in.infosys.backend.service;

import in.infosys.backend.dto.SecurityAlertResponseDto;
import in.infosys.backend.entity.SecurityAlert;
import in.infosys.backend.repository.LoginActivityRepository;
import in.infosys.backend.repository.SecurityAlertRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityAlertService {

    private static final int FAILED_LOGIN_THRESHOLD = 5;

    private static final int TIME_WINDOW_MINUTES = 10;

    private static final String FAILED_LOGIN_ALERT =
            "MULTIPLE_FAILED_LOGINS";

    private final LoginActivityRepository loginActivityRepository;

    private final SecurityAlertRepository securityAlertRepository;

    public SecurityAlertService(
            LoginActivityRepository loginActivityRepository,
            SecurityAlertRepository securityAlertRepository) {

        this.loginActivityRepository =
                loginActivityRepository;

        this.securityAlertRepository =
                securityAlertRepository;
    }

    // method 1. --? checking
    public void checkForSuspiciousActivity(
            String username,
            String ipAddress) {

        LocalDateTime windowStart =
                LocalDateTime.now()
                        .minusMinutes(TIME_WINDOW_MINUTES);

        long failedAttempts =
                loginActivityRepository
                        .countByUsernameAndIpAddressAndSuccessFalseAndTimestampAfter(
                                username,
                                ipAddress,
                                windowStart
                        );

        if (failedAttempts >= FAILED_LOGIN_THRESHOLD) {

            boolean alertAlreadyExists =
                    securityAlertRepository
                            .existsByUsernameAndIpAddressAndTypeAndCreatedAtAfter(
                                    username,
                                    ipAddress,
                                    FAILED_LOGIN_ALERT,
                                    windowStart
                            );

            if (!alertAlreadyExists) {

                SecurityAlert alert =
                        new SecurityAlert();

                alert.setUsername(username);
                alert.setIpAddress(ipAddress);

                alert.setType(
                        FAILED_LOGIN_ALERT
                );

                alert.setMessage(
                        "Multiple failed login attempts detected. "
                                + "There were "
                                + failedAttempts
                                + " failed login attempts within "
                                + TIME_WINDOW_MINUTES
                                + " minutes."
                );

                alert.setCreatedAt(
                        LocalDateTime.now()
                );

                alert.setRead(false);

                securityAlertRepository.save(alert);
            }
        }
    }

    // 2. method --> get alerts
    public List<SecurityAlertResponseDto>
    getMyAlerts() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        return securityAlertRepository
                .findAllByUsernameOrderByCreatedAtDesc(
                        username
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 3. method --? mark as read
    public void markAsRead(Long alertId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        SecurityAlert alert =
                securityAlertRepository
                        .findById(alertId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Alert not found"
                                )
                        );

        if (!alert.getUsername()
                .equals(username)) {

            throw new RuntimeException(
                    "You cannot modify this alert"
            );
        }

        alert.setRead(true);

        securityAlertRepository.save(alert);
    }

    // method 4--? toDto
    private SecurityAlertResponseDto toDto(
            SecurityAlert alert) {

        return new SecurityAlertResponseDto(
                alert.getId(),
                alert.getUsername(),
                alert.getIpAddress(),
                alert.getType(),
                alert.getMessage(),
                alert.getCreatedAt(),
                alert.isRead()
        );
    }
}
