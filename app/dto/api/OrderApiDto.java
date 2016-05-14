package dto.api;

import ch.helin.messages.dto.way.Position;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrderApiDto {
    private String displayName;
    private String email;
    private Position customerPosition;

    private List<OrderProductApiDto> orderProducts;

    public String getDisplayName() {
        return displayName;
    }

    public OrderApiDto setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public OrderApiDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public List<OrderProductApiDto> getOrderProducts() {
        return orderProducts;
    }

    public OrderApiDto setOrderProducts(List<OrderProductApiDto> orderProducts) {
        this.orderProducts = orderProducts;
        return this;
    }

    public Position getCustomerPosition() {
        return customerPosition;
    }

    public OrderApiDto setCustomerPosition(Position customerPosition) {
        this.customerPosition = customerPosition;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
