package in.infosys.backend.dto;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginActivityResponseDto {

    private Long id;

    private String username;

    private boolean success;

    private String ipAddress;

    private LocalDateTime timestamp;

    public LoginActivityResponseDto(
            Long id, String username, String ipAddress,
            boolean success, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.ipAddress = ipAddress;
        this.success = success;
        this.timestamp = timestamp;
    }
}
