package commons.routeCalculationService;

import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;

import java.util.ArrayList;
import java.util.List;

public class RawGraph {

    private final List<LineString> lineStrings = new ArrayList<>();

    public void addLineString(LineString lineString){
        lineStrings.add(lineString);
    }

    public void addMultiLineString(MultiLineString multiLineString){
        for(int i=0; i<multiLineString.getNumGeometries(); i++){
            this.addLineString((LineString) multiLineString.getGeometryN(i));
        }
    }

    public List<LineString> getLineStringList() {
        return lineStrings;
    }

}
