package in.infosys.backend.controller;

import in.infosys.backend.dto.AuthResponseDto;
import in.infosys.backend.dto.LoginRequestDto;
import in.infosys.backend.dto.RegisterRequestDto;
import in.infosys.backend.entity.User;
import in.infosys.backend.service.LoginActivityService;
import in.infosys.backend.service.SecurityAlertService;
import in.infosys.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final LoginActivityService loginActivityService;
    private final SecurityAlertService securityAlertService;

    public AuthController(UserService userService, LoginActivityService loginActivityService, SecurityAlertService securityAlertService) {
        this.userService = userService;
        this.loginActivityService = loginActivityService;
        this.securityAlertService = securityAlertService;
    }

    // POST --> /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody RegisterRequestDto request){
        User createdUser = userService.registerUser(request);
        return ResponseEntity
                .status(201)
                .body(createdUser);
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponseDto> login(
//            @RequestBody LoginRequestDto request) {
//        AuthResponseDto response = userService.login(request);
//
//        return ResponseEntity.ok(response);
//    }

// updated login method to record login activity

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        try {

            AuthResponseDto response = userService.login(request);

            // SUCCESS
            loginActivityService.recordLogin(
                    request.getUsername(),
                    true,
                    ipAddress
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {

            // FAILURE
            loginActivityService.recordLogin(
                    request.getUsername(),
                    false,
                    ipAddress
            );

            // adding for suspicious activity check
            securityAlertService.checkForSuspiciousActivity(
                    request.getUsername(),
                    ipAddress
            );
            throw e;
        }
    }

}
