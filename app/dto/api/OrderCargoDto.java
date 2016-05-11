package dto.api;

import controllers.api.OrderProductApiDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrderCargoDto {
    private String displayName;
    private String email;

    private List<OrderProductApiDto> orderProducts;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<OrderProductApiDto> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProductApiDto> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
