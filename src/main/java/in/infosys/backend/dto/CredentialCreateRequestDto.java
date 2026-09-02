package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialCreateRequestDto {

    private String title;

    private String username;

    private String password;

    private String website;

    private String notes;
}
