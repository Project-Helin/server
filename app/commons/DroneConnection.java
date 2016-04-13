package commons;

import com.rabbitmq.client.*;
import models.Drone;

import java.io.IOException;

public class DroneConnection {

    private Channel channel;
    private Connection connection;
    private String queueName;


    public DroneConnection(Drone drone) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("helin");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            queueName = drone.getToken().toString();

            channel.queueDeclare(queueName, false, false, false, null);
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
            channel.basicPublish("", QueueName.SERVER_TO_DRONE.name(), null, message.getBytes());
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

    public void receiveMessage(java.util.function.Consumer<String> onMessage) {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                onMessage.accept(message);
            }
        };

        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
