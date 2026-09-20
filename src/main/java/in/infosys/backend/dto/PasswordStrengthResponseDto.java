package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class PasswordStrengthResponseDto {

    private int score;

    private String strength;

    private List<String> suggestions;


}
