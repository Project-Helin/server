package commons.drone;

import ch.helin.messages.commons.ConnectionUtils;
import com.rabbitmq.client.*;
import models.Drone;

import java.io.IOException;

public class DroneConnection {

    DroneMessageDispatcher droneMessageDispatcher;
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

    public void closeConnection() {
        closeConnection(connection);
        closeChannel(channel);
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
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void closeChannel(Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startConsumer() {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
               droneMessageDispatcher.dispatchMessage(drone.getId(), message);
            }
        };

        try {
            channel.basicConsume(consumerQueueName, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
