package in.infosys.backend.service;

import in.infosys.backend.dto.*;
import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.LoginActivity;
import in.infosys.backend.entity.User;
import in.infosys.backend.repository.CredentialRepository;
import in.infosys.backend.repository.LoginActivityRepository;
import in.infosys.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final CredentialRepository credentialRepository;
    private final LoginActivityRepository loginActivityRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final PasswordStrengthService passwordStrengthService;

    public AnalyticsService(CredentialRepository credentialRepository, LoginActivityRepository loginActivityRepository, UserRepository userRepository, EncryptionService encryptionService, PasswordStrengthService passwordStrengthService) {
        this.credentialRepository = credentialRepository;
        this.loginActivityRepository = loginActivityRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.passwordStrengthService = passwordStrengthService;
    }

//    1. get the current user
    public User getCurrentUser(){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found")
                );
    }

//    2. method to get password health
    public PasswordHealthResponseDto getPasswordHealth(){
        // get the current user
        User user = getCurrentUser();

        // loop over the user's credentials and check the password strength
        List<Credential> credentials = credentialRepository
                .findAllByUserAndDeletedFalse(user);

        int strong = 0;
        int weak = 0;
        int medium = 0;

        for (Credential credential : credentials) {

            String password =
                    encryptionService.decrypt(
                            credential.getPassword()
                    );

            PasswordStrengthRequestDto request =
                    new PasswordStrengthRequestDto();

            request.setPassword(password);

            PasswordStrengthResponseDto result =
                    passwordStrengthService
                            .analyzePassword(request);

            if ("Strong".equals(result.getStrength())) {

                strong++;

            } else if ("Medium".equals(result.getStrength())) {

                medium++;

            } else {

                weak++;
            }
        }

        return new PasswordHealthResponseDto(
                credentials.size(),
                strong,
                medium,
                weak
        );
    }

    // 2. method to get password reuse
    public List<PasswordReuseResponseDto>
    getPasswordReuse() {

        User user = getCurrentUser();

        List<Credential> credentials =
                credentialRepository
                        .findAllByUserAndDeletedFalse(user);

        Map<String, List<String>> passwordMap =
                new HashMap<>();

        for (Credential credential : credentials) {

            String password =
                    encryptionService.decrypt(
                            credential.getPassword()
                    );

            passwordMap
                    .computeIfAbsent(
                            password,
                            key -> new ArrayList<>()
                    )
                    .add(credential.getTitle());
        }

        return passwordMap.values()
                .stream()
                .filter(titles -> titles.size() > 1)
                .map(titles ->
                        new PasswordReuseResponseDto(
                                titles.size(),
                                titles
                        )
                )
                .toList();
    }

    // 3. method to get login activity
    public LoginAnalyticsResponseDto getLoginAnalytics() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        List<LoginActivity> activities =
                loginActivityRepository
                        .findAllByUsernameOrderByTimestampDesc(
                                username
                        );

        long totalAttempts = activities.size();

        long successfulLogins =
                activities.stream()
                        .filter(LoginActivity::isSuccess)
                        .count();

        long failedLogins =
                activities.stream()
                        .filter(activity ->
                                !activity.isSuccess())
                        .count();

        return new LoginAnalyticsResponseDto(
                totalAttempts,
                successfulLogins,
                failedLogins
        );
    }

}