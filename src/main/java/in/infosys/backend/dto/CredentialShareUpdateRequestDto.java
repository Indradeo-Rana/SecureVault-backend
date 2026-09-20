package in.infosys.backend.dto;

import in.infosys.backend.entity.SharePermission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialShareUpdateRequestDto {

    private SharePermission permission;
}
