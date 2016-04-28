package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.converter.MessageConverter;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.stateMessage.DroneStateMessage;
import com.google.inject.Inject;
import controllers.DroneStateController;

import java.util.UUID;

public class DroneMessageDispatcher {

    @Inject
    public DroneStateController droneStateController;

    public void dispatchMessage (UUID droneId, String jsonMessage) {
        MessageConverter messageConverter = new JsonBasedMessageConverter();
        Message message = messageConverter.parseStringToMessage(jsonMessage);

        switch(message.getPayloadType()) {
            case DroneState:
                DroneStateMessage droneStateMessage = (DroneStateMessage) message;
                droneStateController.onDroneStateReceived(droneId, droneStateMessage.getDroneState());
        }
    }

}
