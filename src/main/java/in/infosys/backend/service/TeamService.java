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
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamCredentialRepository teamCredentialRepository;
    private final CredentialRepository credentialRepository;

    public TeamService(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository, TeamCredentialRepository teamCredentialRepository, CredentialRepository credentialRepository) {

        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.teamCredentialRepository = teamCredentialRepository;
        this.credentialRepository = credentialRepository;
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

    // check if the user is a member of the team
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

    // ========================================================
    // DELETE TEAM
    // ========================================================
    public void deleteTeam(Long teamId) {
        User currentUser = currentUser();

        // Only owner can find/delete the team
        Team team = teamRepository.findByIdAndOwner(
                teamId,
                currentUser
        ).orElseThrow(() -> new RuntimeException(
                "Team not found or you are not the owner"
                )
        );
        // Delete all TeamCredential mappings
        List<TeamCredential> teamCredentials = teamCredentialRepository
                        .findAllByTeam(team);

        for (TeamCredential teamCredential : teamCredentials) {

            Credential credential = teamCredential.getCredential();

            // Delete mapping first
            teamCredentialRepository.delete(teamCredential);

            // Soft delete the credential
            credential.setDeleted(true);

            credentialRepository.save(credential);

            // Delete all team members
            List<TeamMember> members = teamMemberRepository
                    .findAllByTeam(team);

            teamMemberRepository.deleteAll(members);

            // Finally delete team
            teamRepository.delete(team);
        }

    }
}