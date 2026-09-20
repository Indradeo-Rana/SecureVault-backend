package in.infosys.backend.controller;

import in.infosys.backend.dto.PasswordGeneratorRequestDto;
import in.infosys.backend.dto.PasswordGeneratorResponseDto;
import in.infosys.backend.service.PasswordGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-generator")
public class PasswordGeneratorController {

    private final PasswordGeneratorService passwordGeneratorService;

    public PasswordGeneratorController(PasswordGeneratorService passwordGeneratorService) {
        this.passwordGeneratorService = passwordGeneratorService;
    }

    @PostMapping
    public ResponseEntity<PasswordGeneratorResponseDto> generatePasswordStrength(
            @RequestBody PasswordGeneratorRequestDto requestDto) {

        String password =
                passwordGeneratorService.generatePassword(requestDto);

        PasswordGeneratorResponseDto response = new PasswordGeneratorResponseDto(password);


        return ResponseEntity.ok(response);
    }
}
