package UI.Utils;

import Repositories.RangeSearchRepository;
import Model.Wrappers.RailwayStationLatitude;
import Model.Wrappers.RailwayStationLongitude;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Utility class for printing results to files in the outputFiles directory.
 */
public class FilePrinterStations {
    private static final String latitudePath = "outputFiles/StationsByLatitude.txt";
    private static final String longitudePath = "outputFiles/StationsByLongitude.txt";
    private static final String tzcPath = "outputFiles/StationsByTZC.txt";
    private static final String windowPath = "outputFiles/StationsByWindowQuery.txt";
    private static final String allRangeSearchPath = "outputFiles/AllRangeSearchPath.txt";

    /**
     * Prints the results of a latitude range search to a file.
     *
     * @param min minimum latitude
     * @param max maximum latitude
     * @param result list of stations found in the range
     */
    public static void printLat(double min, double max, List<RailwayStationLatitude> result) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(latitudePath, StandardCharsets.UTF_8))) {
            bw.write("--- STATIONS WITH LATITUDE BETWEEN " + min + " AND " + max + " --------------------------");
            bw.newLine();

            for (RailwayStationLatitude st : result) {
                for (RailwayStation rs : st.getStations()) {
                    bw.write("Station " + rs.getName() + " (" + rs.getLatitude() + ", " + rs.getLongitude() + ")");
                    bw.newLine();
                }
            }

            bw.newLine();

            System.out.println("All the stations found were reunited in the file: " + latitudePath);
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error creating/writing to file: " + e.getMessage());
        }
    }

    /**
     * Prints the results of a longitude range search to a file.
     *
     * @param min minimum longitude
     * @param max maximum longitude
     * @param result list of stations found in the range
     */
    public static void printLon(double min, double max, List<RailwayStationLongitude> result) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(longitudePath, StandardCharsets.UTF_8))) {
            bw.write("--- STATIONS WITH LONGITUDE BETWEEN " + min + " AND " + max + " --------------------------");
            bw.newLine();

            for (RailwayStationLongitude st : result) {
                for (RailwayStation rs : st.getStations()) {
                    bw.write("Station " + rs.getName() + " (" + rs.getLatitude() + ", " + rs.getLongitude() + ")");
                    bw.newLine();
                }
            }

            bw.newLine();

            System.out.println("All the stations found were reunited in the file: " + longitudePath);
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error creating/writing to file: " + e.getMessage());
        }
    }

    /**
     * Prints stations filtered by Time Zone Group and Country to a file.
     *
     * @param tzGroup the time zone group
     * @param country the country
     * @param result list of stations matching the criteria
     */
    public static void printTZC(TimeZoneGroup tzGroup, Country country, List<RailwayStation> result) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tzcPath, StandardCharsets.UTF_8))) {
            bw.write("--- STATIONS WITH TIME ZONE GROUP " + tzGroup.name() + " AND COUNTRY " + country.name() + " --------------------------");
            bw.newLine();

            for (RailwayStation rs : result) {
                bw.write("Station " + rs.getName() + " (" + rs.getTimeZoneGroup() + ", " + rs.getCountry().getName() + ")");
                bw.newLine();
            }

            bw.newLine();

            System.out.println("All the stations found were reunited in the file: " + tzcPath);
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error creating/writing to file: " + e.getMessage());
        }
    }

    /**
     * Prints stations found in a window query between two Time Zone Groups.
     *
     * @param lower lower bound Time Zone Group
     * @param upper upper bound Time Zone Group
     * @param result list of stations within the window
     */
    public static void printWindow(TimeZoneGroup lower, TimeZoneGroup upper, List<RailwayStation> result) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(windowPath, StandardCharsets.UTF_8))) {
            bw.write("--- STATIONS IN THE TIME ZONE GROUP WINDOW QUERY [" + lower.getName() + ", " + upper.getName() + "] --------------------------");
            bw.newLine();

            for (RailwayStation rs : result) {
                bw.write("Station " + rs.getName() + " (" + rs.getTimeZoneGroup().getName() + ")");
                bw.newLine();
            }

            bw.newLine();

            System.out.println("All the stations found were reunited in the file: " + windowPath);
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error creating/writing to file: " + e.getMessage());
        }
    }

    /**
     * Prints all range search results from the repository to a file.
     *
     * @param rangeSearchRepository repository containing range search results
     */
    public static void printAllRangeSearch(RangeSearchRepository rangeSearchRepository) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(allRangeSearchPath, StandardCharsets.UTF_8))) {
            bw.write("--- ALL RANGE SEARCH RESULTS --------------------------");
            bw.newLine();

            rangeSearchRepository.getRangeSearchMap().forEach((range, filterRangeSearch) -> {
                try {

                    bw.write("Range Search: " + range.toString());
                    bw.newLine();
                    filterRangeSearch.forEach((filters, rangeSearch) -> {
                        try {
                            bw.write("Filters: City=" + filters.getCity() + ", MainStation=" + filters.getMainStation() + ", Airport=" + filters.getAirport() + ", Countries=" + filters.getCountries());
                            bw.newLine();
                            bw.write("Result Stations:");
                            bw.newLine();
                        }catch (IOException e){
                            System.err.println("Error writing filters to file: " + e.getMessage());
                        }
                        rangeSearch.getResultStations().forEach((name, station) -> {
                            try {
                                bw.write("Station " + station.getName() + " (" + station.getLatitude() + ", " + station.getLongitude() + ")");
                                bw.newLine();
                            } catch (IOException e) {
                                System.err.println("Error writing station to file: " + e.getMessage());
                            }
                        });
                    });
                    bw.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing filters to file: " + e.getMessage());
                }
            });

            System.out.println("All range search results were reunited in the file: " + allRangeSearchPath);
            System.out.println();
        } catch (IOException e) {
            System.err.println("Error creating/writing to file: " + e.getMessage());
        }
    }
}
