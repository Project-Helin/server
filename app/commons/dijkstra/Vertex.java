package commons.dijkstra;

import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;

public class Vertex implements Comparable<Vertex>
{
      public final Point point;
      public ArrayList<Edge> adjacencies = new ArrayList<>();
      public double minDistance = Double.POSITIVE_INFINITY;
      public Vertex previous;

      public Vertex(Point point) {
            this.point = point;
      }

      public Point getPoint() {
            return this.point;
      }

      public String toString() {
            return point.toString();
      }

      public int compareTo(Vertex other) {
          return Double.compare(minDistance, other.minDistance);
      }
}