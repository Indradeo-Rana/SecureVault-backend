package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class SecurityReportResponseDto {

    private LocalDate startDate;
    private LocalDate endDate;

    private long successfulLogins;
    private long failedLogins;

    private long suspiciousActivities;
    private long activeSecurityAlerts;

    private long totalPasswords;
    private long strongPasswords;
    private long mediumPasswords;
    private long weakPasswords;

    private long reusedPasswordGroups;
}
