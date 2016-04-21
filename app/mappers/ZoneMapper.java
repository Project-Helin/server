package mappers;

import controllers.api.ZoneDto;
import models.Zone;

public class ZoneMapper {
    public ZoneDto getZoneDto(Zone zone) {
        return new ZoneDto(zone.getId(), zone.getPolygon(), zone.getHeight(), zone.getType(), zone.getName());
    }

}
