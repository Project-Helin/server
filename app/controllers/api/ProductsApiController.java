package controllers.api;

import com.google.inject.Inject;
import dao.ProductsDao;
import dto.api.ProductApiDto;
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class ProductsApiController extends Controller {

    @Inject
    private ProductsDao productsDao;

    private static final Logger logger = getLogger(ProductsApiController.class);

    @Transactional
    public Result findByLocation(Double lat, Double lon) {
        logger.info("Find by position: lat = {} lon = {}", lat, lon);
        List<ProductApiDto> products = productsDao.findByPosition(lat, lon);
        return ok(Json.toJson(products));
    }
}
