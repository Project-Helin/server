package commons.WebSockets;

import play.mvc.WebSocket;

import java.util.function.Consumer;


public class WebSocketConnection {

    private WebSocket.In<String> in;
    private WebSocket.Out<String> out;
    private Consumer<WebSocketConnection> onClosedConnection;

    public WebSocketConnection(WebSocket.In<String> in, WebSocket.Out<String> out) {
        this.in = in;
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