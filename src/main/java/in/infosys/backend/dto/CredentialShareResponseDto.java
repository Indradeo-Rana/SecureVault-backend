package in.infosys.backend.dto;

import in.infosys.backend.entity.SharePermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialShareResponseDto {

    private Long id;

    private Long credentialId;

    private String credentialTitle;

    private String ownerUsername;

    private String sharedWithUsername;

    private SharePermission permission;

    private LocalDateTime expiresAt;

    private boolean revoked;
}
