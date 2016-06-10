package service.websocket;

import play.mvc.WebSocket;

import java.util.function.Consumer;


public class WebSocketConnection {

    private final WebSocket.Out<String> out;
    private Consumer<WebSocketConnection> onClosedConnection;

    public WebSocketConnection(WebSocket.In<String> in, WebSocket.Out<String> out) {
        this.out = out;

        in.onClose(() -> onClosedConnection.accept(this));
    }

    public void sendMessage(String message) {
        out.write(message);
    }

    public void setCloseCallback(Consumer<WebSocketConnection> onClosedConnection) {
        this.onClosedConnection = onClosedConnection;
    }
}
