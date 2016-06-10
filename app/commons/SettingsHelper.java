package commons;

import com.typesafe.config.ConfigFactory;

public class SettingsHelper {

    public String getRabbitMQUserName(){
        return ConfigFactory.load().getString("rabbitmq.user");
    }

    public String getRabbitMQPassword(){
        return ConfigFactory.load().getString("rabbitmq.password");
    }
}
