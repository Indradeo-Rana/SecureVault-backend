package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SecurityAlertResponseDto {

    private Long id;
    private String username;
    private String ipAddress;
    private String type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
}
