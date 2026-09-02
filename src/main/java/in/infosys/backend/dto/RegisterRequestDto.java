package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequestDto {

    private String username;
    private String email;
    private String password;


}
