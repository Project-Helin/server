package mappers;

import ch.helin.messages.dto.message.DroneDto;
import models.Drone;

public class DroneMapper {

    public DroneDto getDroneDto (Drone drone) {
        DroneDto dto = new DroneDto();
        dto.setId(drone.getId());
        dto.setName(drone.getName());
        dto.setPayload(drone.getPayload());
        if(drone.getProject() != null) {
            dto.setProjectID(drone.getProject().getId());
        }
        dto.setOrganisationToken(drone.getOrganisation().getToken());
        dto.setToken(drone.getToken());
        dto.setActive(drone.getIsActive());

        return dto;
    }
}
