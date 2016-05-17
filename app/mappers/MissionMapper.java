package mappers;

import ch.helin.messages.dto.MissionDto;
import com.google.inject.Inject;
import models.Mission;

public class MissionMapper {

    @Inject
    private RouteMapper routeMapper;

    @Inject
    private OrderProductsMapper orderProductsMapper;

    public MissionDto convertToMissionDto (Mission mission) {
        MissionDto missionDto = new MissionDto();

        missionDto.setRoute(routeMapper.convertToRouteDto(mission.getRoute()));
        missionDto.setOrderProduct(orderProductsMapper.convertToOrderProductDto(mission.getOrderProduct()));
        return missionDto;
    }


}
