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
import play.mvc.Security;
import views.html.products.add;
import views.html.products.edit;
import views.html.products.index;

import java.util.List;
import java.util.UUID;

@Transactional
public class ProductsController extends Controller {

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private FormFactory formFactory;

    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result index() {
        List<Product> all = productsDao.findByOrganisation(sessionHelper.getOrganisation(session()));
        return ok(index.render(all));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result add() {
        Form<Product> form = formFactory
            .form(Product.class)
            .fill(new Product());

        return ok(add.render(form));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result create() {
        Form<Product> form = formFactory
            .form(Product.class)
            .bindFromRequest(request());

        if (form.hasErrors()) {

            logger.info("Has error, go back {}", form.errorsAsJson());
            return badRequest(add.render(form));

        } else {

            Product product = form.get();
            product.setId(UUID.randomUUID());
            product.setOrganisation(sessionHelper.getOrganisation(session()));
            productsDao.persist(product);

            flash("success", "Saved successfully");
            return redirect(routes.ProductsController.index());
        }
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    public Result edit(UUID id) {
        Product found = findProduct(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Product> form = formFactory
            .form(Product.class)
            .fill(found);

        return ok(edit.render(form));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
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
            return redirect(routes.ProductsController.index());

        }
    }

    @Security.Authenticated(SecurityAuthenticator.class)
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
