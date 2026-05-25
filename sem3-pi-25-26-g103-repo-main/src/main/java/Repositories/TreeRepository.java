package Repositories;

import Model.Wrappers.CountryIndex;
import Model.Wrappers.RailwayStationLatitude;
import Model.Wrappers.RailwayStationLongitude;
import Model.Wrappers.TimeZoneIndex;
import Model.Country;
import Model.RailwayStation;
import Model.ReadFromCSV;
import Model.TimeZoneGroup;
import Model.Trees.AVLTree;
import Model.Trees.KDtree;
import Model.Trees.BST;

import java.util.*;

/**
 * Repository that stores multiple tree-based indexes over railway stations.
 * <p>
 * Provides access to AVL trees indexed by latitude, longitude and time zone,
 * as well as to a KD-tree used for proximity searches. It also exposes
 * utilities to load stations from CSV and to run range/window queries.
 */
public class  TreeRepository {
    private static TreeRepository instance = new TreeRepository();
    private AVLTree<RailwayStationLatitude> latitudeTree;
    private AVLTree<RailwayStationLongitude> longitudeTree;
    private AVLTree<TimeZoneIndex> timeZoneCountryTree;
    private KDtree kdtree;
    private volatile boolean loading = false;

    private TreeRepository() {
        latitudeTree = new AVLTree<>();
        longitudeTree = new AVLTree<>();
        timeZoneCountryTree = new AVLTree<>();
        kdtree = new KDtree();
    }

    /**
         * Returns the singleton instance of the TreeRepository.
         *
         * @return the TreeRepository instance
         */
    public static TreeRepository getInstance() {
        return instance;
    }

    /**
         * Returns the AVL tree indexed by latitude containing buckets of stations.
         *
         * @return AVLTree keyed by latitude
         */
    public AVLTree<RailwayStationLatitude> getLatitudeTree() {
        return latitudeTree;
    }

    /**
         * Returns the AVL tree indexed by longitude containing buckets of stations.
         *
         * @return AVLTree keyed by longitude
         */
        public AVLTree<RailwayStationLongitude> getLongitudeTree() {
        return longitudeTree;
    }

    /**
         * Returns the KD-tree used for nearest-neighbor proximity queries.
         *
         * @return KDtree built over station coordinates
         */
        public KDtree getKdtree(){
        return kdtree;
    }

    /**
     * Indicates whether an asynchronous station load is currently running.
     *
     * @return true if the repository is loading data; false otherwise
     */
    public boolean isLoading() {
        return loading;
    }

    private void setLoading(boolean value) {
        loading = value;
    }

    /**
     * Loads all stations from the CSV file into the repository.
     *
     * @throws IllegalArgumentException if any station record is invalid
     */
    public void loadStations() {
        ReadFromCSV.readFile("train_stations_europe", fields -> {
            if (fields.length != 9) {
                throw new IllegalArgumentException("Invalid station record: expected 9 columns but got " + fields.length);
            }

            String name = fields[3];
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Missing name for station.");
            }

            Country country;
            try {
                country = Country.valueOf(fields[0]);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid or missing country for station " + name);
            }

            TimeZoneGroup timeZoneGroup;
            try {
                String tzGroupValue = fields[2].replace("/", "_");
                timeZoneGroup = TimeZoneGroup.valueOf(tzGroupValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid or missing timezone group for station " + name);
            }

            if (fields[4].isBlank()) {
                throw new IllegalArgumentException("Missing latitude for station " + name);
            }
            double latitude;
            try {
                latitude = Double.parseDouble(fields[4]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid latitude format for station " + name + ": " + fields[4]);
            }
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Invalid latitude for station " + name + ": must be between -90 and 90.");
            }

            if (fields[5].isBlank()) {
                throw new IllegalArgumentException("Missing longitude for station " + name);
            }
            double longitude;
            try {
                longitude = Double.parseDouble(fields[5]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid longitude format for station " + name + ": " + fields[5]);
            }
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Invalid longitude for station " + name + ": must be between -180 and 180.");
            }

            String timeZone = fields[1];
            boolean isCity = Boolean.parseBoolean(fields[6]);
            boolean isMainStation = Boolean.parseBoolean(fields[7]);
            boolean isAirport = Boolean.parseBoolean(fields[8]);

            RailwayStation s = new RailwayStation(name, latitude, longitude, country, timeZone, timeZoneGroup, isCity, isMainStation, isAirport);
            addStation(s);

            return s;
        });
    }

    /**
     * Loads stations asynchronously in a separate thread.
     */
    public void loadStationsAsync() {
        new Thread(() -> {
            setLoading(true);
            try {
                loadStations();
            } finally {
                setLoading(false);
            }
        }).start();
    }

    /**
     * Adds a station to all relevant data structures (latitude, longitude, time zone).
     *
     * @param station the station to add
     */
    public void addStation(RailwayStation station) {
        double lat = station.getLatitude();
        RailwayStationLatitude
                latWrapper = new RailwayStationLatitude(lat);
        var latNode = latitudeTree.find(latitudeTree.root(), latWrapper);

        if (latNode == null) {
            latWrapper.addStation(station);
            latitudeTree.insert(latWrapper);
        } else {
            latNode.getElement().addStation(station);
        }

        double lon = station.getLongitude();
        RailwayStationLongitude lonWrapper = new RailwayStationLongitude(lon);
        var lonNode = longitudeTree.find(longitudeTree.root(), lonWrapper);

        if (lonNode == null) {
            lonWrapper.addStation(station);
            longitudeTree.insert(lonWrapper);
        } else {
            lonNode.getElement().addStation(station);
        }

        TimeZoneIndex tzIndex = new TimeZoneIndex(station.getTimeZoneGroup());
        var tzNode = timeZoneCountryTree.find(timeZoneCountryTree.root(), tzIndex);

        TimeZoneIndex actual;
        if (tzNode == null) {
            timeZoneCountryTree.insert(tzIndex);
            actual = tzIndex;
        } else {
            actual = tzNode.getElement();
        }

        actual.addStation(station);
    }

    /**
     * Searches stations within a latitude range.
     *
     * @param min minimum latitude
     * @param max maximum latitude
     * @return list of latitude nodes containing stations
     */
    public List<RailwayStationLatitude> searchByLatitudeRange(double min, double max) {
        List<RailwayStationLatitude> result = new ArrayList<>();

        latitudeRangeQuery(latitudeTree.root(), min, max, result);

        return result;
    }

    private void latitudeRangeQuery(BST.Node<RailwayStationLatitude> node, double min, double max, List<RailwayStationLatitude> result) {
        if (node == null)
            return;

        double lat = node.getElement().getLatitude();

        if (lat >= min && lat <= max) {
            result.add(node.getElement());
        }

        if (lat >= min) {
            latitudeRangeQuery(node.getLeft(), min, max, result);
        }

        if (lat <= max) {
            latitudeRangeQuery(node.getRight(), min, max, result);
        }
    }

    /**
     * Searches stations within a longitude range.
     *
     * @param min minimum longitude
     * @param max maximum longitude
     * @return list of longitude nodes containing stations
     */
    public List<RailwayStationLongitude> searchByLongitudeRange(double min, double max) {
        List<RailwayStationLongitude> result = new ArrayList<>();

        longitudeRangeQuery(longitudeTree.root(), min, max, result);

        return result;
    }

    private void longitudeRangeQuery(BST.Node<RailwayStationLongitude> node, double min, double max, List<RailwayStationLongitude> result) {
        if (node == null)
            return;

        double lon = node.getElement().getLongitude();

        if (lon >= min && lon <= max) {
            result.add(node.getElement());
        }

        if (lon >= min) {
            longitudeRangeQuery(node.getLeft(), min, max, result);
        }

        if (lon <= max) {
            longitudeRangeQuery(node.getRight(), min, max, result);
        }
    }

    /**
     * Filters countries available for a given Time Zone Group.
     *
     * @param tzGroup the time zone group
     * @return list of countries in that group
     */
    public List<Country> filterCountries(TimeZoneGroup tzGroup) {
        TimeZoneIndex key = new TimeZoneIndex(tzGroup);
        TimeZoneIndex ti = timeZoneCountryTree.search(key);

        if (ti == null)
            return Collections.emptyList();

        Set<Country> countries = new TreeSet<>();
        for (CountryIndex ci : ti.getCountries().inOrder()) {
            countries.add(ci.getCountry());
        }

        return new ArrayList<>(countries);
    }

    /**
     * Searches stations by Time Zone Group and Country.
     *
     * @param tzGroup the time zone group
     * @param country the country
     * @return list of stations matching the criteria
     */
    public List<RailwayStation> searchByTimeZoneAndCountry(TimeZoneGroup tzGroup, Country country) {
        List<RailwayStation> result = new ArrayList<>();
        AVLTree.Node<TimeZoneIndex> tzNode = timeZoneCountryTree.find(timeZoneCountryTree.root(), new TimeZoneIndex(tzGroup));

        if (tzNode != null) {
            TimeZoneIndex tzIndex = tzNode.getElement();
            AVLTree.Node<CountryIndex> countryNode = tzIndex.getCountries().find(tzIndex.getCountries().root(), new CountryIndex(country));
            if (countryNode != null) {
                result.addAll(countryNode.getElement().getStationsInOrder());
            }
        }

        return result;
    }

    /**
     * Retrieves stations within a window of Time Zone Groups.
     *
     * @param lower lower bound Time Zone Group
     * @param upper upper bound Time Zone Group
     * @return list of stations within the window
     */
    public List<RailwayStation> getStationsByTimeZoneWindow(TimeZoneGroup lower, TimeZoneGroup upper) {
        List<RailwayStation> result = new ArrayList<>();
        windowQuery(timeZoneCountryTree.root(), lower, upper, result);
        return result;
    }

    private void windowQuery(AVLTree.Node<TimeZoneIndex> node, TimeZoneGroup lower, TimeZoneGroup upper, List<RailwayStation> result) {
        if (node == null || node.getElement() == null || node.getElement().getTimeZoneGroup() == null) return;

        TimeZoneGroup tz = node.getElement().getTimeZoneGroup();

        if (tz.compareTo(lower) > 0) {
            windowQuery(node.getLeft(), lower, upper, result);
        }

        if (tz.compareTo(lower) >= 0 && tz.compareTo(upper) <= 0) {
            Iterable<CountryIndex> countries = node.getElement().getCountries().inOrder();

            for (CountryIndex ci : countries) {
                result.addAll(ci.getStationsInOrder());
            }
        }

        if (tz.compareTo(upper) < 0) {
            windowQuery(node.getRight(), lower, upper, result);
        }
    }
}
