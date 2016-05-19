package mappers;

import ch.helin.messages.dto.DroneInfoDto;
import ch.helin.messages.dto.message.DroneInfoMessage;
import ch.helin.messages.dto.state.BatteryState;
import ch.helin.messages.dto.state.DroneState;
import ch.helin.messages.dto.state.GpsState;
import ch.helin.messages.dto.way.Position;
import commons.gis.GisHelper;
import models.DroneInfo;
import org.geolatte.geom.Point;

public class DroneInfoMapper {

    public DroneInfo convertToDroneInfo(DroneInfoMessage droneInfoMessage) {
        DroneInfo droneInfo = new DroneInfo();
        DroneInfoDto droneInfoDto = droneInfoMessage.getDroneInfo();
        Position phonePosition = droneInfoDto.getPhonePosition();
        GpsState gpsState = droneInfoDto.getGpsState();
        BatteryState batteryState = droneInfoDto.getBatteryState();
        DroneState droneState = droneInfoDto.getDroneState();

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
        }

        droneInfo.setClientTime(droneInfoDto.getClientTime());

        return droneInfo;
    }

    public DroneInfoDto convertToDroneInfoDto(DroneInfo droneInfo) {
        DroneInfoDto droneInfoDto = new DroneInfoDto();

        droneInfoDto.setId(droneInfo.getId());

        //General Attributes
        Position phonePosition = GisHelper.createPosition(droneInfo.getPhonePosition());
        droneInfoDto.setPhonePosition(phonePosition);

        droneInfoDto.setClientTime(droneInfo.getClientTime());

        //droneState
        DroneState droneState = new DroneState();
        droneState.setVerticalSpeed(droneInfo.getVerticalSpeed());
        droneState.setAltitude(droneInfo.getAltitude());
        droneState.setGroundSpeed(droneInfo.getGroundSpeed());
        droneState.setIsConnected(droneInfo.isConnectedToDrone());
        droneState.setTargetAltitude(droneInfo.getTargetAltitude());

        droneInfoDto.setDroneState(droneState);

        //GPSState
        GpsState gpsState = new GpsState();
        gpsState.setPosLat(droneInfo.getDronePosition().getPosition().getCoordinate(0));
        gpsState.setPosLon(droneInfo.getDronePosition().getPosition().getCoordinate(1));
        gpsState.setSatellitesCount(droneInfo.getSatellitesCount());

        droneInfoDto.setGpsState(gpsState);

        //BatteryState

        BatteryState batteryState = new BatteryState();
        batteryState.setDischarge(droneInfo.getBatteryDischarge());
        batteryState.setVoltage(droneInfo.getBatteryVoltage());
        batteryState.setRemain(droneInfo.getRemainingBatteryPercent());

        droneInfoDto.setBatteryState(batteryState);

        return droneInfoDto;
    }


}
