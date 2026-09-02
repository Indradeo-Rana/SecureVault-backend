package in.infosys.backend.controller;

import in.infosys.backend.dto.CredentialCreateRequestDto;
import in.infosys.backend.dto.CredentialResponseDto;
import in.infosys.backend.dto.CredentialUpdateRequestDto;
import in.infosys.backend.entity.Credential;
import in.infosys.backend.service.CredentialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credential")
public class CredentialController {

    private final CredentialService credentialService;
    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    // POST --> Create a new credential
    @PostMapping
    public ResponseEntity<Credential> addCredential(
            @RequestBody CredentialCreateRequestDto credential
    ){
       Credential createdCredential = credentialService
               .createCredential(credential);
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(createdCredential);
    }

    // GET --> Get credential by ID
    @GetMapping("/{id}")
    public ResponseEntity<CredentialResponseDto> getCredentialById(
            @PathVariable Long id) {
        return credentialService.getCredentialById(id);
    }

    // GET --> Get all credentials
    @GetMapping
    public ResponseEntity<List<CredentialResponseDto>> getAllCredentials() {
        return credentialService.getAllCredentials();
    }

    // PUT --> Update a credential by ID
    @PutMapping("/{id}")
    public  ResponseEntity<Credential> updateCredential(
            @PathVariable Long id,
            @RequestBody CredentialUpdateRequestDto credential
    ){
        return credentialService.updateCredential(id, credential);
    }

    // DELETE --> Delete a credential by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCredential(@PathVariable Long id) {
        return credentialService.deleteCredential(id);
    }

    // Soft DELETE --> Soft delete a credential by ID
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<String> softDeleteCredential(@PathVariable Long id) {
        return credentialService.softDeleteCredential(id);
    }

    // get all soft deleted credentials
    @GetMapping("/soft-deleted")
    public ResponseEntity<List<CredentialResponseDto>>
    getAllSoftDeletedCredentials() {
        return credentialService.getAllSoftDeletedCredentials();
    }
}
