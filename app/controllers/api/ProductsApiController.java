package controllers.api;

import ch.helin.messages.dto.ProductDto;
import com.google.inject.Inject;
import dao.ProductsDao;
import dto.api.ProductApiDto;
import mappers.ProductMapper;
import models.Product;
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class ProductsApiController extends Controller {

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProductMapper productMapper;

    private static final Logger logger = getLogger(ProductsApiController.class);

    @Transactional
    public Result findByLocation(Double lat, Double lon) {
        logger.info("Find by position: lat = {} lon = {}", lat, lon);

        List<ProductApiDto> products = productsDao.findByPosition(lat, lon);
        return ok(Json.toJson(products));
    }

    @Transactional
    public Result findByProject(String projectId) {
        List<Product> products = productsDao.findByProjectId(UUID.fromString(projectId));

        List<ProductDto> productDtos = products
            .stream()
            .map(productMapper::convertToProductDto)
            .collect(Collectors.toList());

        return ok(Json.toJson(productDtos));
    }
}
