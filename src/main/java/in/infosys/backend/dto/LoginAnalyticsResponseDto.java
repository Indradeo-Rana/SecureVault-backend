package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor

public class LoginAnalyticsResponseDto {

    private long totalAttempts;

    private long successfulLogins;

    private long failedLogins;
}
