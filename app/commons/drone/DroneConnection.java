package commons.drone;

import ch.helin.messages.commons.ConnectionUtils;
import com.rabbitmq.client.*;
import models.Drone;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public class DroneConnection {

    private static final Logger logger = getLogger(DroneConnection.class);

    private DroneMessageDispatcher droneMessageDispatcher;
    private Channel channel;

    private Drone drone;

    private Connection connection;
    private String consumerQueueName;
    private String producerQueueName;

    public DroneConnection(Drone drone, DroneMessageDispatcher droneMessageDispatcher) {
        //Create two queues, based on token of the drone

        this.drone = drone;
        this.droneMessageDispatcher = droneMessageDispatcher;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("helin");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            consumerQueueName = ConnectionUtils.getServerSideConsumerQueueName(drone.getToken().toString());
            producerQueueName = ConnectionUtils.getServerSideProducerQueueName(drone.getToken().toString());

            channel.queueDeclare(consumerQueueName, false, false, false, null);
            channel.queueDeclare(producerQueueName, false, false, false, null);

            startConsumer();
        } catch (Exception e) {
            closeConnection(connection);
            throw new RuntimeException(e);
        }

    }


    public void sendMessage(String message) {
        try {
            System.out.println("Send message " + message);
            channel.basicPublish("", producerQueueName, null, message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null && channel.isOpen()) {
            try {
                connection.close();
            } catch (IOException e) {
                logger.info("Failed to close connection {}", connection.toString(), e);
            }
        }
    }

    public void startConsumer() {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
               droneMessageDispatcher.dispatchMessageToController(drone.getId(), message);
            }
        };

        try {
            channel.basicConsume(consumerQueueName, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
