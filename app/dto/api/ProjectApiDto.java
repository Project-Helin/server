package dto.api;

import java.util.List;
import java.util.UUID;


/**
 * We cannot pass the Project because that's is a hibernate
 * Entity ( which might contain lazy init fields )
 */
public class ProjectApiDto {
    private UUID id;
    private String name;

    private List<ZoneApiDto> zones;

    public ProjectApiDto(UUID id, String name, List<ZoneApiDto> zones) {
        this.id = id;
        this.name = name;
        this.zones = zones;
    }

    public ProjectApiDto() {
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

    public List<ZoneApiDto> getZones() {
        return zones;
    }

    public void setZones(List<ZoneApiDto> zones) {
        this.zones = zones;
    }
}
