package USEI08;

import Model.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Range Unit Tests")
class RangeTest {
    private Range validRange;
    private Range invalidRange;

    @BeforeEach
    void setUp() {
        validRange = new Range(40.0, 30.0, 10.0, 0.0);
        invalidRange = new Range(30.0, 40.0, 0.0, 10.0);
    }

    @Test
    @DisplayName("Should create Range with valid coordinates")
    void testRangeCreationWithValidCoordinates() {
        assertNotNull(validRange);
        assertEquals(40.0, validRange.getMaxLat());
        assertEquals(30.0, validRange.getMinLat());
        assertEquals(10.0, validRange.getMaxLon());
        assertEquals(0.0, validRange.getMinLon());
    }

    @Test
    @DisplayName("Should return correct max latitude")
    void testGetMaxLat() {
        assertEquals(40.0, validRange.getMaxLat());
    }

    @Test
    @DisplayName("Should return correct min latitude")
    void testGetMinLat() {
        assertEquals(30.0, validRange.getMinLat());
    }

    @Test
    @DisplayName("Should return correct max longitude")
    void testGetMaxLon() {
        assertEquals(10.0, validRange.getMaxLon());
    }

    @Test
    @DisplayName("Should return correct min longitude")
    void testGetMinLon() {
        assertEquals(0.0, validRange.getMinLon());
    }

    @Test
    @DisplayName("Should return true when point is inside range")
    void testContainsPointInsideRange() {
        assertTrue(validRange.contains(35.0, 5.0));
    }

    @Test
    @DisplayName("Should return true when point is on range boundary")
    void testContainsPointOnBoundary() {
        assertTrue(validRange.contains(40.0, 10.0));
        assertTrue(validRange.contains(30.0, 0.0));
        assertTrue(validRange.contains(35.0, 5.0));
    }

    @Test
    @DisplayName("Should return false when point is outside range")
    void testContainsPointOutsideRange() {
        assertFalse(validRange.contains(50.0, 5.0));
        assertFalse(validRange.contains(25.0, 5.0));
        assertFalse(validRange.contains(35.0, 15.0));
        assertFalse(validRange.contains(35.0, -5.0));
    }

    @Test
    @DisplayName("Should return true for valid range")
    void testValidRangeIsValid() {
        assertTrue(validRange.valid());
    }

    @Test
    @DisplayName("Should return false for invalid range with inverted latitudes")
    void testInvalidRangeWithInvertedLatitudes() {
        assertFalse(invalidRange.valid());
    }

    @Test
    @DisplayName("Should return false for invalid range with equal latitudes")
    void testInvalidRangeWithEqualLatitudes() {
        Range equalLatRange = new Range(30.0, 30.0, 10.0, 0.0);
        assertFalse(equalLatRange.valid());
    }

    @Test
    @DisplayName("Should return false for invalid range with equal longitudes")
    void testInvalidRangeWithEqualLongitudes() {
        Range equalLonRange = new Range(40.0, 30.0, 5.0, 5.0);
        assertFalse(equalLonRange.valid());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        String expected = "[maxLat: 40.0, minLat: 30.0, maxLon: 10.0, minLon: 0.0]";
        assertEquals(expected, validRange.toString());
    }

    @Test
    @DisplayName("Should handle negative coordinates")
    void testNegativeCoordinates() {
        Range negativeRange = new Range(10.0, -10.0, 10.0, -10.0);
        assertTrue(negativeRange.valid());
        assertTrue(negativeRange.contains(0.0, 0.0));
        assertFalse(negativeRange.contains(15.0, 0.0));
    }

    @Test
    @DisplayName("Should handle zero coordinates")
    void testZeroCoordinates() {
        Range zeroRange = new Range(1.0, 0.0, 1.0, 0.0);
        assertTrue(zeroRange.valid());
        assertTrue(zeroRange.contains(0.0, 0.0));
        assertTrue(zeroRange.contains(1.0, 1.0));
    }
}