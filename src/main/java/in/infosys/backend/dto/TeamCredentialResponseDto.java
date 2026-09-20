package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamCredentialResponseDto {

    private Long id;
    private Long credentialId;
    private String title;
    private String username;
    private String password;
    private String website;
    private String notes;
}
