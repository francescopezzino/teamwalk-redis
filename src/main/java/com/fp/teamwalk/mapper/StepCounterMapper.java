package com.fp.teamwalk.mapper;

import com.fp.teamwalk.domain.StepCounter;
import com.fp.teamwalk.model.StepCounterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;

/**\
 * In a hybrid caching architecture (Caffeine for L1 and Redis for L2) as of 2026,
 * the StepCounterMapper becomes the gatekeeper for what enters your cache.
 * Best practices strongly recommend caching DTOs (like StepCounterDTO) rather than JPA entities
 * to avoid LazyInitializationException and unnecessary persistence context state
 */
@Mapper(componentModel = "spring")
public interface StepCounterMapper {

    @Mapping(target = "teamId", source = "team.id")
    StepCounterDTO toStepCounterDto(StepCounter entity);

    @Mapping(target = "id", ignore = true)
    StepCounter toStepCounterEntity(StepCounterDTO dto);

    List<StepCounterDTO> toStepCounterDtos(Collection<StepCounter> stepCounter);
}
