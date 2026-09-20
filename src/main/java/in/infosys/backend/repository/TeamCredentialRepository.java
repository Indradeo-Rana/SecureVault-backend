package in.infosys.backend.repository;

import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.Team;
import in.infosys.backend.entity.TeamCredential;
import in.infosys.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamCredentialRepository
        extends JpaRepository<TeamCredential, Long> {

    List<TeamCredential> findAllByTeam(Team team);

    Optional<TeamCredential> findByTeamAndCredential(
            Team team,
            Credential credential
    );

    Optional<TeamCredential> findByIdAndTeam(
            Long id,
            Team team
    );
}
