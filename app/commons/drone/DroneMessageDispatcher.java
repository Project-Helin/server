package commons.drone;

import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class DroneMessageDispatcher {

    public DroneMessageDispatcher() {
        //Inject all MessageHandlingControllers
    }

    public void dispatchMessage (UUID droneId, String message ) {
        //Deserialize Message Content and call controller which handles this messagetype
    }

}
