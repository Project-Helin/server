package controllers;


import commons.MessageService;
import com.google.inject.Inject;

public class DroneStateController {

    @Inject
    MessageService messageService;

    public void initialize() {
        messageService.addGpsStateHandler((state) -> System.out.println("---------------------hahahahaha ----------------------"));
    }

}
