package Repositories.LAPR;

import Model.LAPR.Route;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing routes in memory (Business Requirement).
 */
public class RouteRepository {
    private List<Route> routes;
    private static RouteRepository instance = new RouteRepository();

    private RouteRepository() {
        this.routes = new ArrayList<>();
    }

    public static RouteRepository getInstance() {
        return instance;
    }

    public void addRoute(Route r) {
        if (r != null) {
            this.routes.add(r);
        }
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }
}