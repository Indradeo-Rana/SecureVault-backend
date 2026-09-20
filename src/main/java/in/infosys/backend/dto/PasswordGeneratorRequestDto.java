package in.infosys.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordGeneratorRequestDto {

    private int length; // length of the password to be generated

    private boolean includeUppercase;

    private boolean includeLowercase;

    private boolean includeNumbers;

    private boolean includeSpecialCharacters;
}
