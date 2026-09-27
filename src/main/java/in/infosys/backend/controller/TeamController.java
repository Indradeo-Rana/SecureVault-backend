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

}
