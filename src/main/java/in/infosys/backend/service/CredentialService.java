package in.infosys.backend.service;

import in.infosys.backend.dto.CredentialCreateRequestDto;
import in.infosys.backend.dto.CredentialResponseDto;
import in.infosys.backend.dto.CredentialUpdateRequestDto;
import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.User;
import in.infosys.backend.repository.CredentialRepository;
import in.infosys.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final CredentialShareService credentialShareService;

    public CredentialService(CredentialRepository credentialRepository, UserRepository userRepository, EncryptionService encryptionService, CredentialShareService credentialShareService) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.credentialShareService = credentialShareService;
    }

    // create a new credential
    public CredentialResponseDto createCredential(
            CredentialCreateRequestDto credentialReq) {

        // get currently logged-in username from JWT/SecurityContext
        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

//        System.out.println("Creating credential for logged-in user: " + username);

        // find the logged-in user from the database
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

       // System.out.println("Creating credential for user ID: " + user.getId());

        // create a new credential object and set its properties
        Credential credential = new Credential();

        credential.setTitle(credentialReq.getTitle());
        credential.setUsername(credentialReq.getUsername());
//        credential.setPassword(credentialReq.getPassword());
        // encrypt the password before saving it to the database
        credential.setPassword(
                encryptionService.encrypt(credentialReq.getPassword())
        );
        credential.setWebsite(credentialReq.getWebsite());
        credential.setNotes(credentialReq.getNotes());

        // set owner
        credential.setUser(user);

        // new credential is active * soft delete*
        credential.setDeleted(false);
        Credential savedCredential = credentialRepository.save(credential);

        return CredentialResponseDto.fromEntity(savedCredential);
    }

    // get a credential by ID
    public ResponseEntity<CredentialResponseDto> getCredentialById(Long id) {

        // Get logged-in user from SecurityContext
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Find credential by ID
        Credential credential = credentialRepository
//                .findByIdAndUserAndDeletedFalse(id, user) // update it
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new RuntimeException("Credential not found")
                );

        // Check whether user is owner OR has active share permission
        credentialShareService.canView(credential, currentUser);

        // Convert entity to DTO
        CredentialResponseDto dto =
                CredentialResponseDto.fromEntity(credential);

        // Decrypt only after access is authorized
        String decryptedPassword =
                encryptionService.decrypt(credential.getPassword());

        dto.setPassword(decryptedPassword);

        return ResponseEntity.ok(dto);
    }

// get all credentials
    public ResponseEntity<List<CredentialResponseDto>> getAllCredentials() {

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

//        System.out.println("Logged-in user: " + username);

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<Credential> credentials =
                credentialRepository
                        .findAllByUserAndDeletedFalse(user);

        List<CredentialResponseDto> response =
                credentials.stream()
                        .map(credential -> {

                            CredentialResponseDto dto =
                                    CredentialResponseDto.fromEntity(credential);

                            dto.setPassword(
                                    encryptionService.decrypt(
                                            credential.getPassword()
                                    )
                            );

                            return dto;

                        })
                        .toList();

        return ResponseEntity.ok(response);

    }

    // update a credential
    public ResponseEntity<CredentialResponseDto> updateCredential(
            Long id,
            CredentialUpdateRequestDto requestDto
    ) {

        String username = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getName();

       User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // finding credential and checking if it belongs to the logged-in user
        Credential existingCredential = credentialRepository
//                .findByIdAndUserAndDeletedFalse(id, user)/*findByIdAndUser(id, user)*/
                .findByIdAndDeletedFalse(id)  // updated so shared user can access credentials
                .orElseThrow(() ->
                        new RuntimeException("Credential not found or access denied")
                );

        // check OWNER or EDIT or MANAGE permission
        credentialShareService.canEdit(
                existingCredential, currentUser);

        // update the existing credential with new values
        existingCredential.setTitle(requestDto.getTitle());
        existingCredential.setUsername(requestDto.getUsername());
//        existingCredential.setPassword(requestDto.getPassword());
        existingCredential.setPassword(
                encryptionService.encrypt(requestDto.getPassword())
        );
        existingCredential.setWebsite(requestDto.getWebsite());
        existingCredential.setNotes(requestDto.getNotes());

        Credential updatedCredential = credentialRepository.save(existingCredential);

        // convert to dto
        CredentialResponseDto responseDto = CredentialResponseDto.fromEntity(updatedCredential);
        return ResponseEntity.ok(responseDto);
    }

// delete a credential
    public ResponseEntity<String> deleteCredential(Long id) {

       String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

       User user = userRepository.findByUsername(username)
               .orElseThrow(() ->
                       new RuntimeException("User not found ")
               );

      Credential credential = credentialRepository. /*findByIdAndUser(id, user)*/
                findByIdAndUserAndDeletedFalse(id, user)
               .orElseThrow(() ->
                       new RuntimeException("Credential not found or access denied")
               );

      credentialRepository.delete(credential);
      return ResponseEntity.ok("Credential deleted successfully");
    }

    // soft delete a credential
    public ResponseEntity<String> softDeleteCredential(Long id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found ")
                );

        // finding active credentials belonging to the logged-in user
        Credential credential = credentialRepository. /*findByIdAndUser(id, user)*/
                findByIdAndUserAndDeletedFalse(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Credential not found or access denied")
                );

        // mark the credential as deleted --> soft-delete
        credential.setDeleted(true);
        credentialRepository.save(credential);

        return ResponseEntity.ok("Credential moved to trash successfully");
    }


    // get all soft deleted credentials
    public ResponseEntity<List<CredentialResponseDto>> getAllSoftDeletedCredentials() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found ")
                );

        List<Credential> softDeletedCredentials = credentialRepository
                .findAllByUserAndDeletedTrue(user);

        List<CredentialResponseDto> responseDtos = softDeletedCredentials
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    private CredentialResponseDto convertToResponseDto(Credential credential) {
        return new CredentialResponseDto(
                credential.getId(),
                credential.getTitle(),
                credential.getUsername(),
                credential.getPassword(),
                credential.getWebsite(),
                credential.getNotes(),
                credential.isDeleted()
        );
    }
}
