package controllers;

import ch.helin.messages.dto.message.missionMessage.ConfirmMissionMessage;
import ch.helin.messages.dto.message.missionMessage.FinalAssignMissionMessage;
import ch.helin.messages.dto.message.missionMessage.MissionConfirmType;
import com.google.inject.Inject;
import commons.SessionHelper;
import commons.drone.DroneCommunicationManager;
import dao.DroneDao;
import dao.MissionsDao;
import mappers.MissionMapper;
import models.Drone;
import models.Mission;
import models.MissionState;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;
import java.util.UUID;


public class MissionController extends Controller{

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private DroneDao droneDao;

    @Inject
    private JPAApi jpaApi;

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private DroneCommunicationManager droneCommunicationManager;

    public void onConfirmMissionMessageReceived(UUID droneId, ConfirmMissionMessage missionMessage) {
        jpaApi.withTransaction(() -> {
            boolean missionConfirmed = missionMessage.getMissionConfirmType() == MissionConfirmType.ACCEPT;
            Drone drone = droneDao.findById(droneId);

            Mission mission = drone.getCurrentMission();
            if (missionConfirmed) {
                mission.setState(MissionState.LOADING);
                FinalAssignMissionMessage finalAssignMissionMessage = new FinalAssignMissionMessage();
                finalAssignMissionMessage.setMission(missionMapper.convertToMissionDto(mission));
                droneCommunicationManager.sendMessageToDrone(drone.getId(), finalAssignMissionMessage);

            } else {
                mission.setState(MissionState.WAITING_FOR_FREE_DRONE);
                drone.setCurrentMission(null);
            }

            droneDao.persist(drone);
        });
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result index() {
        List<Mission> all = missionsDao.findByOrganisation(sessionHelper.getOrganisation(session()));
        return ok(views.html.missions.index.render(all));
    }

    @Transactional
    public Result show(UUID missionId) {
        Mission found = missionsDao.findById(missionId);
        return ok(views.html.missions.show.render(found.getOrder().getProject().getIdAsString(), found.getIdAsString()));
    }

}
