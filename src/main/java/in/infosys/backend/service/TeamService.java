package in.infosys.backend.service;

import in.infosys.backend.dto.*;
import in.infosys.backend.entity.*;
import in.infosys.backend.repository.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final CredentialRepository credentialRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamCredentialRepository teamCredentialRepository;
    private final EncryptionService encryptionService;

    public TeamService(
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
    // CREATE TEAM
    // =========================================================

    public TeamResponseDto createTeam(
            TeamCreateRequestDto request) {

        if (request == null ||
                request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException(
                    "Team name is required"
            );
        }

        User owner = currentUser();

        Team team = new Team();

        team.setName(request.getName());
        team.setOwner(owner);

        team = teamRepository.save(team);

        // Automatically add creator as OWNER
        TeamMember member = new TeamMember();

        member.setTeam(team);
        member.setUser(owner);
        member.setRole(TeamRole.OWNER);

        teamMemberRepository.save(member);

        return new TeamResponseDto(
                team.getId(),
                team.getName(),
                owner.getUsername()
        );
    }

    // =========================================================
    // GET TEAM BY ID
    // =========================================================

    public TeamResponseDto getTeam(Long teamId) {

        User user = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        checkTeamMember(team, user);

        return new TeamResponseDto(
                team.getId(),
                team.getName(),
                team.getOwner().getUsername()
        );
    }

    // =========================================================
    // GET MY TEAMS
    // =========================================================

    public List<TeamResponseDto> getMyTeams() {

        User user = currentUser();

        return teamMemberRepository
                .findAllByUser(user)
                .stream()
                .map(member ->
                        new TeamResponseDto(
                                member.getTeam().getId(),
                                member.getTeam().getName(),
                                member.getTeam()
                                        .getOwner()
                                        .getUsername()
                        )
                )
                .toList();
    }

    // =========================================================
    // ADD MEMBER
    // =========================================================

    public TeamMemberResponseDto addMember(
            Long teamId,
            TeamMemberRequestDto request) {

        User currentUser = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        TeamMember currentMember =
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

        if (currentMember.getRole() != TeamRole.OWNER &&
                currentMember.getRole() != TeamRole.ADMIN) {

            throw new RuntimeException(
                    "Only OWNER or ADMIN can add members"
            );
        }

        if (request == null ||
                request.getUsername() == null ||
                request.getUsername().isBlank()) {

            throw new RuntimeException(
                    "Username is required"
            );
        }

        User user =
                userRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        if (user.getId().equals(currentUser.getId())) {

            throw new RuntimeException(
                    "User is already a team member"
            );
        }

        if (teamMemberRepository
                .findByTeamAndUser(team, user)
                .isPresent()) {

            throw new RuntimeException(
                    "User is already a team member"
            );
        }

        TeamRole role = request.getRole();

        if (role == null ||
                role == TeamRole.OWNER) {

            role = TeamRole.MEMBER;
        }

        TeamMember member = new TeamMember();

        member.setTeam(team);
        member.setUser(user);
        member.setRole(role);

        member =
                teamMemberRepository.save(member);

        return new TeamMemberResponseDto(
                member.getId(),
                user.getUsername(),
                member.getRole()
        );
    }

    // =========================================================
    // GET MEMBERS
    // =========================================================

    public List<TeamMemberResponseDto> getMembers(
            Long teamId) {

        User user = currentUser();

        Team team =
                teamRepository
                        .findById(teamId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found"
                                )
                        );

        checkTeamMember(team, user);

        return teamMemberRepository
                .findAllByTeam(team)
                .stream()
                .map(member ->
                        new TeamMemberResponseDto(
                                member.getId(),
                                member.getUser().getUsername(),
                                member.getRole()
                        )
                )
                .toList();
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

        credential.setUser(currentUser);
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

        credential =
                credentialRepository.save(credential);

        // Connect Credential with Team
        TeamCredential teamCredential =
                new TeamCredential();

        teamCredential.setTeam(team);
        teamCredential.setCredential(credential);

        teamCredential =
                teamCredentialRepository.save(
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

    public List<TeamCredentialResponseDto>
    getCredentials(Long teamId) {

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

    public TeamCredentialResponseDto
    getCredential(
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


    // only owner can delete team
    public void deleteTeam(Long teamId) {

        User currentUser = currentUser();

        // Only owner can find/delete the team
        Team team =
                teamRepository
                        .findByIdAndOwner(
                                teamId,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Team not found or you are not the owner"
                                )
                        );

        // Delete all TeamCredential mappings
        List<TeamCredential> teamCredentials =
                teamCredentialRepository
                        .findAllByTeam(team);

        for (TeamCredential teamCredential : teamCredentials) {

            Credential credential =
                    teamCredential.getCredential();

            // Delete mapping first
            teamCredentialRepository.delete(
                    teamCredential
            );

            // Soft delete the credential
            credential.setDeleted(true);

            credentialRepository.save(
                    credential
            );
        }

        // Delete all team members
        List<TeamMember> members =
                teamMemberRepository
                        .findAllByTeam(team);

        teamMemberRepository.deleteAll(members);

        // Finally delete team
        teamRepository.delete(team);
    }
}