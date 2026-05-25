package Repositories.LAPR;

import Database.DataBaseConnection;
import Model.LAPR.*;
import data.TemplateData.ConnectionFactory; // Ajusta o import para o teu projeto
import data.TemplateData.DatabaseConnection; // Ajusta o import para o teu projeto

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes; // Precisas do ojdbc11.jar nas bibliotecas

/**
 * Oracle-based implementation of {@link FreightRepository}.
 *
 * <p>This repository accesses freight and facility data using
 * stored functions defined in an Oracle database.</p>
 */
public class FreightRepositoryOracle implements FreightRepository {

    /**
     * Gets a database connection.
     *
     * @return an active SQL connection
     * @throws SQLException if the connection cannot be obtained
     */
    private Connection getConnection() throws SQLException {
        try {
           Connection con = DataBaseConnection.getConnection();
           return con;
        } catch (Exception e) {
            throw new SQLException("Error getting connection from Factory: " + e.getMessage());
        }
    }

    @Override
    public List<Facility> getAllFacilities() {
        List<Facility> list = new ArrayList<>();
        String sql = "{ ? = call fn_get_all_facilities() }";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("FACILITYID"));
                    String name = rs.getString("NAME");
                    int siding = rs.getInt("SIDINGID");
                    boolean hasSiding = siding > 0;
                    int numSiding = hasSiding ? 1 : 0;
                    list.add(new Facility(id, name, hasSiding, numSiding));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database Error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Freight> getPendingFreights() {
        List<Freight> list = new ArrayList<>();
        String sql = "{ ? = call fn_get_all_pending_freights() }";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    int id = rs.getInt("ID");

                    String originId = String.valueOf(rs.getInt("ORIGIN"));
                    String destId = String.valueOf(rs.getInt("DESTINATION"));
                    String originName = rs.getString("ORIGIN_NAME");
                    String destName = rs.getString("DEST_NAME");
                    int originSiding = rs.getInt("ORIGIN_SIDING");
                    int destSiding = rs.getInt("DEST_SIDING");
                    boolean originHasSiding = originSiding > 0;
                    boolean destHasSiding = destSiding > 0;

                    int originNumSiding = originHasSiding ? 1 : 0;
                    int destNumSiding = destHasSiding ? 1 : 0;
                    Facility origin = new Facility(originId, originName,originHasSiding , originNumSiding);
                    Facility dest = new Facility(destId, destName, destHasSiding, destNumSiding);
                    list.add(new Freight(id, "Carga " + id, 100.0, origin, dest));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}