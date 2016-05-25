package dto.api;

import ch.helin.messages.dto.way.Position;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class OrderApiDto {
    private String customerId;
    private Position customerPosition;
    private String projectId;

    private List<OrderProductApiDto> orderProducts;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerID(String customerID) {
        this.customerId = customerId;
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

    public String getProjectId() {
        return projectId;
    }

    public OrderApiDto setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
