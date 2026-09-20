package in.infosys.backend.dto;

import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialResponseDto {

    private Long id;

    private String title;

    private String username;

    private String password;

    private String website;

    private String notes;

    private Boolean deleted;

//    private String message;

    // convert Credential entity to list of CredentialResponseDto
    public static CredentialResponseDto fromEntity(Credential credential) {

        CredentialResponseDto response =
                new CredentialResponseDto();

        response.setId(credential.getId());
        response.setTitle(credential.getTitle());
        response.setUsername(credential.getUsername());
        response.setPassword(credential.getPassword());
        response.setWebsite(credential.getWebsite());
        response.setNotes(credential.getNotes());

        return response;
    }
}
