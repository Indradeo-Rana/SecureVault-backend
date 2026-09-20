package in.infosys.backend.controller;


import in.infosys.backend.dto.*;
import in.infosys.backend.entity.Team;
import in.infosys.backend.entity.TeamMember;
import in.infosys.backend.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // =========================================================
    // CREATE TEAM
    // =========================================================

    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(
            @RequestBody TeamCreateRequestDto request) {

        return ResponseEntity.ok(
                teamService.createTeam(request)
        );
    }

    // =========================================================
    // GET MY TEAMS
    // =========================================================
    @GetMapping("/mine")
    public ResponseEntity<List<TeamResponseDto>> getMyTeams() {

        return ResponseEntity.ok(
                teamService.getMyTeams()
        );
    }

    // =========================================================
    // GET TEAM by team id
    // =========================================================
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponseDto> getTeam(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamService.getTeam(teamId)
        );
    }

    // =========================================================
    // delete team
    // =========================================================
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(
            @PathVariable Long teamId) {

        teamService.deleteTeam(teamId);

        return ResponseEntity.ok(
                "Team deleted successfully"
        );
    }

    // Starts team member related functionality
    // =========================================================
    // ADD MEMBER
    // =========================================================
    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamMemberResponseDto>
    addMember(
            @PathVariable Long teamId,
            @RequestBody TeamMemberRequestDto request) {

        return ResponseEntity.ok(
                teamService.addMember(
                        teamId,
                        request
                )
        );
    }

    // =========================================================
    // GET MEMBERS
    // =========================================================

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponseDto>>
    getMembers(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamService.getMembers(teamId)
        );
    }

    // =========================================================
    // ADD TEAM CREDENTIAL( team credential functionality)
    // =========================================================
    @PostMapping("/{teamId}/credentials")
    public ResponseEntity<TeamCredentialResponseDto>
    addCredential(
            @PathVariable Long teamId,
            @RequestBody TeamCredentialRequestDto request) {

        return ResponseEntity.ok(
                teamService.addCredential(
                        teamId,
                        request
                )
        );
    }

    // =========================================================
    // GET TEAM CREDENTIALS
    // =========================================================

    @GetMapping("/{teamId}/credentials")
    public ResponseEntity<List<TeamCredentialResponseDto>>
    getCredentials(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamService.getCredentials(teamId)
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
                teamService.getCredential(
                        teamId,
                        teamCredentialId
                )
        );
    }

    // =========================================================
    // UPDATE TEAM CREDENTIAL
    // =========================================================

    @PutMapping("/{teamId}/credentials/{teamCredentialId}")
    public ResponseEntity<TeamCredentialResponseDto>
    updateCredential(
            @PathVariable Long teamId,
            @PathVariable Long teamCredentialId,
            @RequestBody TeamCredentialRequestDto request) {

        return ResponseEntity.ok(
                teamService.updateCredential(
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
    public ResponseEntity<String>
    deleteCredential(
            @PathVariable Long teamId,
            @PathVariable Long teamCredentialId) {

        teamService.deleteCredential(
                teamId,
                teamCredentialId
        );

        return ResponseEntity.ok(
                "Team credential deleted successfully"
        );
    }


}
