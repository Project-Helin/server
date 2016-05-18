package models;

import org.geolatte.geom.Point;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "drone_infos")
public class DroneInfo extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drone_id")
    private Drone drone;

    private Point dronePosition;
    private Point phonePosition;
    private double remainingBatteryPercent;
    private double batteryDischarge;
    private double batteryVoltage;
    private double altitude;
    private double targetAltitude;
    private double verticalSpeed;
    private double groundSpeed;
    private boolean isConnectedToDrone;
    private int satellitesCount;

    private Date clientTime;

    public Point getDronePosition() {
        return dronePosition;
    }

    public void setDronePosition(Point dronePosition) {
        this.dronePosition = dronePosition;
    }

    public Point getPhonePosition() {
        return phonePosition;
    }

    public void setPhonePosition(Point phonePosition) {
        this.phonePosition = phonePosition;
    }

    public double getRemainingBatteryPercent() {
        return remainingBatteryPercent;
    }

    public void setRemainingBatteryPercent(double remainingBatteryPercent) {
        this.remainingBatteryPercent = remainingBatteryPercent;
    }

    public double getBatteryDischarge() {
        return batteryDischarge;
    }

    public void setBatteryDischarge(double batteryDischarge) {
        this.batteryDischarge = batteryDischarge;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getTargetAltitude() {
        return targetAltitude;
    }

    public void setTargetAltitude(double targetAltitude) {
        this.targetAltitude = targetAltitude;
    }

    public double getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(double verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(double groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public boolean isConnectedToDrone() {
        return isConnectedToDrone;
    }

    public void setConnectedToDrone(boolean connectedToDrone) {
        isConnectedToDrone = connectedToDrone;
    }

    public int getSatellitesCount() {
        return satellitesCount;
    }

    public void setSatellitesCount(int satellitesCount) {
        this.satellitesCount = satellitesCount;
    }

    public Date getClientTime() {
        return clientTime;
    }

    public void setClientTime(Date clientTime) {
        this.clientTime = clientTime;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
