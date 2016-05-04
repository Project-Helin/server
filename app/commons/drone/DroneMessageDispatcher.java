package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.converter.MessageConverter;
import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.stateMessage.DroneStateMessage;
import com.google.inject.Inject;
import controllers.DroneStateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DroneMessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DroneMessageDispatcher.class);

    @Inject
    public DroneStateController droneStateController;

    public void dispatchMessage (UUID droneId, String jsonMessage) {
        MessageConverter messageConverter = new JsonBasedMessageConverter();
        Message message = messageConverter.parseStringToMessage(jsonMessage);

        switch(message.getPayloadType()) {
            case GpsState:
                logger.debug(message.getPayloadType().toString());
                break;
            case DroneState:
                DroneStateMessage droneStateMessage = (DroneStateMessage) message;
                droneStateController.onDroneStateReceived(droneId, droneStateMessage.getDroneState());
            case DroneInfo:
                DroneInfoMessage droneInfoMessage = (DroneInfoMessage) message;
                if(droneInfoMessage.getBatteryState() != null) {
                    logger.debug(droneInfoMessage.getBatteryState().toString());
                }
                if (droneInfoMessage.getGpsState() != null) {
                    logger.debug(droneInfoMessage.getGpsState().toString());
                }
                logger.debug(droneInfoMessage.getDroneState().toString());
                logger.debug(droneInfoMessage.getClientTime().toString());
        }
    }

}
