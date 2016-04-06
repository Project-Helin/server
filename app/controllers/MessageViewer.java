package controllers;


import ch.helin.messages.experimental.MySimpleMessage;
import commons.MessageConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.messageviewer;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class MessageViewer extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(MessageViewer.class);


    /*
    public WebSocket<String> register() {
        return new WebSocket<String>() {
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {

                MessageConnection messageConnection = new MessageConnection();

                messageConnection.receiveMessage(out::write);

                // When the socket is closed.
                in.onClose(messageConnection::closeConnection);
//                System.out.println("Got connection");
                logger.info("=> Got connection");
            }
        };
    }
    */

    public Result sendSampleMessage() {
        logger.info("=> Send Sample Messages");

        MySimpleMessage mySimpleMessage = new MySimpleMessage();

        MessageConnection messageConnection = new MessageConnection();
        messageConnection.sendMessage("Hello Buddy, time is " + System.currentTimeMillis());
        messageConnection.closeConnection();
        return ok(messageviewer.render());
    }

    public Result index() {
        logger.info("=> Show index");
        return ok(messageviewer.render());
    }
}
