package in.infosys.backend.dto;

import in.infosys.backend.entity.TeamRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamMemberRequestDto {

    private String username;

    private TeamRole role;

}
