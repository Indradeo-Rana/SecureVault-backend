package in.infosys.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCredentialRequestDto {

    private String title;
    private String username;
    private String password;
    private String website;
    private String notes;
}
