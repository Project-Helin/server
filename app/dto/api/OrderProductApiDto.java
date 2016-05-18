package dto.api;

public class OrderProductApiDto {
    private String id;
    private Integer amount;

    public Integer getAmount() {
        return amount;
    }

    public OrderProductApiDto setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public String getId() {
        return id;
    }

    public OrderProductApiDto setId(String id) {
        this.id = id;
        return this;
    }
}
