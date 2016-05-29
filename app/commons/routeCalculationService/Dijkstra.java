package commons.routeCalculationService;


import org.geolatte.geom.LineString;
import org.geolatte.geom.Position;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.Pseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class Dijkstra {

    private static final Logger logger = LoggerFactory.getLogger(Dijkstra.class);

    private Pseudograph<Position, LineString> graph = new Pseudograph<>(LineString.class);

    public Dijkstra(RawGraph rawGraph){

        for (LineString lineString : rawGraph.getLineStringList()) {
            graph.addVertex(lineString.getStartPosition());
            graph.addVertex(lineString.getEndPosition());
            graph.addEdge(lineString.getStartPosition(), lineString.getEndPosition(), lineString);
        }

        logger.debug("Generated graph for Dijkstra: {}", graph.toString());

    }

    public List<Position> calculateShortestPath(Position dronePosition,
                                                Position customerPosition){

        DijkstraShortestPath<Position, LineString> algorithm =
                new DijkstraShortestPath<>(graph, dronePosition, customerPosition);

        if (!isGraphFullyConnected()){
            throw new RuntimeException("Graph is not fully connected!");
        }

        GraphPath<Position, LineString> path = algorithm.getPath();
        if (path == null) {
            throw new RuntimeException("Path not found");
        }

        List<org.geolatte.geom.Position> pathVertexList = Graphs.getPathVertexList(path);
        logger.debug("Path-Vertex list: {}", pathVertexList);

        return pathVertexList;
    }


    private boolean isGraphFullyConnected(){
        ConnectivityInspector<Position, LineString> connectivityInspector =
                new ConnectivityInspector<>(graph);

        logger.info("Is connected: {}", connectivityInspector.isGraphConnected());
        for (Set<Position> positions : connectivityInspector.connectedSets()) {
            logger.info("Connected sets: {}", positions);
        }

        return connectivityInspector.isGraphConnected();

    }

}
