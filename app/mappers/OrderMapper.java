package mappers;

import ch.helin.messages.dto.OrderDto;
import com.google.inject.Inject;
import commons.gis.GisHelper;
import models.Order;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class OrderMapper {

    @Inject
    private MissionMapper missionMapper;

    @Inject
    private OrderProductsMapper orderProductsMapper;


    public OrderDto convertToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());

        orderDto.setCreatedAt(fromLdt(order.getCreatedAt()));
        orderDto.setCustomerPosition(GisHelper.createPosition(order.getCustomerPosition()));
        if (order.getCustomer() != null) {
            orderDto.setCustomerName(order.getCustomer().getFamilyName());
        }

        orderDto.setMissions(order.getMissions().stream().map(missionMapper::convertToMissionDto).collect(Collectors.toList()));
        orderDto.setOrderProducts(order.getOrderProducts().stream().map(orderProductsMapper::convertToOrderProductDto).collect(Collectors.toList()));
        orderDto.setProjectId(order.getProject().getId());
        orderDto.setState(order.getState().name());

        return orderDto;
    }

    static public Date fromLdt(LocalDateTime ldt) {
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        GregorianCalendar cal = GregorianCalendar.from(zdt);
        return cal.getTime();
    }


}
