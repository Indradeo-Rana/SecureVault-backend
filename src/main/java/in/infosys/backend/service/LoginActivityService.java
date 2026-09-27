package in.infosys.backend.service;

import in.infosys.backend.dto.LoginActivityResponseDto;
import in.infosys.backend.entity.LoginActivity;
import in.infosys.backend.repository.LoginActivityRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginActivityService {

    private final LoginActivityRepository loginActivityRepository;

    public LoginActivityService(
            LoginActivityRepository loginActivityRepository) {
        this.loginActivityRepository = loginActivityRepository;
    }

    public void recordLogin(
            String username,
            boolean success,
            String ipAddress) {

        LoginActivity activity = new LoginActivity();

        activity.setUsername(username);
        activity.setSuccess(success);
        activity.setIpAddress(ipAddress);
        activity.setTimestamp(LocalDateTime.now());

        loginActivityRepository.save(activity);
    }

    public List<LoginActivityResponseDto> getLoginHistory() {
        String username = SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return loginActivityRepository
                .findAllByUsernameOrderByTimestampDesc(username)
                .stream()
                .map(activity ->
                        new LoginActivityResponseDto(
                                activity.getId(),
                                activity.getUsername(),
                                activity.getIpAddress(),
                                activity.isSuccess(),
                                activity.getTimestamp()
                        )
                )
                .toList();
    }
}
