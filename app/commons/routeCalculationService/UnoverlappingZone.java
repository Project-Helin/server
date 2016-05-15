package commons.routeCalculationService;

import models.Zone;
import org.geolatte.geom.Polygon;

public class UnoverlappingZone {

    private Polygon polygon;
    private int height;


    public UnoverlappingZone(Zone zone) {
        this.polygon = zone.getPolygon();
        this.height = zone.getHeight();
    }


    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
