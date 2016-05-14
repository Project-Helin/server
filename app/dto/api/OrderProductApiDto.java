package dto.api;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrderProductApiDto {
    private Integer amount;
    private String productId;

    public Integer getAmount() {
        return amount;
    }

    public OrderProductApiDto setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public OrderProductApiDto setProductId(String productId) {
        this.productId = productId;
        return this;
    }
}
