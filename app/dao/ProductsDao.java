package dao;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import commons.gis.GisHelper;
import models.*;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryType;
import org.hibernate.spatial.GeolatteGeometryJavaTypeDescriptor;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    public List<Product> findByPosition(Double lat, Double lon) {
        String wkt = GisHelper.toWktStringWithSrid(GisHelper.createPoint(lon, lat));
        Query nativeQuery = jpaApi.em().createNativeQuery(
            " select " +
            "  p.* " +
            " from zones z " +
            " join projects_products pp on pp.project_id = z.project_id  " +
            " join products p on p.id = pp.product_id  " +
            " where z.type = :type and st_contains(z.polygon\\:\\:geometry, '" + wkt + "' ) = true",
            Product.class
        );

        nativeQuery.setParameter("type", ZoneType.DeliveryZone.name());

        List<Product> resultList = nativeQuery.getResultList();
        return resultList;
    }


    private com.vividsolutions.jts.geom.Geometry wktToGeometry(String wktPoint) {
        WKTReader fromText = new WKTReader();
        com.vividsolutions.jts.geom.Geometry geom = null;
        try {
            geom = fromText.read(wktPoint);
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT string:" + wktPoint);
        }
        return geom;
    }

}
