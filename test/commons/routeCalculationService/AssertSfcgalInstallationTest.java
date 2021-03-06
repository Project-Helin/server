package service.routeCalculationService;

import service.AbstractIntegrationTest;
import org.junit.Test;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.Query;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * SFCGAL is a library used by PostGis. It is not part of PostGis and must therefore
 * be installed separately. This tests verifies - that SFCGAL ist correctly installed.
 */
public class AssertSfcgalInstallationTest extends AbstractIntegrationTest {

    @Inject
    private JPAApi jpaApi;

    /**
     * So It seems that casting with postgres and JPA parameter don't like each other.
     * so ::geometry does not work with jpa -> because JPAs feelings are hurt and it seems
     * to interpret that as a parameter.
     *
     * This tests is prove that it is possible to use casting, but with escaping each colon.
     */
    @Test
    public void testCastingWithJPA(){
        Boolean doesIntersect = jpaApi.withTransaction((em) -> {
            Query query = jpaApi.em().createNativeQuery(
                "SELECT ST_Intersects('POINT(0 0)'\\:\\:geometry, 'LINESTRING ( 0 0, 0 2 )'\\:\\:geometry);"
            );
            return (Boolean) query.getSingleResult();
        });
        assertThat(doesIntersect).isTrue();
    }

    @Test
    public void assertSfcgalIsInstalledInPostgis(){
        String testPolygon = "POLYGON((8.81647686634051 47.2235972073977," +
                                      "8.81666981124281 47.2234438666848," +
                                      "8.81619360680309 47.2231567001565," +
                                      "8.8160622400611 47.2230535428686," +
                                      "8.81595550458323 47.2230145103289," +
                                      "8.81565171899238 47.2232598572438," +
                                      "8.8154505636687 47.2234578067679," +
                                      "8.81533972298015 47.2237003636278," +
                                      "8.81538898550839 47.2238313996306," +
                                      "8.81543003761526 47.2238564915941," +
                                      "8.81599245147942 47.2233434979779," +
                                      "8.81607455569316 47.2233044656518," +
                                      "8.81609918695728 47.2233797422551," +
                                      "8.81609918695728 47.2233797422551," +
                                      "8.81647686634051 47.2235972073977))";

        String polygonAsBinary = "ST_GeomFromText('" + testPolygon + "', 4326)";
        String sql = "select ST_asText(ST_ApproximateMedialAxis(" + polygonAsBinary + "));";

        String calculatedMedialAxis = jpaApi.withTransaction((em) -> {
            Query query = jpaApi.em().createNativeQuery(sql);
            return (String) query.getSingleResult();
        });

        String expectedMedialAxis = "MULTILINESTRING((8.81542480157503 47.2238017861246,8.81544827593939 47.2237041119867)," +
                                                    "(8.81612055126825 47.2231972919686,8.81614322549452 47.2232296877017)," +
                                                    "(8.81612055126825 47.2231972919686,8.81599026768789 47.2231827764133)," +
                                                    "(8.81614322549452 47.2232296877017,8.81619845658185 47.2232990887558)," +
                                                    "(8.81544827593939 47.2237041119867,8.81556791970555 47.2235400094954)," +
                                                    "(8.81646639433932 47.2234569928832,8.81619845658185 47.2232990887558)," +
                                                    "(8.81591402905597 47.2232267826139,8.81598347117314 47.2231830933168)," +
                                                    "(8.81591402905597 47.2232267826139,8.81574877132104 47.223368661629)," +
                                                    "(8.81556791970555 47.2235400094954,8.81574877132104 47.223368661629)," +
                                                    "(8.81599026768789 47.2231827764133,8.81598347117314 47.2231830933168))";

        assertEquals(calculatedMedialAxis, expectedMedialAxis);
    }

}
