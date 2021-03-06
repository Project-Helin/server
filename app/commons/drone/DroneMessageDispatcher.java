package service.drone;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.converter.MessageConverter;
import ch.helin.messages.dto.message.DroneActiveStateMessage;
import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.message.Message;
import ch.helin.messages.dto.message.missionMessage.ConfirmMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinishedMissionMessage;
import com.google.inject.Inject;
import com.google.inject.Provider;
import controllers.messages.DroneActiveController;
import controllers.messages.DroneInfosController;
import controllers.messages.MissionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DroneMessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DroneMessageDispatcher.class);

    //only use providers for controller-injection in order to avoid
    //circular dependencies, which point back to DroneCommunicationManager

    @Inject
    private Provider<DroneInfosController> droneInfosControllerProvider;

    @Inject
    private Provider<DroneActiveController> droneActiveControllerProvider;

    @Inject
    private Provider<MissionController> missionControllerProvider;

    public void dispatchMessageToController(UUID droneId, String jsonMessage) {
        logger.debug("Received Message {}", jsonMessage);

        MessageConverter messageConverter = new JsonBasedMessageConverter();
        Message message = messageConverter.parseStringToMessage(jsonMessage);

        switch (message.getPayloadType()) {
            case DroneInfo:
                DroneInfoMessage droneInfoMessage = (DroneInfoMessage) message;
                droneInfosControllerProvider.get().onDroneInfoReceived(droneId, droneInfoMessage);
                logger.info("DroneInfoMessage {}", droneInfoMessage.getDroneInfo());
                break;
            case ConfirmMission:
                ConfirmMissionMessage missionMessage = (ConfirmMissionMessage) message;
                missionControllerProvider.get().onConfirmMissionMessageReceived(droneId, missionMessage);
                logger.info("ConfirmMissionMessage {}", missionMessage.getMissionConfirmType());
                break;
            case FinishedMission:
                FinishedMissionMessage finishedMissionMessage = (FinishedMissionMessage) message;
                missionControllerProvider.get().onFinishedMissionMessageReceived(droneId, finishedMissionMessage);
                logger.info("FinishedMissionMessage {}", finishedMissionMessage.getFinishedType());
                break;
            case DroneActiveState:
                DroneActiveStateMessage droneActiveStateMessage = (DroneActiveStateMessage) message;
                droneActiveControllerProvider.get().onDroneActiveStateReceived(droneId, droneActiveStateMessage);
                logger.info("DroneActiveStateMessage {}", droneActiveStateMessage.getDroneActiveState());
                break;

            default:
                logger.error("Message Type: {} unknown ", message.getPayloadType());
        }
    }

}
