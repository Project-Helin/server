package commons.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.converter.MessageConverter;
import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.message.Message;
import com.google.inject.Inject;
import controllers.DroneInfosController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DroneMessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DroneMessageDispatcher.class);

    @Inject
    public DroneInfosController droneInfoController;

    public void dispatchMessageToController(UUID droneId, String jsonMessage) {
        MessageConverter messageConverter = new JsonBasedMessageConverter();
        Message message = messageConverter.parseStringToMessage(jsonMessage);

        switch(message.getPayloadType()) {
            case DroneInfo:
                DroneInfoMessage droneInfoMessage = (DroneInfoMessage) message;
                droneInfoController.onDroneInfoReceived(droneId, droneInfoMessage);
        }
    }

}
