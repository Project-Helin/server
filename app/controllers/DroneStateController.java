package controllers;

import ch.helin.messages.dto.state.DroneState;

import java.util.UUID;

public class DroneStateController {

    public void onDroneStateReceived(UUID droneId, DroneState droneState) {
        System.out.println(droneState);
    }

}
