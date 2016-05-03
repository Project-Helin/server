package commons.gis;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Explore features from JGraphT library - which is used to
 * calculate route in a graph
 */
public class JGraphTExplorationTest {

    @Test
    public void findSimpleRoute(){
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // add the vertices
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");

        // add edges to create a circuit like:
        /**
         * v1 - v2
         *      |
         * v4 - v3
         */
        graph.addEdge("v1", "v2");
        graph.addEdge("v2", "v3");
        graph.addEdge("v3", "v4");

        List<String> route = findRoute(graph, "v1", "v4");
        assertThat(route).containsExactly("(v1 : v2)", "(v2 : v3)", "(v3 : v4)");
    }

    @Test
    public void findRouteInACycle(){
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // add the vertices
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");

        // add edges to create a circuit like:
        /**
         * v1 - v2
         * |    |
         * v4 - v3
         */
        graph.addEdge("v1", "v2");
        graph.addEdge("v2", "v3");
        graph.addEdge("v3", "v4");
        graph.addEdge("v4", "v1");

        List<String> route = findRoute(graph, "v1", "v4");
        assertThat(route).containsExactly("(v4 : v1)");
    }

    @Test
    public void findRouteInARoadWithCrossRoad(){
        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // add the vertices
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.addVertex("v4");
        graph.addVertex("v5");
        graph.addVertex("v6");

        // add edges to create a circuit like:
        /**
         * v1 - v2 - v3
         * |    |
         * v4 - v5 - v6
         */
        graph.addEdge("v1", "v2");
        graph.addEdge("v2", "v3");
        graph.addEdge("v1", "v4");
        graph.addEdge("v2", "v5");
        graph.addEdge("v4", "v5");
        graph.addEdge("v5", "v6");

        assertThat(findRoute(graph, "v1", "v4"))
                .containsExactly("(v1 : v4)");
        assertThat(findRoute(graph, "v1", "v3"))
                .containsExactly("(v1 : v2)", "(v2 : v3)");
        assertThat(findRoute(graph, "v1", "v5"))
                .containsExactly("(v1 : v2)", "(v2 : v5)");

        assertThat(findRoute(graph, "v1", "v6"))
                .containsExactly("(v1 : v2)", "(v2 : v5)", "(v5 : v6)");
    }

    private List<String> findRoute(UndirectedGraph<String, DefaultEdge> graph, String startEdge, String endEdge) {
        List<DefaultEdge> foundPath = DijkstraShortestPath.findPathBetween(graph, startEdge, endEdge);
        return foundPath.stream().map(DefaultEdge::toString).collect(Collectors.toList());
    }

}
