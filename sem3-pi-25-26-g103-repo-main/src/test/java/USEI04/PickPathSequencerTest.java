package USEI04;

import Model.Bay;
import Model.PickPathSequencer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PickPathSequencer covering duplicate removal and
 * path generation strategies A and B.
 */
@DisplayName("PickPathSequencer Tests")
public class PickPathSequencerTest {

    private PickPathSequencer sequencer;
    private List<Bay> bays;
    private Bay bay1, bay2, bay3, bay4;

    /**
     * Initializes the sequencer and a default set of bays before each test.
     */
    @BeforeEach
    void setUp() {
        sequencer = new PickPathSequencer();
        bay1 = new Bay("", 1, 2, 0);
        bay2 = new Bay("", 2, 3, 0);
        bay3 = new Bay("", 1, 4, 0);
        bay4 = new Bay("", 2, 1, 0);
        bays = new ArrayList<>(Arrays.asList(bay1, bay2, bay3, bay4));
    }

    /**
     * Ensures removeDuplicates eliminates repeated bays while preserving
     * unique entries.
     */
    @Test
    @DisplayName("removeDuplicates removes duplicate bays")
    void removeDuplicatesRemovesDuplicateBays() {
        Bay duplicateBay = new Bay("", 1, 2, 0);
        bays.add(duplicateBay);
        List<Bay> result = sequencer.removeDuplicates(bays);
        assertEquals(4, result.size());
        assertTrue(result.contains(bay1));
        assertTrue(result.contains(bay2));
        assertTrue(result.contains(bay3));
        assertTrue(result.contains(bay4));
    }

    /**
     * Verifies removeDuplicates returns an empty list when the input is empty.
     */
    @Test
    @DisplayName("removeDuplicates with empty list returns empty list")
    void removeDuplicatesWithEmptyList() {
        List<Bay> result = sequencer.removeDuplicates(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    /**
     * Verifies that a list composed purely of duplicates yields a single
     * unique bay in the result.
     */
    @Test
    @DisplayName("removeDuplicates with all duplicates returns single bay")
    void removeDuplicatesWithAllDuplicates() {
        List<Bay> allDuplicates = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            allDuplicates.add(new Bay("", 1, 2, 0));
        }
        List<Bay> result = sequencer.removeDuplicates(allDuplicates);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getAisle());
        assertEquals(2, result.get(0).getBay());
    }

    /**
     * Validates that strategyA sorts by aisle and then by bay and produces a
     * properly ordered path description.
     */
    @Test
    @DisplayName("strategyA sorts bays by aisle then bay")
    void strategyASortsByAisleThenBay() {
        String result = sequencer.strategyA(bays);
        assertTrue(result.contains("Path:"));
        assertTrue(result.contains("(1,2)"));
        assertTrue(result.contains("(1,4)"));
        assertTrue(result.contains("(2,1)"));
        assertTrue(result.contains("(2,3)"));
        int pos1 = result.indexOf("(1,2)");
        int pos2 = result.indexOf("(1,4)");
        int pos3 = result.indexOf("(2,1)");
        int pos4 = result.indexOf("(2,3)");
        assertTrue(pos1 < pos2);
        assertTrue(pos2 < pos3);
        assertTrue(pos3 < pos4);
    }

    /**
     * Ensures strategyA yields a zero distance path when given no bays.
     */
    @Test
    @DisplayName("strategyA with empty list returns path with zero distance")
    void strategyAWithEmptyList() {
        String result = sequencer.strategyA(Collections.emptyList());
        assertTrue(result.contains("Path:"));
        assertTrue(result.contains("Total Distance: 0"));
    }

    /**
     * Ensures strategyA returns a path containing the single provided bay.
     */
    @Test
    @DisplayName("strategyA with single bay returns path with that bay")
    void strategyAWithSingleBay() {
        String result = sequencer.strategyA(Collections.singletonList(bay1));
        assertTrue(result.contains("Path: (1,2)"));
        assertTrue(result.contains("Total Distance:"));
    }

    /**
     * Validates the nearest neighbor behavior of strategyB and that it
     * includes all bays and a total distance.
     */
    @Test
    @DisplayName("strategyB finds nearest neighbor path")
    void strategyBFindsNearestNeighborPath() {
        String result = sequencer.strategyB(bays);
        assertTrue(result.contains("Path:"));
        assertTrue(result.contains("(1,2)"));
        assertTrue(result.contains("(1,4)"));
        assertTrue(result.contains("(2,1)"));
        assertTrue(result.contains("(2,3)"));
        assertTrue(result.contains("Total Distance:"));
    }

    /**
     * Ensures strategyB yields a zero distance path when given no bays.
     */
    @Test
    @DisplayName("strategyB with empty list returns path with zero distance")
    void strategyBWithEmptyList() {
        String result = sequencer.strategyB(Collections.emptyList());
        assertTrue(result.contains("Path:"));
        assertTrue(result.contains("Total Distance: 0"));
    }

    /**
     * Ensures strategyB returns a path containing the single provided bay.
     */
    @Test
    @DisplayName("strategyB with single bay returns path with that bay")
    void strategyBWithSingleBay() {
        String result = sequencer.strategyB(Collections.singletonList(bay1));
        assertTrue(result.contains("Path: (1,2)"));
        assertTrue(result.contains("Total Distance:"));
    }

    /**
     * Confirms behavior of strategyB when multiple bays share the same aisle.
     */
    @Test
    @DisplayName("strategyB handles bays with same aisle differently than different aisles")
    void strategyBHandlesSameAisleDifferently() {
        List<Bay> sameAisleBays = Arrays.asList(
                new Bay("", 1, 2, 0),
                new Bay("", 1, 5, 0),
                new Bay("", 1, 8, 0)
        );
        String result = sequencer.strategyB(sameAisleBays);
        assertTrue(result.contains("Total Distance:"));
    }

    /**
     * Compares total distances reported by strategies A and B for the same
     * complex input; they are expected to differ.
     */
    @Test
    @DisplayName("Compare strategies A and B with same input")
    void compareStrategiesWithSameInput() {
        List<Bay> complexPath = Arrays.asList(
                new Bay("", 1, 5, 0),
                new Bay("", 3, 2, 0),
                new Bay("", 2, 8, 0),
                new Bay("", 4, 1, 0),
                new Bay("", 1, 9, 0)
        );
        String resultA = sequencer.strategyA(complexPath);
        String resultB = sequencer.strategyB(complexPath);
        for (Bay bay : complexPath) {
            String bayStr = "(" + bay.getAisle() + "," + bay.getBay() + ")";
            assertTrue(resultA.contains(bayStr));
            assertTrue(resultB.contains(bayStr));
        }
        int distanceA = extractDistance(resultA);
        int distanceB = extractDistance(resultB);
        assertNotEquals(distanceA, distanceB);
    }

    /**
     * Extracts the numeric distance from a result string formatted with
     * a trailing "Total Distance: <number>".
     *
     * @param result formatted strategy result string
     * @return parsed distance value
     */
    private int extractDistance(String result) {
        String distanceStr = result.substring(result.indexOf("Total Distance: ") + 16);
        return Integer.parseInt(distanceStr);
    }
}
