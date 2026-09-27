package in.infosys.backend.service;

import in.infosys.backend.dto.PasswordHealthResponseDto;
import in.infosys.backend.dto.PasswordReuseResponseDto;
import in.infosys.backend.dto.SecurityAnalyticsRequestDto;
import in.infosys.backend.dto.SecurityReportResponseDto;
import in.infosys.backend.entity.LoginActivity;
import in.infosys.backend.entity.SecurityAlert;
import in.infosys.backend.entity.User;
import in.infosys.backend.repository.LoginActivityRepository;
import in.infosys.backend.repository.SecurityAlertRepository;
import in.infosys.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SecurityReportService {

    private final LoginActivityRepository loginActivityRepository;
    private final SecurityAlertRepository securityAlertRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    public SecurityReportService(
            LoginActivityRepository loginActivityRepository,
            SecurityAlertRepository securityAlertRepository,
            UserRepository userRepository,
            AnalyticsService analyticsService) {

        this.loginActivityRepository = loginActivityRepository;
        this.securityAlertRepository = securityAlertRepository;
        this.userRepository = userRepository;
        this.analyticsService = analyticsService;
    }

    public SecurityReportResponseDto generateReport(
            SecurityAnalyticsRequestDto request) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User currentUser =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        DateRange dateRange =
                calculateDateRange(request);

        List<LoginActivity> loginActivities =
                loginActivityRepository
                        .findAllByUsernameAndTimestampBetweenOrderByTimestampDesc(
                                username,
                                dateRange.startDateTime(),
                                dateRange.endDateTime()
                        );

        long successfulLogins =
                loginActivities
                        .stream()
                        .filter(LoginActivity::isSuccess)
                        .count();

        long failedLogins =
                loginActivities
                        .stream()
                        .filter(activity -> !activity.isSuccess())
                        .count();

        List<SecurityAlert> alerts =
                securityAlertRepository
                        .findAllByUsernameAndCreatedAtBetweenOrderByCreatedAtDesc(
                                username,
                                dateRange.startDateTime(),
                                dateRange.endDateTime()
                        );

        long suspiciousActivities =
                alerts.size();

        long activeSecurityAlerts =
                alerts
                        .stream()
                        .filter(alert -> !alert.isRead())
                        .count();

        PasswordHealthResponseDto health =
                analyticsService.getPasswordHealth();

        List<PasswordReuseResponseDto> reuse =
                analyticsService.getPasswordReuse();

        return new SecurityReportResponseDto(
                dateRange.startDate(),
                dateRange.endDate(),

                successfulLogins,
                failedLogins,

                suspiciousActivities,
                activeSecurityAlerts,

                health.getTotalPasswords(),
                health.getStrongPasswords(),
                health.getMediumPasswords(),
                health.getWeakPasswords(),

                reuse.size()
        );
    }

    private DateRange calculateDateRange(
            SecurityAnalyticsRequestDto request) {

        LocalDate today = LocalDate.now();

        if (request == null ||
                request.getRange() == null ||
                request.getRange().isBlank()) {

            return new DateRange(
                    today,
                    today,
                    today.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
            );
        }

        String range =
                request.getRange()
                        .trim()
                        .toUpperCase();

        if ("TODAY".equals(range)) {

            return new DateRange(
                    today,
                    today,
                    today.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
            );
        }

        if ("LAST_7_DAYS".equals(range)) {

            LocalDate start =
                    today.minusDays(6);

            return new DateRange(
                    start,
                    today,
                    start.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
            );
        }

        if ("CUSTOM".equals(range)) {

            if (request.getStartDate() == null ||
                    request.getEndDate() == null) {

                throw new IllegalArgumentException(
                        "Start date and end date are required"
                );
            }

            if (request.getStartDate()
                    .isAfter(request.getEndDate())) {

                throw new IllegalArgumentException(
                        "Start date cannot be after end date"
                );
            }

            LocalDate start =
                    request.getStartDate();

            LocalDate end =
                    request.getEndDate();

            return new DateRange(
                    start,
                    end,
                    start.atStartOfDay(),
                    end.atTime(LocalTime.MAX)
            );
        }

        throw new IllegalArgumentException(
                "Invalid analytics range"
        );
    }

    private record DateRange(
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
    }
}
