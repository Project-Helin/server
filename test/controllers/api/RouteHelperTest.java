package controllers.api;

import ch.helin.messages.dto.Action;
import ch.helin.messages.dto.way.Position;
import models.Route;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by martin on 19.05.16.
 */
public class RouteHelperTest {

    @Test
    public void shouldConvertPositionListToRoute(){
        List<Position> positions = getPositions();

        Route route = RouteHelper.positionListToRoute(positions);

         /*
         * The positions are just a one-way path, so we have to dublicate and reverse it, to find the full path.
         * We remove one, because the drop off command is only executed once!
         * */
        int truePathLength = (positions.size() * 2) - 1;

        assertEquals(truePathLength, route.getWayPoints().size());
    }

    @Test
    public void shouldContainDropZoneInRoute(){

        List<Position> positions = getPositions();
        Route route = RouteHelper.positionListToRoute(positions);

        int numOfDropOffZone = (int) route.getWayPoints().stream().filter(x -> x.getAction() == Action.DROP).count();

        assertEquals(1, numOfDropOffZone);
    }

    private List<Position> getPositions() {
        List<Position> positions = new LinkedList<>();

        Position pos0 = new Position();
        pos0.setHeight(1);
        pos0.setLon(2.0);
        pos0.setLat(3.0);
        positions.add(pos0);

        Position pos1 = new Position();
        pos1.setHeight(4);
        pos1.setLon(5.0);
        pos1.setLat(6.0);
        positions.add(pos1);

        Position pos2 = new Position();
        pos2.setHeight(7);
        pos2.setLon(8.0);
        pos2.setLat(9.0);
        positions.add(pos2);
        return positions;
    }

}