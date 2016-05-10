package dao;


import models.Route;

public class RouteDao extends AbstractDao<Route>{
    public RouteDao() {
        super(Route.class, "routes");
    }
}
