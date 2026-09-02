package in.infosys.backend.service;

import in.infosys.backend.dto.AuthResponseDto;
import in.infosys.backend.dto.LoginRequestDto;
import in.infosys.backend.dto.RegisterRequestDto;
import in.infosys.backend.entity.User;
import in.infosys.backend.repository.UserRepository;
import in.infosys.backend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User registerUser(RegisterRequestDto request) {
        if (request == null
                || request.getPassword() == null
                || request.getPassword().isBlank()
        ) {
            throw new IllegalArgumentException("Password is required");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        if (request == null
                || request.getUsername() == null
                || request.getPassword() == null
        ) {
            throw new BadCredentialsException("Username and password are required");
        }

        User user = userRepository
                .findByUsername(request.getUsername())
                .or(() -> userRepository
                        .findByEmail(request.getUsername())
                )
                .orElseThrow(() -> new BadCredentialsException(
                        "Invalid username or password")
                );

        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            throw new BadCredentialsException(
                    "Invalid username or password");
        }

        if (isBcryptHash(storedPassword)) {
            if (!passwordEncoder.matches(request.getPassword(), storedPassword)) {
                throw new BadCredentialsException("Invalid username or password");
            }
            // password is valid, generate JWT token and return response
            String token = jwtService.generateToken(user.getUsername());
            return new AuthResponseDto(token,
                    user.getUsername(),
                    user.getEmail(),
                    "Login successful"
            );
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.matches("\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}");
    }
}
