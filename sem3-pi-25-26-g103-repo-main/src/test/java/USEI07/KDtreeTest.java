package USEI07;

import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import Model.Trees.KDnode;
import Model.Trees.KDtree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KDtree class covering tree building, structure verification,
 * and various tree metrics.
 */
@DisplayName("KDtree Tests")
public class KDtreeTest {

    private KDtree kdTree;
    private List<RailwayStation> testStations;
    private RailwayStation station1, station2, station3, station4, station5;

    /**
     * Initializes test objects before each test.
     */
    @BeforeEach
    void setUp() {
        kdTree = new KDtree();

        station1 = new RailwayStation("Station A", 40.0, -8.0,
                Country.PT, "WET", TimeZoneGroup.WET_GMT, true, false, false);
        station2 = new RailwayStation("Station B", 41.0, -7.0,
                Country.PT, "WET", TimeZoneGroup.WET_GMT, false, true, false);
        station3 = new RailwayStation("Station C", 39.0, -9.0,
                Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, true);
        station4 = new RailwayStation("Station D", 40.0, -8.0,
                Country.PT, "WET", TimeZoneGroup.WET_GMT, true, true, false);
        station5 = new RailwayStation("Station E", 42.0, -6.0,
                Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);

        testStations = new ArrayList<RailwayStation>();
        testStations.add(station1);
        testStations.add(station2);
        testStations.add(station3);
        testStations.add(station4);
        testStations.add(station5);
    }

    /**
     * Tests building a KD-tree with a small controlled dataset.
     */
    @Test
    @DisplayName("Building a KD-tree with a small controlled dataset")
    void buildTreeWithControlledDataset() {
        // Arrange is done in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);

        // Assert
        assertNotNull(kdTree.getRoot());
    }

    /**
     * Tests that the root node is correctly selected.
     */
    @Test
    @DisplayName("Root is correctly selected")
    void rootIsCorrectlySelected() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        KDnode root = kdTree.getRoot();

        // Assert
        assertNotNull(root);
        assertEquals(40.0, root.getLatitude());
        assertEquals(-8.0, root.getLongitude());
    }

    /**
     * Tests that left and right children are correctly assigned.
     */
    @Test
    @DisplayName("Left and right children are correctly assigned")
    void leftAndRightChildrenAreCorrectlyAssigned() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        KDnode root = kdTree.getRoot();
        KDnode leftChild = root.getLeft();
        KDnode rightChild = root.getRight();

        // Assert
        assertNotNull(leftChild);
        assertNotNull(rightChild);
        assertTrue(leftChild.getLatitude() < root.getLatitude());
        assertTrue(rightChild.getLatitude() > root.getLatitude());
    }

    /**
     * Tests that nodes with the same coordinates are grouped into the same bucket.
     */
    @Test
    @DisplayName("Nodes with same coordinates are grouped into the same bucket")
    void nodesWithSameCoordinatesAreGroupedInSameBucket() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        KDnode root = kdTree.getRoot();
        List<RailwayStation> rootStations = root.getStations();

        // Assert
        assertEquals(2, rootStations.size());

        boolean hasStation1 = false;
        boolean hasStation4 = false;

        for (RailwayStation station : rootStations) {
            if (station.getName().equals("Station A")) {
                hasStation1 = true;
            }
            if (station.getName().equals("Station D")) {
                hasStation4 = true;
            }
        }

        assertTrue(hasStation1);
        assertTrue(hasStation4);
    }

    /**
     * Tests that tree height is computed correctly for the controlled dataset.
     */
    @Test
    @DisplayName("Tree height is computed correctly")
    void treeHeightIsComputedCorrectly() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        int height = kdTree.height();

        // Assert
        assertEquals(3, height);
    }

    /**
     * Tests that tree size is computed correctly.
     */
    @Test
    @DisplayName("Tree size is computed correctly")
    void treeSizeIsComputedCorrectly() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        int size = kdTree.size();

        // Assert
        assertEquals(4, size);
    }

    /**
     * Tests that bucket sizes are collected correctly.
     */
    @Test
    @DisplayName("Bucket sizes are collected correctly")
    void bucketSizesAreCollectedCorrectly() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        List<Integer> bucketSizes = kdTree.bucketSizes();

        // Assert
        assertNotNull(bucketSizes);
        assertEquals(4, bucketSizes.size());

        int bucketsOfSizeOne = 0;
        int bucketsOfSizeTwo = 0;

        for (int size : bucketSizes) {
            if (size == 1) {
                bucketsOfSizeOne++;
            }
            if (size == 2) {
                bucketsOfSizeTwo++;
            }
        }

        assertEquals(3, bucketsOfSizeOne);
        assertEquals(1, bucketsOfSizeTwo);
    }

    /**
     * Tests that distinct bucket sizes are correct.
     */
    @Test
    @DisplayName("Distinct bucket sizes are correct")
    void distinctBucketSizesAreCorrect() {
        // Arrange in setUp

        // Act
        kdTree.buildTreeFromStationList(testStations);
        Set<Integer> distinctSizes = kdTree.distinctBucketSizes();

        // Assert
        assertNotNull(distinctSizes);
        assertEquals(2, distinctSizes.size());
        assertTrue(distinctSizes.contains(1));
        assertTrue(distinctSizes.contains(2));
    }

    /**
     * Tests that attempting to build with an empty dataset results in null root.
     */
    @Test
    @DisplayName("Attempting to build with an empty dataset results in null root")
    void buildWithEmptyDatasetResultsInNullRoot() {
        // Arrange
        List<RailwayStation> emptyList = new ArrayList<RailwayStation>();

        // Act
        kdTree.buildTreeFromStationList(emptyList);

        // Assert
        assertNull(kdTree.getRoot());
        assertEquals(0, kdTree.size());
        assertEquals(0, kdTree.height());
    }
}
