package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordHealthResponseDto {

    private int totalPasswords;

    private int strongPasswords;

    private int mediumPasswords;

    private int weakPasswords;
}
