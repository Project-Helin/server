package dao;

import models.DroneInfo;

public class DroneInfoDao extends AbstractDao<DroneInfo> {

    public DroneInfoDao() {
        super(DroneInfo.class, "drone_infos");
    }
}
