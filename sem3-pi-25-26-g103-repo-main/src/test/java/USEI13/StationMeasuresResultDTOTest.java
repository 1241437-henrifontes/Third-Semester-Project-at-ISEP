package USEI13;

import Model.Graph.Node;
import Model.Pair;
import Services.DTO.StationMeasuresResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StationMeasuresResultDTO class.
 *
 * These tests validate correct object construction, getter and setter
 * functionality, and the string representation of the DTO.
 *
 * The DTO encapsulates multiple centrality and importance measures
 * associated with a railway station.
 */
@DisplayName("StationMeasuresResultDTO Unit Tests")
class StationMeasuresResultDTOTest {

    private Node station;
    private StationMeasuresResultDTO dto;

    @BeforeEach
    void setUp() {
        station = new Node("1", "Test Station", new Pair<>(1.0, 1.0), new Pair<>(10.0, 10.0));
        dto = new StationMeasuresResultDTO(station, 0.5, 0.6, 0.7, 0.8, 0.9);
    }

    @Test
    @DisplayName("Should create DTO with correct values")
    void testConstructorAndGetters() {
        assertEquals(station, dto.getStation());
        assertEquals(0.5, dto.getBetweenness());
        assertEquals(0.6, dto.getHarmonicCloseness());
        assertEquals(0.7, dto.getDegree());
        assertEquals(0.8, dto.getStrength());
        assertEquals(0.9, dto.getHubScore());
    }

    @Test
    @DisplayName("Should update station")
    void testSetStation() {
        Node newStation = new Node("2", "Other Station", new Pair<>(2.0, 2.0), new Pair<>(20.0, 20.0));
        dto.setStation(newStation);
        assertEquals(newStation, dto.getStation());
    }

    @Test
    @DisplayName("Should update betweenness")
    void testSetBetweenness() {
        dto.setBetweenness(0.1);
        assertEquals(0.1, dto.getBetweenness());
    }

    @Test
    @DisplayName("Should update harmonic closeness")
    void testSetHarmonicCloseness() {
        dto.setHarmonicCloseness(0.2);
        assertEquals(0.2, dto.getHarmonicCloseness());
    }

    @Test
    @DisplayName("Should update degree")
    void testSetDegree() {
        dto.setDegree(0.3);
        assertEquals(0.3, dto.getDegree());
    }

    @Test
    @DisplayName("Should update strength")
    void testSetStrength() {
        dto.setStrength(0.4);
        assertEquals(0.4, dto.getStrength());
    }

    @Test
    @DisplayName("Should update hub score")
    void testSetHubScore() {
        dto.setHubScore(1.0);
        assertEquals(1.0, dto.getHubScore());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        String expected = "StationMeasuresResultDTO{station=Test Station, betweenness=0.5, harmonicCloseness=0.6, degree=0.7, strength=0.8, hubScore=0.9}";
        assertEquals(expected, dto.toString());
    }
}
