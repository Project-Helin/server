package service;

public class SettingsHelper {

    public static final String RABBIT_MQ_USER = "admin";
    public static final String RABBIT_MQ_PASSWORD = "helin";

    public String getRabbitMQUserName(){
        return RABBIT_MQ_USER;
    }

    public String getRabbitMQPassword(){
        return RABBIT_MQ_PASSWORD;
    }
}
