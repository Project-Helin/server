package mappers;

import dto.api.ZoneApiDto;
import models.Zone;

public class ZoneMapper {
    public ZoneApiDto getZoneDto(Zone zone) {
        return new ZoneApiDto(zone.getId(), zone.getPolygon(), zone.getHeight(), zone.getType(), zone.getName());
    }

}
