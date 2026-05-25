import Database.DataBaseConnection;
import Repositories.*;
import Repositories.LAPR.LineRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

/**
 * Bootstrap routine to preload repositories with initial data.
 */
public class Bootstrap implements Runnable {

    /**
     * Creates a new Bootstrap instance.
     */
    public Bootstrap() {}

    /**
     * Executes the bootstrap process.
     */
    public void run() {
        checkDirectory();
        cleanFiles();
        loadRepositories();
    }

    /**
     * Loads all necessary repositories and datasets.
     */
    private void loadRepositories() {
        ItemRepository.getInstance().loadItems();
        WagonRepository.getInstance().loadWagons();
        WarehouseRepository.getInstance().createWarehouse();
        OrderRepository.getInstance().loadOrders();
        ReturnRepository.getInstance().loadReturns(ItemRepository.getInstance());
        Connection conn = getConnection();
        LineRepository.getInstance().loadFromSQLFile(conn);
        try {
            Repositories.LAPR.SegmentRepository.getInstance().loadFromSQL(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TreeRepository.getInstance().loadStationsAsync();
        StationRepository.getInstance().loadStations();
        StationRepository.getInstance().loadEdges();
        GraphRepository.getInstance().buildGraph(false);
        GraphRepository.getInstance().buildGraph(true);
        GraphRepository.getInstance().buildAsync();
        GraphRepository.getInstance().computeMinimalBackboneNetwork();
    }

    /**
     * Removes previously generated output and log files under the outputFiles directory.
     * This ensures a clean state before the bootstrap routines generate fresh artifacts.
     * Files targeted include parsing/audit logs, station listings, and MBN graph outputs.
     */
    private void cleanFiles() {
        File parsingErrors = new File("outputFiles/parsingErrors.log");
        File auditLog = new File("outputFiles/audit.log");
        File latitudeTxt = new File("outputFiles/StationsByLatitude.txt");
        File longitudeTxt = new File("outputFiles/StationsByLongitude.txt");
        File tzcTxt = new File("outputFiles/StationsByTZC.txt");
        File windowTxt = new File("outputFiles/StationsByWindowQuery.txt");
        File mbnDot = new File("outputFiles/MBN.dot");
        File mbnSvg = new File("outputFiles/MBN.svg");

        if (parsingErrors.exists()) {
            parsingErrors.delete();
        }

        if (auditLog.exists()) {
            auditLog.delete();
        }

        if (latitudeTxt.exists()) {
            latitudeTxt.delete();
        }

        if (longitudeTxt.exists()) {
            longitudeTxt.delete();
        }

        if (tzcTxt.exists()) {
            tzcTxt.delete();
        }

        if (windowTxt.exists()) {
            windowTxt.delete();
        }

        if (mbnDot.exists()) {
            mbnDot.delete();
        }

        if (mbnSvg.exists()) {
            mbnSvg.delete();
        }
    }

    /**
     * Ensures the outputFiles directory exists.
     * If it does not exist, the directory structure is created.
     * Any IOException encountered is caught and an error message is printed to stderr.
     */
    private void checkDirectory() {
        Path dir = Paths.get("outputFiles");

        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            System.err.println("Could not check/create output directory.");
        }
    }

    /**
     * Obtains a database connection using the application's DataBaseConnection helper.
     * Returns null if a connection cannot be established (exception is printed to stderr).
     *
     * @return an active SQL Connection or null if acquisition fails
     */
    private Connection getConnection() {
        try  {
            Connection conn = DataBaseConnection.getConnection();
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
