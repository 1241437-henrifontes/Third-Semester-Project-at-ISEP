package USEI09;

import org.junit.jupiter.api.Test;
import Utils.DistanceUtils;

import static org.junit.jupiter.api.Assertions.*;

public class DistanceUtilsTest {

    @Test
    void testHaversineDistanceSamePoint() {
        double distance = DistanceUtils.haversineDistance(38.7223, -9.1393, 38.7223, -9.1393);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void testHaversineDistanceLisbonToPorto() {
        double lat1 = 38.7223;
        double lon1 = -9.1393;
        double lat2 = 41.1496;
        double lon2 = -8.6109;

        double distance = DistanceUtils.haversineDistance(lat1, lon1, lat2, lon2);

        assertTrue(distance > 270.0 && distance < 280.0);
    }

    @Test
    void testHaversineDistanceLisbonToMadrid() {
        double lat1 = 38.7223;
        double lon1 = -9.1393;
        double lat2 = 40.4168;
        double lon2 = -3.7038;

        double distance = DistanceUtils.haversineDistance(lat1, lon1, lat2, lon2);

        assertTrue(distance > 495.0 && distance < 510.0);
    }

    @Test
    void testHaversineDistanceParisToLondon() {
        double lat1 = 48.8566;
        double lon1 = 2.3522;
        double lat2 = 51.5074;
        double lon2 = -0.1278;

        double distance = DistanceUtils.haversineDistance(lat1, lon1, lat2, lon2);

        assertTrue(distance > 340.0 && distance < 350.0);
    }

    @Test
    void testHaversineDistanceSymmetric() {
        double lat1 = 38.7223;
        double lon1 = -9.1393;
        double lat2 = 41.1496;
        double lon2 = -8.6109;

        double distance1 = DistanceUtils.haversineDistance(lat1, lon1, lat2, lon2);
        double distance2 = DistanceUtils.haversineDistance(lat2, lon2, lat1, lon1);

        assertEquals(distance1, distance2, 0.001);
    }

    @Test
    void testHaversineDistanceWithZeroCoordinates() {
        double distance = DistanceUtils.haversineDistance(0.0, 0.0, 0.0, 0.0);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void testHaversineDistanceEquator() {
        double distance = DistanceUtils.haversineDistance(0.0, 0.0, 0.0, 1.0);
        assertTrue(distance > 110.0 && distance < 112.0);
    }

    @Test
    void testHaversineDistanceNorthPole() {
        double distance = DistanceUtils.haversineDistance(89.0, 0.0, 89.0, 180.0);
        assertTrue(distance < 250.0);
    }

    @Test
    void testHaversineDistanceAcrossPrimeMeridian() {
        double distance = DistanceUtils.haversineDistance(51.5, -0.5, 51.5, 0.5);
        assertTrue(distance > 0.0 && distance < 100.0);
    }

    @Test
    void testHaversineDistanceAcrossDateLine() {
        double distance = DistanceUtils.haversineDistance(0.0, 179.5, 0.0, -179.5);
        assertTrue(distance > 0.0 && distance < 150.0);
    }

    @Test
    void testHaversineDistanceNegativeCoordinates() {
        double distance = DistanceUtils.haversineDistance(-33.8688, 151.2093, -37.8136, 144.9631);
        assertTrue(distance > 700.0 && distance < 730.0);
    }

    @Test
    void testHaversineDistanceVerySmallDistance() {
        double lat1 = 38.7223;
        double lon1 = -9.1393;
        double lat2 = 38.7224;
        double lon2 = -9.1394;

        double distance = DistanceUtils.haversineDistance(lat1, lon1, lat2, lon2);
        assertTrue(distance < 0.2);
    }

    @Test
    void testHaversineDistanceAlwaysPositive() {
        double distance1 = DistanceUtils.haversineDistance(10.0, 20.0, 30.0, 40.0);
        double distance2 = DistanceUtils.haversineDistance(30.0, 40.0, 10.0, 20.0);

        assertTrue(distance1 >= 0.0);
        assertTrue(distance2 >= 0.0);
        assertEquals(distance1, distance2, 0.001);
    }

    @Test
    void testDistanceSquaredSamePoint() {
        double distSq = DistanceUtils.distanceSquared(38.7223, -9.1393, 38.7223, -9.1393);
        assertEquals(0.0, distSq, 0.0001);
    }

    @Test
    void testDistanceSquaredDifferentPoints() {
        double distSq = DistanceUtils.distanceSquared(0.0, 0.0, 1.0, 1.0);
        assertEquals(2.0, distSq, 0.0001);
    }

    @Test
    void testDistanceSquaredSymmetric() {
        double distSq1 = DistanceUtils.distanceSquared(10.0, 20.0, 30.0, 40.0);
        double distSq2 = DistanceUtils.distanceSquared(30.0, 40.0, 10.0, 20.0);

        assertEquals(distSq1, distSq2, 0.0001);
    }

    @Test
    void testDistanceSquaredAlwaysPositive() {
        double distSq = DistanceUtils.distanceSquared(-10.0, -20.0, 10.0, 20.0);
        assertTrue(distSq >= 0.0);
    }

    @Test
    void testDistanceSquaredVsHaversine() {
        double distSq = DistanceUtils.distanceSquared(38.0, -9.0, 39.0, -8.0);
        double haversine = DistanceUtils.haversineDistance(38.0, -9.0, 39.0, -8.0);

        assertNotEquals(distSq, haversine, 0.1);
    }

    @Test
    void testDistanceSquaredWithNegativeCoordinates() {
        double distSq = DistanceUtils.distanceSquared(-38.7, -9.1, -41.1, -8.6);
        assertTrue(distSq > 0.0);
    }

    @Test
    void testDistanceSquaredFormula() {
        double lat1 = 0.0;
        double lon1 = 0.0;
        double lat2 = 3.0;
        double lon2 = 4.0;

        double distSq = DistanceUtils.distanceSquared(lat1, lon1, lat2, lon2);
        assertEquals(25.0, distSq, 0.0001);
    }

    @Test
    void testDistanceSquaredSmallDifferences() {
        double distSq = DistanceUtils.distanceSquared(38.7223, -9.1393, 38.7224, -9.1394);
        assertTrue(distSq > 0.0);
        assertTrue(distSq < 0.001);
    }

    @Test
    void testHaversineDistanceAccuracy() {
        double distance = DistanceUtils.haversineDistance(40.7128, -74.0060, 34.0522, -118.2437);
        assertTrue(distance > 3900.0 && distance < 4000.0);
    }

    @Test
    void testHaversineDistanceWithLargeLatitudeDifference() {
        double distance = DistanceUtils.haversineDistance(-50.0, 0.0, 50.0, 0.0);
        assertTrue(distance > 10000.0);
    }

    @Test
    void testHaversineDistanceWithLargeLongitudeDifference() {
        double distance = DistanceUtils.haversineDistance(0.0, -170.0, 0.0, 170.0);
        assertTrue(distance > 2000.0);
    }
}
