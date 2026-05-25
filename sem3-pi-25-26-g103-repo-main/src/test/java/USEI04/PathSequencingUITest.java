package USEI04;

import Repositories.TrolleyRepository;
import Model.PickAllocationRow;
import Model.Trolley;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import Services.PickingService;
import UI.PathSequencingUI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI tests for validating path sequencing behavior and console output
 * for various repository states and trolley configurations.
 */
@DisplayName("PathSequencingUI Tests")
public class PathSequencingUITest {

    private PathSequencingUI ui;
    private TrolleyRepository repository;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * Prepares the test context by redirecting System.out and
     * initializing the UI and repository with a clean state.
     */
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        ui = new PathSequencingUI();
        repository = TrolleyRepository.getInstance();
        repository.clear();
    }

    /**
     * Restores System.out and clears the repository after each test.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        repository.clear();
    }

    /**
     * Verifies that the UI prints an informative message when no trolleys
     * are available in the repository.
     */
    @Test
    @DisplayName("run() with empty repository shows appropriate message")
    void runWithEmptyRepositoryShowsMessage() {
        assertTrue(repository.getAllTrolleys().isEmpty());
        ui.run();
        String output = outputStream.toString();
        assertTrue(output.contains("No trolleys available"));
        assertTrue(output.contains("please run the Picking Plan"));
    }

    /**
     * Ensures the UI processes available trolleys and reports strategies and
     * completion status when the repository is populated.
     */
    @Test
    @DisplayName("run() with populated repository processes trolleys")
    void runWithPopulatedRepositoryProcessesTrolleys() {
        List<Trolley> trolleys = createTestTrolleys();
        repository.storePlan(trolleys, PickingService.Heuristic.FF,
                PickingService.OverflowPolicy.SPLIT, 100.0);
        ui.run();
        String output = outputStream.toString();
        assertTrue(output.contains("Loaded " + trolleys.size() + " trolleys"));
        assertTrue(output.contains("Strategy A"));
        assertTrue(output.contains("Strategy B"));
        assertTrue(output.contains("Path sequencing completed"));
    }

    /**
     * Confirms that the UI displays heuristic, policy, and capacity metadata
     * obtained from the repository.
     */
    @Test
    @DisplayName("run() displays correct metadata from repository")
    void runDisplaysCorrectMetadata() {
        List<Trolley> trolleys = createTestTrolleys();
        PickingService.Heuristic heuristic = PickingService.Heuristic.BFD;
        PickingService.OverflowPolicy policy = PickingService.OverflowPolicy.DEFER;
        double capacity = 75.5;
        repository.storePlan(trolleys, heuristic, policy, capacity);
        ui.run();
        String output = outputStream.toString();
        assertTrue(output.contains("Heuristic = " + heuristic));
        assertTrue(output.contains("Policy = " + policy));
        assertTrue(output.contains("Capacity = " + capacity));
    }

    /**
     * Checks that the UI handles trolleys with no valid bays by printing
     * an appropriate message.
     */
    @Test
    @DisplayName("run() handles trolleys with no valid bays")
    void runHandlesTrolleysWithNoValidBays() {
        Trolley emptyTrolley = new Trolley(100.0);
        List<Trolley> trolleys = new ArrayList<>();
        trolleys.add(emptyTrolley);
        repository.storePlan(trolleys, PickingService.Heuristic.FF,
                PickingService.OverflowPolicy.SPLIT, 100.0);
        ui.run();
        String output = outputStream.toString();
        assertTrue(output.contains("No valid bays for this trolley"));
    }

    /**
     * Validates that multiple trolleys are reported and both strategies
     * are executed for each one.
     */
    @Test
    @DisplayName("run() processes multiple trolleys correctly")
    void runProcessesMultipleTrolleysCorrectly() {
        List<Trolley> trolleys = new ArrayList<>();
        Trolley trolley1 = new Trolley(100.0);
        addPicksToTrolley(trolley1, new int[][]{{1, 2}, {3, 4}, {5, 6}});
        trolleys.add(trolley1);
        Trolley trolley2 = new Trolley(100.0);
        addPicksToTrolley(trolley2, new int[][]{{7, 8}, {9, 10}});
        trolleys.add(trolley2);
        repository.storePlan(trolleys, PickingService.Heuristic.FF,
                PickingService.OverflowPolicy.SPLIT, 100.0);
        ui.run();
        String output = outputStream.toString();
        assertTrue(output.contains("Trolley #1"));
        assertTrue(output.contains("Trolley #2"));
        int strategyACount = countOccurrences(output, "Strategy A");
        int strategyBCount = countOccurrences(output, "Strategy B");
        assertEquals(2, strategyACount);
        assertEquals(2, strategyBCount);
    }

    /**
     * Creates a list with a single trolley populated with a few sample picks.
     *
     * @return list of prepared trolleys
     */
    private List<Trolley> createTestTrolleys() {
        List<Trolley> trolleys = new ArrayList<>();
        Trolley trolley = new Trolley(100.0);
        addPicksToTrolley(trolley, new int[][]{{1, 2}, {3, 4}, {5, 6}});
        trolleys.add(trolley);
        return trolleys;
    }

    /**
     * Adds pick rows to the given trolley for the provided (aisle, bay) pairs.
     *
     * @param trolley      trolley to enrich with picks
     * @param aisleAndBays array of pairs [aisle, bay]
     */
    private void addPicksToTrolley(Trolley trolley, int[][] aisleAndBays) {
        for (int[] aisleAndBay : aisleAndBays) {
            PickAllocationRow row = createPickAllocationRow("O1", 1, "SKU1", "B1",
                    aisleAndBay[0], aisleAndBay[1],
                    1, 1.0);
            trolley.tryAdd(row);
        }
    }

    /**
     * Builds a PickAllocationRow for tests.
     *
     * @param orderId    order identifier
     * @param lineNo     order line number
     * @param skuId      SKU identifier
     * @param boxId      box identifier
     * @param aisle      aisle index
     * @param bay        bay index
     * @param qty        quantity
     * @param unitWeight weight per unit
     * @return created PickAllocationRow instance
     */
    private PickAllocationRow createPickAllocationRow(String orderId, int lineNo, String skuId,
                                                      String boxId, int aisle, int bay,
                                                      int qty, double unitWeight) {
        return new PickAllocationRow(orderId, lineNo, new Model.SKU(skuId),
                qty, boxId, aisle, bay, unitWeight);
    }

    /**
     * Counts the occurrences of a substring within a string.
     *
     * @param str    the source string
     * @param substr the substring to count
     * @return number of times substr appears in str
     */
    private int countOccurrences(String str, String substr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substr, index)) != -1) {
            count++;
            index += substr.length();
        }
        return count;
    }
}
