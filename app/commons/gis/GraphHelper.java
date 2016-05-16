package commons.gis;

import org.geolatte.geom.LineString;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.Position;
import org.jgrapht.Graph;

public class GraphHelper {


    public static String convertToWKT(Graph<Position, LineString> graph){

        LineString[] lineStrings = graph.edgeSet().toArray(new LineString[graph.edgeSet().size()]);

        MultiLineString collectedGraph = new MultiLineString(lineStrings);

        return collectedGraph.toString();
    }


}
