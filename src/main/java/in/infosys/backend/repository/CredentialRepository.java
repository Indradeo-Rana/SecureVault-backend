package in.infosys.backend.repository;

import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {

//    List<Credential> findAllByUser(User user);
//
//    Optional<Credential> findByIdAndUser(Long id, User user);

//    above thinks move to  soft-delete

    // Get only active credentials of a user
    List<Credential> findAllByUserAndDeletedFalse(User user);

    // Find an active credential belonging to a user
    Optional<Credential> findByIdAndUserAndDeletedFalse(
            Long id,
            User user
    );

    // Later: get deleted credentials for Trash
    List<Credential> findAllByUserAndDeletedTrue(User user);


    // Later: find deleted credential for restore
    Optional<Credential> findByIdAndUserAndDeletedTrue(
            Long id,
            User user
    );

    Optional<Credential> findByIdAndDeletedFalse(Long id);
}
