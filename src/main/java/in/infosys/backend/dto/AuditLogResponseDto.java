package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AuditLogResponseDto {

    private Long id;
    private String username;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String description;
    private LocalDateTime timestamp;
}
