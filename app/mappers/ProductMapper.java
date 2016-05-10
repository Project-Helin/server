package mappers;

import ch.helin.messages.dto.ProductDto;
import models.Product;

public class ProductMapper {

    public ProductDto convertToProductDto(Product product) {
        ProductDto productDto = new ProductDto();

        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());

        return productDto;
    }
}
