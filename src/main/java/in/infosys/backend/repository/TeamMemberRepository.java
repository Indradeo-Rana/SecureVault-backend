package in.infosys.backend.repository;

import in.infosys.backend.entity.Team;
import in.infosys.backend.entity.TeamMember;
import in.infosys.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository
        extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByTeamAndUser(
            Team team,
            User user
    );

    List<TeamMember> findAllByTeam(Team team);

    List<TeamMember> findAllByUser(User user);
}
