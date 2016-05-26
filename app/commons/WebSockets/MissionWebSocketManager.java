package commons.WebSockets;


import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.DroneInfoMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class MissionWebSocketManager {

    private Map<UUID, List<WebSocketConnection>> missionWebSocketMap = new ConcurrentHashMap<>();

    @Inject
    JsonBasedMessageConverter jsonBasedMessageConverter;


    public void addWebSocketConnection(UUID missionId, WebSocketConnection webSocketConnection) {

        List<WebSocketConnection> connections = missionWebSocketMap.get(missionId);
        if (connections == null) {
            connections = new ArrayList<>();
            missionWebSocketMap.put(missionId, connections);
        }

        connections.add(webSocketConnection);
        webSocketConnection.setCloseCallback((connection) -> removeWebSocketConnection(missionId, connection));
    }

    public void sendDroneInfoToConnectedClients(UUID missionId, DroneInfoMessage droneInfoMessage) {
        List<WebSocketConnection> webSocketConnections = missionWebSocketMap.get(missionId);
        if (webSocketConnections != null) {
            String message = jsonBasedMessageConverter.parseMessageToString(droneInfoMessage);
            webSocketConnections.forEach((c) -> c.sendMessage(message));
        }

    }

    private void removeWebSocketConnection(UUID missionId, WebSocketConnection connection) {
        List<WebSocketConnection> webSocketConnections = missionWebSocketMap.get(missionId);
        webSocketConnections.remove(connection);
    }

}
