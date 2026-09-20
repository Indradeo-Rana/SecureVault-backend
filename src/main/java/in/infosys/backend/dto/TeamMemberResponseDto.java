package in.infosys.backend.dto;

import in.infosys.backend.entity.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamMemberResponseDto {

    private Long id;
    private String username;
    private TeamRole role;
}
