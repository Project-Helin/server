package dao;

import models.Mission;

public class MissionsDao extends AbstractDao<Mission> {

    public MissionsDao() {
        super(Mission.class, "missions");
    }
}
