package controllers;

import ch.helin.messages.dto.message.DroneInfoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DroneInfosController {
    private static final Logger logger = LoggerFactory.getLogger(DroneInfosController.class);

    public void onDroneInfoReceived(UUID droneId, DroneInfoMessage droneInfoMessage) {


    }
}
