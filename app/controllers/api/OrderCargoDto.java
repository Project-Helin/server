package controllers.api;

import ch.helin.messages.dto.OrderProductDto;
import ch.helin.messages.dto.ProductDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrderCargoDto {
    private String displayName;
    private String email;

    private List<ApiOrderProductDto> orderProducts;

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

    public List<ApiOrderProductDto> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<ApiOrderProductDto> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
