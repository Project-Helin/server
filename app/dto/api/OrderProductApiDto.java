package dto.api;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class OrderProductApiDto {
    private Integer amount;
    private String productId;
    private String projectId;

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

    public String getProjectId() {
        return projectId;
    }

    public OrderProductApiDto setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }
}
