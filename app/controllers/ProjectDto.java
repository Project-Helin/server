package controllers;

import java.util.List;
import java.util.UUID;


/**
 * We cannot pass the Project because that's is a hibernate
 * Entity ( which might contain lazy init fields )
 */
public class ProjectDto {
    private UUID id;
    private String name;

    private List<ZoneDto> zones;

    public ProjectDto(UUID id, String name, List<ZoneDto> zones) {
        this.id = id;
        this.name = name;
        this.zones = zones;
    }

    public ProjectDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ZoneDto> getZones() {
        return zones;
    }

    public void setZones(List<ZoneDto> zones) {
        this.zones = zones;
    }
}
