package in.infosys.backend.dto;

import in.infosys.backend.entity.SharePermission;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CredentialShareRequestDto {

    private Long credentialId;

    private String username;

    private SharePermission permission;

    private LocalDateTime expiresAt;

}
