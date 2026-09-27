package in.infosys.backend.controller;

import in.infosys.backend.dto.TeamCredentialRequestDto;
import in.infosys.backend.dto.TeamCredentialResponseDto;
import in.infosys.backend.service.TeamCredentialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams") // "/api/team-credentials
public class TeamCredentialController {

    private final TeamCredentialService teamCredentialService;

    public TeamCredentialController(TeamCredentialService teamCredentialService) {
        this.teamCredentialService = teamCredentialService;
    }

    // =========================================================
    // ADD TEAM CREDENTIAL( team credential functionality)
    // =========================================================
    @PostMapping("/{teamId}/credentials")
    public ResponseEntity<TeamCredentialResponseDto> addCredential(
            @PathVariable Long teamId,
            @RequestBody TeamCredentialRequestDto request) {

        return ResponseEntity.ok(
                teamCredentialService.addCredential(
                        teamId,
                        request
                )
        );
    }

    // =========================================================
    // GET TEAM CREDENTIALS
    // =========================================================
    @GetMapping("/{teamId}/credentials")
    public ResponseEntity<List<TeamCredentialResponseDto>> getCredentials(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamCredentialService.getCredentials(teamId)
        );
    }

    // =========================================================
    // GET SINGLE TEAM CREDENTIAL
    // =========================================================
    @GetMapping("/{teamId}/credentials/{teamCredentialId}")
    public ResponseEntity<TeamCredentialResponseDto>
    getCredential(
            @PathVariable Long teamId,
            @PathVariable Long teamCredentialId) {

        return ResponseEntity.ok(
                teamCredentialService.getCredential(
                        teamId,
                        teamCredentialId
                )
        );
    }

    // =========================================================
    // UPDATE TEAM CREDENTIAL
    // =========================================================

    @PutMapping("/{teamId}/credentials/{teamCredentialId}")
    public ResponseEntity<TeamCredentialResponseDto> updateCredential(
            @PathVariable Long teamId,
            @PathVariable Long teamCredentialId,
            @RequestBody TeamCredentialRequestDto request) {

        return ResponseEntity.ok(
                teamCredentialService.updateCredential(
                        teamId,
                        teamCredentialId,
                        request
                )
        );
    }

    // =========================================================
    // DELETE TEAM CREDENTIAL
    // =========================================================

    @DeleteMapping("/{teamId}/credentials/{teamCredentialId}")
    public ResponseEntity<String> deleteCredential(
            @PathVariable Long teamId,
            @PathVariable Long teamCredentialId) {

        teamCredentialService.deleteCredential(
                teamId,
                teamCredentialId
        );

        return ResponseEntity.ok(
                "Team credential deleted successfully"
        );
    }
}
