package controllers.api;

import ch.helin.messages.dto.MissionDto;
import com.google.inject.Inject;
import dao.MissionsDao;
import mappers.MissionMapper;
import models.Mission;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.UUID;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class MissionApiController extends Controller {

    @Inject
    private MissionsDao missionsDao;

    @Inject
    private MissionMapper missionMapper;

    public Result show(UUID missionId) {
        Mission mission = missionsDao.findById(missionId);
        if (mission == null) {
            return forbidden("Mission not found");
        }

        MissionDto missionDto = missionMapper.convertToMissionDto(mission);
        return ok(Json.toJson(missionDto));
    }

}
