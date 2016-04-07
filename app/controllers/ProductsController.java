package controllers;

import com.google.inject.Inject;
import dao.OrganisationsDao;
import dao.ProductsDao;
import models.Organisation;
import models.Product;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.products.add;
import views.html.products.index;
import views.html.products.edit;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
public class ProductsController extends Controller {

    @Inject
    private OrganisationsDao organisationsDao;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private FormFactory formFactory;

    public Result index() {
        List<Product> all =
            productsDao.findAll();
        return ok(index.render(all));
    }

    public Result add() {
        Form<Product> form = formFactory
            .form(Product.class)
            .fill(new Product());

        return ok(add.render(form));
    }

    public Result create() {
        Form<Product> form = formFactory
            .form(Product.class)
            .bindFromRequest(request());

        Product product = form.get();
        product.setId(UUID.randomUUID());
        product.setOrganisation(getOrganisation());
        productsDao.persist(product);

        return index();
    }


    public Result edit(UUID id) {
        Product found = productsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }

        Form<Product> form = formFactory
            .form(Product.class)
            .fill(found);

        return ok(edit.render(form));
    }

    public Result update(UUID id) {
        Product found = productsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }
        productsDao.persist(found);

        return index();
    }

    public Result delete(UUID id) {
        Product found = productsDao.findById(id);

        if (found == null) {
            return forbidden("Organisation not found!");
        }
        productsDao.delete(found);
        return index();
    }

    private Organisation getOrganisation() {
        /**
         * TODO
         * For now -> HSR is always there
         */
        return organisationsDao
                .findAll()
                .stream()
                .filter(new Predicate<Organisation>() {
                    @Override
                    public boolean test(Organisation organisation) {
                        return organisation.getName().equals("HSR");
                    }
                })
                .collect(Collectors.toList()).get(0);
    }
}
