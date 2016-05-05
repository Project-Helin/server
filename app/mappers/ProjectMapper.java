package mappers;


import com.google.inject.Inject;
import controllers.api.ProjectDto;
import controllers.api.ZoneDto;
import models.Project;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    @Inject
    private ZoneMapper zoneMapper;

    public ProjectDto getProjectDto(Project project) {
        List<ZoneDto> zoneDtos= project
            .getZones()
            .stream()
            .map(zoneMapper::getZoneDto)
            .collect(Collectors.toList());

        return new ProjectDto(
                project.getId(),
                project.getName(),
                zoneDtos
        );
    }

}
