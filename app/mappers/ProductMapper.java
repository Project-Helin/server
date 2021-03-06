package mappers;

import ch.helin.messages.dto.ProductDto;
import models.Product;

public class ProductMapper {

    public ProductDto convertToProductDto(Product product) {
        ProductDto productDto = new ProductDto();

        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setWeightGramm(product.getWeightGramm());

        return productDto;
    }
}
