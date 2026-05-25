package Repositories.LAPR;

import Model.LAPR.Line;
import Model.LAPR.Segment; // Usamos a tua classe
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineRepository {

    private List<Line> lines = new ArrayList<>();
    private List<Segment> allPhysicalSegments = new ArrayList<>();

    private static LineRepository instance = new LineRepository();

    public static LineRepository getInstance() {
        return instance;
    }

    public void loadFromSQLFile(Connection conn) {
        lines.clear();
        try {
            String sql = "SELECT * FROM LINE";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lines.add(new Line(
                        rs.getInt("LINEID"),
                        rs.getString("NAME"),
                        rs.getInt("STARTFACILITYID"),
                        rs.getInt("ENDFACILITYID"),
                        rs.getInt("GAUGEID"),
                        rs.getString("START"),
                        rs.getString("END"),
                        rs.getString("OWNERMANAGINGVATNUMBER")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Encontra a Linha que liga duas estações (bidirecional).
     */
    public Line findLineConnecting(int fac1, int fac2) {
        for (Line l : lines) {
            if ((l.getStartId() == fac1 && l.getEndId() == fac2) ||
                    (l.getStartId() == fac2 && l.getEndId() == fac1)) {
                return l;
            }
        }
        return null;
    }

    /**
     * Devolve os segmentos físicos de uma linha específica.
     */
    public List<Segment> getSegmentsByLineId(int lineId) {
        return SegmentRepository.getInstance().getSegments().stream()
                .filter(s -> s.getLineID() == lineId)
                .collect(Collectors.toList());
    }

    public List<Line> getLines() {

        return lines;

    }

    public Line getLineById(int id){

        for (Line line : lines) {

            if(line.getLineId() == id){

                return line;

            }

        }

        return null;

    }
}