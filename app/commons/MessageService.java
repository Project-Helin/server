package commons;

import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.services.MessageHandler;

import java.util.ArrayList;
import java.util.List;

public class MessageService {

    private MessageConnection
    private List<MessageHandler<GpsState>> gpsStateHandlers = new ArrayList<>();

    public MessageService() {

    }

    public void addGpsStateHandler(MessageHandler<GpsState> handler) {
        gpsStateHandlers.add(handler);
    }
}
