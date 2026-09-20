package in.infosys.backend.controller;

import in.infosys.backend.dto.CredentialShareRequestDto;
import in.infosys.backend.dto.CredentialShareResponseDto;
import in.infosys.backend.dto.CredentialShareUpdateRequestDto;
import in.infosys.backend.entity.CredentialShare;
import in.infosys.backend.service.CredentialShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shares")
public class CredentialShareController {

    private final CredentialShareService credentialShareService;

    public CredentialShareController(CredentialShareService credentialShareService) {
        this.credentialShareService = credentialShareService;
    }

    // 1. share the credential
    @PostMapping
    public ResponseEntity<CredentialShareResponseDto> shareCredential(
            @RequestBody CredentialShareRequestDto request
    ){
        return  ResponseEntity.ok(
                credentialShareService.shareCredential(request)
        );
    }

    // 2. Revoke share (delete the share)
    @DeleteMapping("/{shareId}")
    public ResponseEntity<String> revokeShare(
            @PathVariable Long shareId
    ){
        credentialShareService.revokeShare(shareId);
        return ResponseEntity.ok("Credential Sharing access removed successfully");
    }

    // 3. credential share with me
    @GetMapping("/received")
    public ResponseEntity<List<CredentialShareResponseDto>> getSharedCredentials(){
        return ResponseEntity.ok(credentialShareService.getSharedCredentials());
    }

    // 4. credentials I shared
    @GetMapping("/sent")
    public ResponseEntity<List<CredentialShareResponseDto>> getMyShares(){

        return ResponseEntity.ok(
                credentialShareService.getMyShares()
        );
    }

    // 5. Update share permission
    @PutMapping("/{shareId}/accept")
    public ResponseEntity<CredentialShareResponseDto> updateSharePermission(
            @PathVariable Long shareId,
            @RequestBody CredentialShareUpdateRequestDto request
    ){
        return ResponseEntity
                .ok(credentialShareService
                        .updateSharePermission(shareId, request));
    }

}
