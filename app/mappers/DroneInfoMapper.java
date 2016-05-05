package mappers;

import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.Position;
import commons.gis.GisHelper;
import models.DroneInfo;
import org.geolatte.geom.Point;

public class DroneInfoMapper {

    public DroneInfo convertToDroneInfo (DroneInfoMessage droneInfoMessage) {
        DroneInfo droneInfo = new DroneInfo();
        Position phonePosition = droneInfoMessage.getPhonePosition();
        GpsState gpsState = droneInfoMessage.getGpsState();
        BatteryState batteryState = droneInfoMessage.getBatteryState();
        DroneState droneState = droneInfoMessage.getDroneState();

        if (phonePosition != null) {
            Point phoneCoordinates = GisHelper.createPoint(phonePosition.getLat(), phonePosition.getLon());
            droneInfo.setPhonePosition(phoneCoordinates);
        }

        if (gpsState != null) {
            Point droneCoordinates = GisHelper.createPoint(gpsState.getPosLat(), gpsState.getPosLon());
            droneInfo.setSatellitesCount(gpsState.getSatellitesCount());
            droneInfo.setDronePosition(droneCoordinates);
        }

        if (droneState != null) {
            droneInfo.setGroundSpeed(droneState.getGroundSpeed());
            droneInfo.setTargetAltitude(droneState.getTargetAltitude());
            droneInfo.setAltitude(droneState.getAltitude());
            droneInfo.setVerticalSpeed(droneState.getVerticalSpeed());
            droneInfo.setGroundSpeed(droneState.getVerticalSpeed());
            droneInfo.setConnectedToDrone(droneState.isConnected());
        }

        //Battery-Attributes
        if (batteryState != null) {
            droneInfo.setBatteryVoltage(batteryState.getVoltage());
            droneInfo.setRemainingBatteryPercent(batteryState.getRemain());
            droneInfo.setBatteryDischarge(batteryState.getDischarge());
            droneInfo.setClientTime(droneInfoMessage.getClientTime());
        }

        droneInfo.setClientTime(droneInfoMessage.getClientTime());

        return droneInfo;

    }



}
