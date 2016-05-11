package mappers;


import com.google.inject.Inject;
import dto.api.ProjectApiDto;
import dto.api.ZoneApiDto;
import models.Project;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    @Inject
    ZoneMapper zoneMapper;

    public ProjectApiDto getProjectDto(Project project) {

        List<ZoneApiDto> zoneDtos= project.getZones().stream().map(zoneMapper::getZoneDto).collect(Collectors.toList());

        return new ProjectApiDto(
                project.getId(),
                project.getName(),
                zoneDtos
        );
    }

}
