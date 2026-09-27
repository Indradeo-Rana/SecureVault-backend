package in.infosys.backend.service;

import in.infosys.backend.dto.TeamMemberRequestDto;
import in.infosys.backend.dto.TeamMemberResponseDto;
import in.infosys.backend.entity.*;
import in.infosys.backend.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class TeamMemberService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public TeamMemberService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
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
                        new RuntimeException("User not found"));
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
