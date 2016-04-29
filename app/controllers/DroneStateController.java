package controllers;

import ch.helin.messages.dto.state.DroneState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DroneStateController {
    private static final Logger logger = LoggerFactory.getLogger(DroneStateController.class);

    public void onDroneStateReceived(UUID droneId, DroneState droneState) {
        logger.debug(droneId + " : " + droneState.getAltitude());
    }

}
