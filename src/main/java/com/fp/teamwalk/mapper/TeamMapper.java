package com.fp.teamwalk.mapper;

import com.fp.teamwalk.domain.Team;
import com.fp.teamwalk.model.TeamDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    // MapStruct will automatically use EmployeeMapper and StepCounterMapper
    // if they are defined as beans to handle nested objects.
    TeamDTO toTeamDto(Team entity);

    Team toTeamEntity(TeamDTO dto);
}
