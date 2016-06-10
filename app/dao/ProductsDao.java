package dao;

import service.gis.GisHelper;
import dto.api.OrganisationApiDto;
import dto.api.ProductApiDto;
import models.*;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductsDao extends AbstractDao<Product> {

    public ProductsDao() {
        super(Product.class, "products");
    }

    public List<Product> findByOrganisation(Organisation organisation) {
        Objects.requireNonNull(organisation);

        TypedQuery<Product> query = jpaApi.em().createQuery(
            "select p " +
                " from products p " +
                " where p.organisation = :organisation", Product.class);
        query.setParameter("organisation", organisation);
        return query.getResultList();
    }

    public Product findByIdAndOrganisation(UUID productId, Organisation organisation) {
        Objects.requireNonNull(productId);
        Objects.requireNonNull(organisation);

        TypedQuery<Product> query = jpaApi.em().createQuery(
            "select p from products  p " +
                " where p.organisation = :organisation " +
                " and p.id = :productId", Product.class);

        query.setParameter("organisation", organisation);
        query.setParameter("productId", productId);

        return query.getSingleResult();
    }

    public List<ProductApiDto> findByPosition(Double lat, Double lon) {
        String wkt = GisHelper.toWktStringWithSrid(GisHelper.createPoint(lon, lat));
        /**
         * So this sql need explanation:
         * we need \\:\\:varchar otherwise Hibernate does not understand,
         * how to parse uuid type to String -> and throws some random ugly
         * 'hibernate no dialect mapping for jdbc type 1111' exception.
         */
        Query nativeQuery = jpaApi.em().createNativeQuery(
                " select " +
                "  p.id\\:\\:varchar as id, " +
                "  p.name as name, " +
                "  p.price as price, " +
                "  project.id\\:\\:varchar  as projectId, " +
                "  o.id\\:\\:varchar  as organisationId, " +
                "  o.name as organisationName " +
                " from zones z " +
                " join projects_products pp on pp.project_id = z.project_id  " +
                " join products p on p.id = pp.product_id  " +
                " join projects project on project.id = pp.project_id  " +
                " join organisations o on o.id = project.organisation_id  " +
                " where z.type = :type and st_contains(z.polygon\\:\\:geometry, '" + wkt + "' ) = true"
        );

        nativeQuery.setParameter("type", ZoneType.OrderZone.name());

        List<Object[]> objects = nativeQuery.getResultList();
        return objects.stream()
            .map(o -> {
                ProductApiDto productApiDto = new ProductApiDto();
                productApiDto.setId(String.valueOf(o[0]));
                productApiDto.setName(String.valueOf(o[1]));
                productApiDto.setPrice(Double.valueOf(String.valueOf(o[2])));
                productApiDto.setProjectId(String.valueOf(o[3]));

                OrganisationApiDto organisationApiDto = new OrganisationApiDto();
                organisationApiDto.setId(String.valueOf(o[4]));
                organisationApiDto.setName(String.valueOf(o[5]));
                productApiDto.setOrganisation(organisationApiDto);

                return productApiDto;
            })
            .collect(Collectors.toList());
    }

    public List<Product> findByProjectId(UUID projectId) {
        Query query = jpaApi.em().createQuery(
            "select p from products  p " +
                " join p.projects as pj " +
                " where pj. id = :projectId", Product.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }
}
