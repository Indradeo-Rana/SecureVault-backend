package in.infosys.backend.controller;

import in.infosys.backend.dto.PasswordStrengthRequestDto;
import in.infosys.backend.dto.PasswordStrengthResponseDto;
import in.infosys.backend.service.PasswordStrengthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password-strength")
public class PasswordStrengthController {

    private final PasswordStrengthService passwordStrengthService;

    public PasswordStrengthController(PasswordStrengthService passwordStrengthService) {
        this.passwordStrengthService = passwordStrengthService;
    }

    @PostMapping
    public ResponseEntity<PasswordStrengthResponseDto> analyzePassword(
            @RequestBody PasswordStrengthRequestDto requestDto
    ) {
        PasswordStrengthResponseDto response = passwordStrengthService
                        .analyzePassword(requestDto);

        return ResponseEntity.ok(response);
    }
}