package in.infosys.backend.service;

import in.infosys.backend.dto.TeamCredentialRequestDto;
import in.infosys.backend.dto.TeamCredentialResponseDto;
import in.infosys.backend.entity.*;
import in.infosys.backend.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamCredentialService {

    private final TeamRepository teamRepository;
    private final CredentialRepository credentialRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamCredentialRepository teamCredentialRepository;
    private final EncryptionService encryptionService;


    public TeamCredentialService(
            TeamRepository teamRepository,
            CredentialRepository credentialRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository,
            TeamCredentialRepository teamCredentialRepository,
            EncryptionService encryptionService) {

        this.teamRepository = teamRepository;
        this.credentialRepository = credentialRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.teamCredentialRepository = teamCredentialRepository;
        this.encryptionService = encryptionService;
    }

    // =========================================================
    // CURRENT USER
    // =========================================================
    private User currentUser() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }

    // =========================================================
    // ADD TEAM CREDENTIAL
    // =========================================================
    public TeamCredentialResponseDto addCredential(
            Long teamId,
            TeamCredentialRequestDto request) {

        User currentUser = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        TeamMember member =
                teamMemberRepository
                        .findByTeamAndUser(
                                team,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this team"
                                )
                        );

        if (member.getRole() != TeamRole.OWNER &&
                member.getRole() != TeamRole.ADMIN) {

            throw new RuntimeException(
                    "Only OWNER or ADMIN can add credentials"
            );
        }

        if (request == null) {
            throw new RuntimeException(
                    "Credential data is required"
            );
        }

        if (request.getTitle() == null ||
                request.getTitle().isBlank()) {

            throw new RuntimeException(
                    "Title is required"
            );
        }

        if (request.getUsername() == null ||
                request.getUsername().isBlank()) {

            throw new RuntimeException(
                    "Username is required"
            );
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        // Create normal Credential
        Credential credential = new Credential();

        credential.setTeam(team);
        credential.setTitle(request.getTitle());
        credential.setUsername(request.getUsername());

        credential.setPassword(
                encryptionService.encrypt(
                        request.getPassword()
                )
        );

        credential.setWebsite(request.getWebsite());
        credential.setNotes(request.getNotes());
        credential.setDeleted(false);

        credential = credentialRepository.save(credential);

        // Connect Credential with Team
        TeamCredential teamCredential = new TeamCredential();

        teamCredential.setTeam(team);
        teamCredential.setCredential(credential);

        teamCredential = teamCredentialRepository.save(
                        teamCredential
                );

        return new TeamCredentialResponseDto(
                teamCredential.getId(),
                credential.getId(),
                credential.getTitle(),
                credential.getUsername(),
                null,
                credential.getWebsite(),
                credential.getNotes()
        );
    }

    // =========================================================
    // GET TEAM CREDENTIALS
    // =========================================================
    public List<TeamCredentialResponseDto> getCredentials(Long teamId) {

        User currentUser = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        checkTeamMember(team, currentUser);

        return teamCredentialRepository
                .findAllByTeam(team)
                .stream()
                .map(teamCredential -> {

                    Credential credential =
                            teamCredential.getCredential();

                    return new TeamCredentialResponseDto(
                            teamCredential.getId(),
                            credential.getId(),
                            credential.getTitle(),
                            credential.getUsername(),
                            null,
                            credential.getWebsite(),
                            credential.getNotes()
                    );
                })
                .toList();
    }

    // =========================================================
    // GET SINGLE TEAM CREDENTIAL
    // =========================================================
    public TeamCredentialResponseDto getCredential(
            Long teamId,
            Long teamCredentialId) {

        User currentUser = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        checkTeamMember(team, currentUser);

        TeamCredential teamCredential =
                teamCredentialRepository
                        .findByIdAndTeam(
                                teamCredentialId,
                                team
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team credential not found"
                                )
                        );

        Credential credential =
                teamCredential.getCredential();

        String decryptedPassword =
                encryptionService.decrypt(
                        credential.getPassword()
                );

        return new TeamCredentialResponseDto(
                teamCredential.getId(),
                credential.getId(),
                credential.getTitle(),
                credential.getUsername(),
                decryptedPassword,
                credential.getWebsite(),
                credential.getNotes()
        );
    }

    // =========================================================
    // UPDATE TEAM CREDENTIAL
    // =========================================================
    public TeamCredentialResponseDto updateCredential(
            Long teamId,
            Long teamCredentialId,
            TeamCredentialRequestDto request) {

        User currentUser = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        TeamMember member =
                teamMemberRepository
                        .findByTeamAndUser(
                                team,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this team"
                                )
                        );

        if (member.getRole() != TeamRole.OWNER &&
                member.getRole() != TeamRole.ADMIN) {

            throw new RuntimeException(
                    "Only OWNER or ADMIN can edit credentials"
            );
        }

        TeamCredential teamCredential =
                teamCredentialRepository
                        .findByIdAndTeam(
                                teamCredentialId,
                                team
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team credential not found"
                                )
                        );

        Credential credential =
                teamCredential.getCredential();

        if (request.getTitle() != null) {
            credential.setTitle(request.getTitle());
        }

        if (request.getUsername() != null) {
            credential.setUsername(
                    request.getUsername()
            );
        }

        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            credential.setPassword(
                    encryptionService.encrypt(
                            request.getPassword()
                    )
            );
        }

        credential.setWebsite(
                request.getWebsite()
        );

        credential.setNotes(
                request.getNotes()
        );

        credential =
                credentialRepository.save(
                        credential
                );

        String password =
                encryptionService.decrypt(
                        credential.getPassword()
                );

        return new TeamCredentialResponseDto(
                teamCredential.getId(),
                credential.getId(),
                credential.getTitle(),
                credential.getUsername(),
                password,
                credential.getWebsite(),
                credential.getNotes()
        );
    }

    // =========================================================
    // DELETE TEAM CREDENTIAL
    // =========================================================
    public void deleteCredential(
            Long teamId,
            Long teamCredentialId) {

        User currentUser = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        TeamMember member =
                teamMemberRepository
                        .findByTeamAndUser(
                                team,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "You are not a member of this team"
                                )
                        );

        if (member.getRole() != TeamRole.OWNER &&
                member.getRole() != TeamRole.ADMIN) {

            throw new RuntimeException(
                    "Only OWNER or ADMIN can delete credentials"
            );
        }

        TeamCredential teamCredential =
                teamCredentialRepository
                        .findByIdAndTeam(
                                teamCredentialId,
                                team
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team credential not found"
                                )
                        );

        teamCredentialRepository.delete(
                teamCredential
        );

        // Soft delete original credential
        Credential credential =
                teamCredential.getCredential();

        credential.setDeleted(true);

        credentialRepository.save(credential);
    }

    // =========================================================
    // CHECK TEAM MEMBERSHIP(get team member)
    // =========================================================
    private void checkTeamMember(
            Team team,
            User user) {

        boolean isMember =
                teamMemberRepository
                        .findByTeamAndUser(
                                team,
                                user
                        )
                        .isPresent();

        if (!isMember) {

            throw new RuntimeException(
                    "You do not have access to this team"
            );
        }
    }



}
