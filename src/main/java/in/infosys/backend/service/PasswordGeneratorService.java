package in.infosys.backend.service;

import in.infosys.backend.dto.PasswordGeneratorRequestDto;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorService {

    private static final String UPPERCASE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWERCASE =
            "abcdefghijklmnopqrstuvwxyz";

    private static final String NUMBERS =
            "0123456789";

    private static final String SPECIAL_CHARACTERS =
            "!@#$%^&*()-_=+[]{};:,.?";

    private final SecureRandom secureRandom = new SecureRandom();


    public String generatePassword(
            PasswordGeneratorRequestDto requestDto) {

        // 1. Validate request
        if (requestDto == null) {
            throw new IllegalArgumentException(
                    "Password generation request cannot be null"
            );
        }

        int length = requestDto.getLength();

        if (length <= 0) {
            throw new IllegalArgumentException(
                    "Password length must be greater than 0"
            );
        }


        // 2. Get selected character options

        boolean includeUppercase =
                requestDto.isIncludeUppercase();

        boolean includeLowercase =
                requestDto.isIncludeLowercase();

        boolean includeNumbers =
                requestDto.isIncludeNumbers();

        boolean includeSpecialCharacters =
                requestDto.isIncludeSpecialCharacters();


        // 3. Build character pool

        String characterPool = buildCharacterPool(
                includeUppercase,
                includeLowercase,
                includeNumbers,
                includeSpecialCharacters
        );


        // 4. Check whether user selected anything

        if (characterPool.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one character type must be selected"
            );
        }


        // 5. Count selected character types

        int selectedTypes = 0;

        if (includeUppercase) {
            selectedTypes++;
        }

        if (includeLowercase) {
            selectedTypes++;
        }

        if (includeNumbers) {
            selectedTypes++;
        }

        if (includeSpecialCharacters) {
            selectedTypes++;
        }


        // 6. Length must be enough for selected types

        if (length < selectedTypes) {
            throw new IllegalArgumentException(
                    "Password length is too short for the selected character types"
            );
        }


        // 7. Start creating password

        StringBuilder password = new StringBuilder();


        // 8. Guarantee one character from each selected type

        if (includeUppercase) {

            password.append(
                    randomCharacter(UPPERCASE)
            );
        }

        if (includeLowercase) {

            password.append(
                    randomCharacter(LOWERCASE)
            );
        }

        if (includeNumbers) {

            password.append(
                    randomCharacter(NUMBERS)
            );
        }

        if (includeSpecialCharacters) {

            password.append(
                    randomCharacter(SPECIAL_CHARACTERS)
            );
        }


        // 9. Fill remaining characters

        while (password.length() < length) {

            password.append(
                    randomCharacter(characterPool)
            );
        }


        // 10. Shuffle the password

        return shufflePassword(password);
    }


    // ------------------------------------------------
    // Build character pool
    // ------------------------------------------------

    private String buildCharacterPool(
            boolean includeUppercase,
            boolean includeLowercase,
            boolean includeNumbers,
            boolean includeSpecialCharacters) {

        StringBuilder characterPool =
                new StringBuilder();

        if (includeUppercase) {
            characterPool.append(UPPERCASE);
        }

        if (includeLowercase) {
            characterPool.append(LOWERCASE);
        }

        if (includeNumbers) {
            characterPool.append(NUMBERS);
        }

        if (includeSpecialCharacters) {
            characterPool.append(SPECIAL_CHARACTERS);
        }

        return characterPool.toString();
    }


    // ------------------------------------------------
    // Generate one random character
    // ------------------------------------------------

    private char randomCharacter(String characters) {

        int randomIndex =
                secureRandom.nextInt(characters.length());

        return characters.charAt(randomIndex);
    }


    // ------------------------------------------------
    // Shuffle generated password
    // ------------------------------------------------

    private String shufflePassword(
            StringBuilder password) {

        char[] characters =
                password.toString().toCharArray();

        for (int i = characters.length - 1;
             i > 0;
             i--) {

            int randomIndex =
                    secureRandom.nextInt(i + 1);

            char temp = characters[i];

            characters[i] =
                    characters[randomIndex];

            characters[randomIndex] =
                    temp;
        }

        return new String(characters);
    }
}

    // password may be like --> ABCabc123456 so not guaranteed to have --> Aq7@mP2#x9L!
    // for this we have to build a password that contains at least one character from each selected type, but this implementation does not guarantee that. It simply generates a random password from the selected character pool.

    // we'll use a 2-step generation strategy

