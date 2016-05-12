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

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
