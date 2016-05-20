package controllers.api;

import com.google.inject.Inject;
import commons.SessionHelper;
import dao.ProductsDao;
import dao.ProjectsDao;
import dto.api.ProductApiDto;
import models.Product;
import models.Project;
import org.slf4j.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Kirusanth Poopalasingam ( pkirusanth@gmail.com )
 */
public class ProductsApiController extends Controller {

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private SessionHelper sessionHelper;

    private static final Logger logger = getLogger(ProductsApiController.class);

    @Transactional
    // TODO this might be not needed -> remove fake order
    public Result index() {
        List<Product> products = productsDao.findAll();

        List<Project> projects =
            projectsDao.findAll(); // TODO fix this ...

        List<ProductApiDto> productDtos =
            products
                .stream()
                .map(product -> {
                    ProductApiDto productApiDto = new ProductApiDto();
                    productApiDto.setName(product.getName());
                    productApiDto.setId(product.getIdAsString());
                    productApiDto.setPrice(product.getPrice());
                    productApiDto.setProjectId(projects.iterator().next().getIdAsString()); // TODO discuss this!
                    return productApiDto;
                })
                .collect(Collectors.toList());
        return ok(Json.toJson(productDtos));
    }

    /**
     * TODO implement this:
     * Input:
     * - Customer location
     *
     * Output:
     * => all products in in delivery zone
     *
     */
    @Transactional
    public Result findByLocation(Double lat, Double lon) {
        logger.info("Find by position: lat = {} lon = {}", lat, lon);
        List<Product> products = productsDao.findByPosition(lat, lon);

        List<Project> projects =
            projectsDao.findAll();

        List<ProductApiDto> productDtos =
            products
                .stream()
                .map(product -> {
                    ProductApiDto productApiDto = new ProductApiDto();
                    productApiDto.setName(product.getName());
                    productApiDto.setId(product.getIdAsString());

                    productApiDto.setPrice(product.getPrice());
                    productApiDto.setProjectId(projects.iterator().next().getIdAsString()); // TODO discuss this!
                    return productApiDto;
                })
                .collect(Collectors.toList());

        return ok(Json.toJson(productDtos));
    }

}
