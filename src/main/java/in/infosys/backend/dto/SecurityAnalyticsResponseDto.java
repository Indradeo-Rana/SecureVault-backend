package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAnalyticsResponseDto {

    private LocalDate startDate;

    private LocalDate endDate;

    private long successfulLogins;

    private long failedLogins;

    private long suspiciousActivities;

    private long activeSecurityAlerts;

    private List<RecentSecurityActivityDto> recentSecurityActivity;
}