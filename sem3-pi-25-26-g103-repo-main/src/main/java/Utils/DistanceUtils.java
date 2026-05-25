package Utils;

/**
 * Utility class for distance calculations.
 * Implements the Haversine formula for calculating distances between geographical coordinates.
 */
public class DistanceUtils {

    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Calculates the Haversine distance between two points on Earth.
     *
     * @param lat1 Latitude of the first point (in degrees)
     * @param lon1 Longitude of the first point (in degrees)
     * @param lat2 Latitude of the second point (in degrees)
     * @param lon2 Longitude of the second point (in degrees)
     * @return Distance in kilometers
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the squared distance (for optimization purposes, avoiding sqrt).
     * Used for distance comparisons without needing the actual distance value.
     */
    public static double distanceSquared(double lat1, double lon1, double lat2, double lon2) {
        double latDiff = lat2 - lat1;
        double lonDiff = lon2 - lon1;
        return latDiff * latDiff + lonDiff * lonDiff;
    }
}

