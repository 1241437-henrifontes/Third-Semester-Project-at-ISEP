package Repositories.LAPR;

import Database.DataBaseConnection;
import Model.LAPR.*;
import oracle.jdbc.OracleTypes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for retrieving scheduling-related data
 * from the Oracle database.
 *
 * <p>This includes trains, locomotives, wagons, facilities,
 * and line segments.</p>
 */
public class SchedulerRepository {

    /**
     * Retrieves all trains from the database.
     *
     * @return a list of trains
     */
    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call fn_get_all_trains() }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                Locomotive loco = getLocomotiveById(rs.getInt("LOCOMOTIVEID"));
                Train train = new Train(rs.getString("TRAINID"), loco);
                loadWagonsForTrain(train, conn);
                trains.add(train);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trains;
    }

    /**
     * Retrieves a locomotive by its identifier.
     *
     * @param id the locomotive identifier
     * @return the locomotive, or null if not found
     */
    private Locomotive getLocomotiveById(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call fn_get_locomotive_by_id(?) }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setInt(2, id);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            if (rs.next()) {
                return new Locomotive(
                    rs.getInt("ID"), rs.getString("NAME"), rs.getString("MAKE"), rs.getString("MODEL"),
                    rs.getInt("SERVICE"), rs.getInt("NUMBOGIES"), rs.getString("BOGIES"), rs.getInt("POWER"),
                    rs.getDouble("LENGTH"), rs.getDouble("WIDTH"), rs.getDouble("HEIGHT"), rs.getDouble("WEIGHT"),
                    rs.getDouble("MAXSPEED"), rs.getDouble("OPERATIONALSPEED"), rs.getDouble("TRACTION"),
                    rs.getString("TYPE"), rs.getDouble("VOLTAGE"), rs.getDouble("FREQUENCY"),
                    rs.getString("OPERATOR"), rs.getInt("GAUGEID"), rs.getDouble("GAS")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all wagons associated with a given train.
     *
     * @param train the train to populate
     * @param conn an active database connection
     * @throws SQLException if a database error occurs
     */
    private void loadWagonsForTrain(Train train, Connection conn) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{ ? = call fn_get_wagons_by_train(?) }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.setString(2, train.getTrainId());
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                RailwayWagon wagon = new RailwayWagon(
                    rs.getString("WAGONID"), rs.getString("TYPE"), rs.getString("MAKE"), rs.getString("MODEL"),
                    rs.getDouble("LENGTH"), rs.getDouble("WIDTH"), rs.getDouble("HEIGHT"), rs.getDouble("EMPTYWEIGHT"),
                    rs.getDouble("MAXCAPACITY"), rs.getDouble("CURRENTLOAD"), rs.getInt("GAUGEID"), rs.getString("OPERATOR")
                );
                train.addWagon(wagon);
            }
        }
    }

    /**
     * Retrieves all facilities.
     *
     * @return a list of facilities
     */
    public List<Facility> getAllFacilities() {
        List<Facility> facilities = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call fn_get_all_facilities() }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                facilities.add(new Facility(
                    String.valueOf(rs.getInt("FACILITYID")), rs.getString("NAME"),
                    rs.getInt("SIDINGID") > 0, rs.getInt("SIDINGID") > 0 ? 1 : 0
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facilities;
    }

    /**
     * Retrieves all railway line segments.
     *
     * @return a list of segments
     */
    public List<Segment> getAllSegments() {
        List<Segment> segments = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ ? = call fn_get_all_line_segments() }")) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            while (rs.next()) {
                segments.add(new Segment(
                    rs.getInt("LINEID"), rs.getInt("ORDER"), rs.getString("ISELECTRIFIED").equals("Y"),
                    rs.getDouble("MAXWEIGHT"), rs.getDouble("LENGTH"), rs.getInt("NUMBEROFTRACKS"),
                    rs.getString("SIDINGID") != null,
                    String.valueOf(rs.getInt("STARTFACILITYID")),
                    String.valueOf(rs.getInt("ENDFACILITYID")),
                    rs.getString("STARTSTATIONNAME"),
                    rs.getString("ENDSTATIONNAME")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return segments;
    }
}
