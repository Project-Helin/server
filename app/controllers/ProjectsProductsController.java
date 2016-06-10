package controllers;

import com.google.inject.Inject;
import service.SessionHelper;
import dao.ProductsDao;
import dao.ProjectsDao;
import models.Product;
import models.Project;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.projectsProducts.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ProjectsProductsController extends Controller {

    @Inject
    private SessionHelper sessionHelper;

    @Inject
    private ProductsDao productsDao;

    @Inject
    private ProjectsDao projectsDao;

    @Inject
    private FormFactory formFactory;

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result index(UUID projectId) {
        Project foundProject = getProject(projectId);

        if (foundProject == null) {
            return forbidden("Project not found!");
        }

        List<Product> products = new ArrayList<>(foundProject.getProducts());
        Collections.sort(products, (a, b) -> a.getName().compareTo(b.getName()));

        // possible products to add
        List<Product> missingProducts = productsDao.findByOrganisation(sessionHelper.getOrganisation(session()));
        missingProducts.removeAll(products);

        return ok(index.render(projectId, products, missingProducts));
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result addProduct(UUID projectId) {
        Project foundProject = getProject(projectId);

        if (foundProject == null) {
            return forbidden("Project not found!");
        }

        Product newProductToAdd = getProductFromRequest();
        if (newProductToAdd == null) {
            return forbidden("Product not found!");
        }

        foundProject.getProducts().add(newProductToAdd);
        projectsDao.persist(foundProject);

        return index(projectId);
    }

    @Security.Authenticated(SecurityAuthenticator.class)
    @Transactional
    public Result delete(UUID projectId, UUID productId) {
        Project foundProject = getProject(projectId);

        if (foundProject == null) {
            return forbidden("Project not found!");
        }

        Product productToDelete = findProduct(productId);
        if (productToDelete == null) {
            return forbidden("Product not found!");
        }

        foundProject.getProducts().remove(productToDelete);
        productsDao.persist(productToDelete);

        return redirect(routes.ProjectsProductsController.index(projectId));
    }

    private Product getProductFromRequest() {
        DynamicForm dynamicForm = formFactory.form().bindFromRequest(request());
        String productId = dynamicForm.get("productId");
        return findProduct(UUID.fromString(productId));
    }

    private Project getProject(UUID projectId) {
        return projectsDao.findByIdAndOrganisation(projectId, sessionHelper.getOrganisation(session()));
    }

    private Product findProduct(UUID id) {
        return productsDao.findByIdAndOrganisation(id, sessionHelper.getOrganisation(session()));
    }
}
