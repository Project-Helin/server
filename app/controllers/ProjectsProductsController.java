package controllers;

import com.google.inject.Inject;
import commons.ModelHelper;
import commons.SessionHelper;
import dao.ProductsDao;
import models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.products.edit;

import java.util.UUID;

@Transactional
public class ProjectsProductsController extends Controller {

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private FormFactory formFactory;

    private static final Logger logger = LoggerFactory.getLogger(ProjectsProductsController.class);

    public Result edit(UUID projectId) {
        Product found = findProduct(projectId);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Product> form = formFactory
            .form(Product.class)
            .fill(found);

        return ok(edit.render(form));
    }

    public Result update(UUID id) {
        Product found = findProduct(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Product> form = formFactory
            .form(Product.class)
            .bindFromRequest(request());

        if (form.hasErrors()) {

            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(edit.render(form));

        } else {

            ModelHelper.updateAttributes(found, form.get());
            productsDao.persist(found);
            // return redirect(ProjectsProductsController.index());
            return null;

        }
    }

    public Result delete(UUID id) {
        Product found = findProduct(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        flash("success", "Deleted successfully");
        productsDao.delete(found);
        return index();
    }

    private Product findProduct(UUID id) {
        return productsDao.findByIdAndOrganisation(id, sessionHelper.getOrganisation(session()));
    }
}
