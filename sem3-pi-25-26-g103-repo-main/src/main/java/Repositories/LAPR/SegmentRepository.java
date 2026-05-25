package Repositories.LAPR;

import Model.LAPR.Segment;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository exposing a predefined collection of track segments.
 */
public class SegmentRepository {
    private List<Segment> segments;
    private static SegmentRepository instance = new SegmentRepository();

    private SegmentRepository() {
        this.segments = new ArrayList<>();
    }

    public void loadFromSQL(Connection conn) throws SQLException {
        segments.clear();
        CallableStatement statement = conn.prepareCall("{? = call fn_get_all_line_segments()}");
        statement.registerOutParameter(1, OracleTypes.CURSOR);
        statement.execute();

        ResultSet rs = (ResultSet) statement.getObject(1);

        while (rs.next()) {
            int lineID = rs.getInt("LINEID");
            int order = rs.getInt("ORDER");
            String isElectrified = rs.getString("ISELECTRIFIED");
            int maxWeight = rs.getInt("MAXWEIGHT");
            int length = rs.getInt("LENGTH");
            int numberOfTracks = rs.getInt("NUMBEROFTRACKS");
            String siding = rs.getString("SIDINGID");

            segments.add(new Segment(lineID, order, stringToBoolean(isElectrified), maxWeight, length, numberOfTracks, stringToBoolean(siding)));
        }
    }

    private boolean stringToBoolean(String str) {
        if (str != null) return str.equals("Y");
        return false;
    }
    /** Returns the SegmentRepository singleton. */
    public static SegmentRepository getInstance() {
        return instance;
    }

    /** Returns all available segments. */
    public List<Segment> getSegments() {
        return segments;
    }
}