package in.infosys.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SecurityAnalyticsRequestDto {

    private String range;

    private LocalDate startDate;

    private LocalDate endDate;
}
