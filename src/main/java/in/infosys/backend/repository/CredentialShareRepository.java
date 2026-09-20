package in.infosys.backend.repository;

import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.CredentialShare;
import in.infosys.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialShareRepository
        extends JpaRepository<CredentialShare, Long> {

    List<CredentialShare> findAllByOwnerAndRevokedFalse(User owner);

    List<CredentialShare> findAllBySharedWithAndRevokedFalse(User user);

    Optional<CredentialShare> findByIdAndOwnerAndRevokedFalse(
            Long id,
            User owner
    );

    Optional<CredentialShare> findByCredentialAndSharedWithAndRevokedFalse(
            Credential credential, User sharedWith
    );
}
