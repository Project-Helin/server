package dto.api;

/**
 * Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class ProductApiDto {
    private String name;
    private Double price;
    private String project;
    private String id;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProject() {
        return project;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
