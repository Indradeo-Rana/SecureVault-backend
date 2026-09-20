package in.infosys.backend.repository;

import in.infosys.backend.entity.Team;
import in.infosys.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByOwner(User owner);

    Optional<Team> findByIdAndOwner(
            Long id,
            User owner
    );
}