package Repositories.LAPR;

import Database.DataBaseConnection;
import Model.LAPR.*;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * USLP09 - Repository for Train Assembly operations.
 * Accesses the database through PL/SQL functions to retrieve available rolling stock.
 */
public class TrainAssemblyRepository {

    private static TrainAssemblyRepository instance;

    private TrainAssemblyRepository() {
    }

    public static TrainAssemblyRepository getInstance() {
        if (instance == null) {
            instance = new TrainAssemblyRepository();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DataBaseConnection.getConnection();
    }

    /**
     * Gets all available locomotives for train assembly.
     * Uses PL/SQL function with cursor to retrieve data.
     *
     * @param routeStartStationId the starting station of the route
     * @return list of available locomotives with location information
     */
    public List<Locomotive> getAvailableLocomotives(int routeStartStationId) {
        List<Locomotive> locomotives = new ArrayList<>();
        String sql = "{ ? = call fn_get_available_locomotives_for_route(?) }";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, routeStartStationId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Locomotive loco = new Locomotive(
                        rs.getInt("LOCOMOTIVE_ID"),
                        rs.getString("LOCOMOTIVE_NAME"),
                        rs.getString("MAKE"),
                        rs.getString("MODEL"),
                        rs.getInt("SERVICE_YEAR"),
                        rs.getInt("NUM_BOGIES"),
                        rs.getString("BOGIES"),
                        rs.getInt("POWER"),
                        rs.getDouble("LENGTH"),
                        rs.getDouble("WIDTH"),
                        rs.getDouble("HEIGHT"),
                        rs.getDouble("WEIGHT"),
                        rs.getDouble("MAX_SPEED"),
                        rs.getDouble("OPERATIONAL_SPEED"),
                        rs.getDouble("TRACTION"),
                        rs.getString("LOCO_TYPE"),
                        rs.getDouble("VOLTAGE"),
                        rs.getDouble("FREQUENCY"),
                        rs.getString("OPERATOR"),
                        rs.getInt("GAUGE_ID"),
                        rs.getDouble("FUEL_CAPACITY")
                    );

                    // Removed verbose debug print to avoid showing full locomotive internals and station info
                    // Location and other details will be formatted and shown by the UI layer where needed.

                    // Set location information
                    RollingStockLocation location = loco.getLocation();
                    String status = rs.getString("STATUS");
                    if ("PARKED".equals(status)) {
                        location.setStatus(RollingStockStatus.PARKED);
                        location.setCurrentStationId(rs.getString("CURRENT_STATION_ID"));
                        location.setCurrentStationName(rs.getString("CURRENT_STATION_NAME"));
                    } else if ("IN_TRANSIT".equals(status)) {
                        location.setStatus(RollingStockStatus.IN_TRANSIT);
                        location.setRouteId(rs.getInt("ROUTE_ID"));
                        location.setDestinationStationId(rs.getString("DESTINATION_STATION_ID"));
                        location.setDestinationStationName(rs.getString("DESTINATION_STATION_NAME"));
                    } else {
                        location.setStatus(RollingStockStatus.AVAILABLE);
                    }
                    location.setDistanceFromPoint(rs.getDouble("DISTANCE_FROM_START"));

                    locomotives.add(loco);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving available locomotives: " + e.getMessage());
            e.printStackTrace();
        }

        return locomotives;
    }

    /**
     * Gets all available wagons for train assembly.
     * Uses PL/SQL function with cursor to retrieve data.
     *
     * @param routeStartStationId the starting station of the route
     * @return list of available wagons with location information
     */
    public List<RailwayWagon> getAvailableWagons(int routeStartStationId) {
        List<RailwayWagon> wagons = new ArrayList<>();
        String sql = "{ ? = call fn_get_available_wagons_for_route(?) }";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, routeStartStationId);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    RailwayWagon wagon = new RailwayWagon(
                        rs.getString("WAGON_ID"),
                        rs.getString("WAGON_TYPE"),
                        rs.getString("MAKE"),
                        rs.getString("MODEL"),
                        rs.getDouble("LENGTH"),
                        rs.getDouble("WIDTH"),
                        rs.getDouble("HEIGHT"),
                        rs.getDouble("EMPTY_WEIGHT"),
                        rs.getDouble("MAX_LOAD_CAPACITY"),
                        rs.getDouble("CURRENT_LOAD"),
                        rs.getInt("GAUGE_ID"),
                        rs.getString("OPERATOR")
                    );

                    // Set location information
                    RollingStockLocation location = wagon.getLocation();
                    String status = rs.getString("STATUS");
                    if ("PARKED".equals(status)) {
                        location.setStatus(RollingStockStatus.PARKED);
                        location.setCurrentStationId(rs.getString("CURRENT_STATION_ID"));
                        location.setCurrentStationName(rs.getString("CURRENT_STATION_NAME"));
                    } else if ("IN_TRANSIT".equals(status)) {
                        location.setStatus(RollingStockStatus.IN_TRANSIT);
                        Integer routeId = rs.getInt("ROUTE_ID");
                        if (!rs.wasNull()) {
                            location.setRouteId(routeId);
                        }
                        location.setDestinationStationId(rs.getString("DESTINATION_STATION_ID"));
                        location.setDestinationStationName(rs.getString("DESTINATION_STATION_NAME"));
                    } else {
                        location.setStatus(RollingStockStatus.AVAILABLE);
                    }
                    location.setDistanceFromPoint(rs.getDouble("DISTANCE_FROM_START"));

                    wagons.add(wagon);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving available wagons: " + e.getMessage());
            e.printStackTrace();
        }

        return wagons;
    }

    /**
     * Gets all available routes from the database.
     *
     * @return list of routes
     */
    public List<Route> getAllRoutes() {
        return RouteRepository.getInstance().getRoutes();
    }
}

