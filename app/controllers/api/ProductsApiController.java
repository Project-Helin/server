package controllers.api;

import ch.helin.messages.dto.ProductDto;
import com.google.inject.Inject;
import dao.ProductsDao;
import mappers.ProductMapper;
import models.Product;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class ProductsApiController extends Controller {

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProductMapper productMapper;

    @Transactional
    public Result index() {
        List<Product> products = productsDao.findAll();

        List<ProductDto> productDtos =
            products
                .stream()
                .map(productMapper::convertToProductDto)
                .collect(Collectors.toList());
        return ok(Json.toJson(productDtos));
    }


}
