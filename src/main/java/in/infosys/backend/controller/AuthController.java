package in.infosys.backend.controller;

import in.infosys.backend.dto.AuthResponseDto;
import in.infosys.backend.dto.LoginRequestDto;
import in.infosys.backend.dto.RegisterRequestDto;
import in.infosys.backend.entity.User;
import in.infosys.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // POST --> /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody RegisterRequestDto request){
        User createdUser = userService.registerUser(request);
        return ResponseEntity
                .status(201)
                .body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody LoginRequestDto request) {
        AuthResponseDto response = userService.login(request);

        return ResponseEntity.ok(response);
    }


}
