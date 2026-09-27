package in.infosys.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class PasswordReuseResponseDto {

    private int count;

    private List<String> passwords;
}
