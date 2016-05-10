package mappers;

import ch.helin.messages.dto.OrderProductDto;
import com.google.inject.Inject;
import models.OrderProduct;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderProductsMapper {

    @Inject
    private ProductMapper productMapper;


    public List<OrderProductDto> convertToOrderProductList (Set<OrderProduct> orderProducts) {
        return orderProducts.stream().map(this::convertToOrderProductDto).collect(Collectors.toList());
    }

    public OrderProductDto convertToOrderProductDto(OrderProduct orderProduct) {
        OrderProductDto orderProductDto = new OrderProductDto();

        orderProductDto.setAmount(orderProduct.getAmount());
        orderProductDto.setProduct(productMapper.convertToProductDto(orderProduct.getProduct()));
        orderProductDto.setTotalPrice(orderProduct.getTotalPrice());

        return orderProductDto;
    }




}
