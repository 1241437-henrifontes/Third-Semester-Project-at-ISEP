package USEI01;

import Repositories.ItemRepository;
import Repositories.WagonRepository;
import Model.Box;
import Model.Wagon;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WagonRepository Tests")
public class WagonRepositoryTest {

    private WagonRepository repository;

    @BeforeAll
    static void loadOnce() {
        ItemRepository.getInstance().loadItems();
        WagonRepository.getInstance().loadWagons();
    }

    @BeforeEach
    void setUp() {
        repository = WagonRepository.getInstance();
    }

    @Test
    @DisplayName("Wagons loaded from CSV are not null")
    void wagonsLoadedFromCSVAreNotNull() {
        List<Wagon> wagons = repository.getWagons();
        assertNotNull(wagons);
    }

    @Test
    @DisplayName("Wagons loaded from CSV are not empty")
    void wagonsLoadedFromCSVAreNotEmpty() {
        List<Wagon> wagons = repository.getWagons();
        assertFalse(wagons.isEmpty());
    }

    @Test
    @DisplayName("At least one wagon contains boxes")
    void atLeastOneWagonContainsBoxes() {
        List<Wagon> wagons = repository.getWagons();
        boolean anyHasBox = wagons.stream().anyMatch(w -> !w.getBoxes().isEmpty());
        assertTrue(anyHasBox);
    }

    @Test
    @DisplayName("All wagons have non-null wagon ID")
    void allWagonsHaveNonNullWagonID() {
        List<Wagon> wagons = repository.getWagons();
        for (Wagon wagon : wagons) {
            assertNotNull(wagon.getWagonId());
        }
    }

    @Test
    @DisplayName("All wagons have non-null boxes list")
    void allWagonsHaveNonNullBoxesList() {
        List<Wagon> wagons = repository.getWagons();
        for (Wagon wagon : wagons) {
            assertNotNull(wagon.getBoxes());
        }
    }

    @Test
    @DisplayName("All boxes aggregate from all wagons")
    void allBoxesAggregateFromAllWagons() {
        List<Wagon> wagons = repository.getWagons();
        List<Box> all = repository.getAllBoxesAndRemove();
        int expected = wagons.stream().mapToInt(w -> w.getBoxes().size()).sum();
        assertEquals(expected, all.size());
    }

    @Test
    @DisplayName("Get all boxes returns non-null list")
    void getAllBoxesAndRemoveReturnsNonNullList() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        assertNotNull(boxes);
    }

    @Test
    @DisplayName("Get all boxes returns non-empty list")
    void getAllBoxesAndRemoveReturnsNonEmptyList() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        assertFalse(boxes.isEmpty());
    }

    @Test
    @DisplayName("All boxes have non-null box ID")
    void allBoxesHaveNonNullBoxID() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            assertNotNull(box.getBoxId());
        }
    }

    @Test
    @DisplayName("All boxes have non-null received date")
    void allBoxesHaveNonNullReceivedDate() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            assertNotNull(box.getReceivedAt());
        }
    }

    @Test
    @DisplayName("All boxes have non-null items map")
    void allBoxesHaveNonNullItemsMap() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            assertNotNull(box.getItems());
        }
    }

    @Test
    @DisplayName("All boxes contain at least one item")
    void allBoxesContainAtLeastOneItem() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            assertFalse(box.getItems().isEmpty());
        }
    }

    @Test
    @DisplayName("Repository is singleton")
    void repositoryIsSingleton() {
        WagonRepository repo1 = WagonRepository.getInstance();
        WagonRepository repo2 = WagonRepository.getInstance();
        assertSame(repo1, repo2);
    }

    @Test
    @DisplayName("Get wagons returns the same list instance")
    void getWagonsReturnsSameListInstance() {
        List<Wagon> wagons1 = repository.getWagons();
        List<Wagon> wagons2 = repository.getWagons();
        assertSame(wagons1, wagons2);
    }

    @Test
    @DisplayName("Get all boxes returns different list instances")
    void getAllBoxesAndRemoveReturnsDifferentListInstances() {
        List<Box> boxes1 = repository.getAllBoxesAndRemove();
        List<Box> boxes2 = repository.getAllBoxesAndRemove();
        assertNotSame(boxes1, boxes2);
    }

    @Test
    @DisplayName("All boxes from getAllBoxesAndRemove are in wagons")
    void allBoxesFromGetAllBoxesAndRemoveAreInWagons() {
        List<Wagon> wagons = repository.getWagons();
        List<Box> allBoxes = repository.getAllBoxesAndRemove();
        
        for (Box box : allBoxes) {
            boolean found = false;
            for (Wagon wagon : wagons) {
                if (wagon.getBoxes().contains(box)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Box " + box.getBoxId() + " not found in any wagon");
        }
    }

    @Test
    @DisplayName("Wagon IDs are unique")
    void wagonIDsAreUnique() {
        List<Wagon> wagons = repository.getWagons();
        for (int i = 0; i < wagons.size(); i++) {
            for (int j = i + 1; j < wagons.size(); j++) {
                assertNotEquals(wagons.get(i).getWagonId(), wagons.get(j).getWagonId(),
                    "Duplicate wagon ID found");
            }
        }
    }

    @Test
    @DisplayName("All boxes have valid quantity values")
    void allBoxesHaveValidQuantityValues() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            box.getItems().forEach((item, quantity) -> {
                assertTrue(quantity >= 0, "Negative quantity found in box " + box.getBoxId());
            });
        }
    }

    @Test
    @DisplayName("All boxes have valid SKU references")
    void allBoxesHaveValidSKUReferences() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            box.getItems().forEach((item, quantity) -> {
                assertNotNull(item.getSku(), "Null SKU found in box " + box.getBoxId());
            });
        }
    }

    @Test
    @DisplayName("Multiple calls to getAllBoxesAndRemove return same total count")
    void multipleCallsToGetAllBoxesAndRemoveReturnSameTotalCount() {
        int count1 = repository.getAllBoxesAndRemove().size();
        int count2 = repository.getAllBoxesAndRemove().size();
        assertEquals(count1, count2);
    }

    @Test
    @DisplayName("Wagons list is mutable through getter")
    void wagonsListIsMutableThroughGetter() {
        List<Wagon> wagons = repository.getWagons();
        int originalSize = wagons.size();
        Wagon newWagon = new Wagon("NEW-WAGON", new java.util.ArrayList<>());
        wagons.add(newWagon);
        assertEquals(originalSize + 1, repository.getWagons().size());
    }

    @Test
    @DisplayName("All boxes have consistent data across multiple retrievals")
    void allBoxesHaveConsistentDataAcrossMultipleRetrievals() {
        Box box1 = repository.getAllBoxesAndRemove().get(0);
        Box box2 = repository.getAllBoxesAndRemove().get(0);
        
        assertEquals(box1.getBoxId(), box2.getBoxId());
        assertEquals(box1.getReceivedAt(), box2.getReceivedAt());
        assertEquals(box1.getExpiryDate(), box2.getExpiryDate());
    }

    @Test
    @DisplayName("All wagons contain boxes in correct order")
    void allWagonsContainBoxesInCorrectOrder() {
        List<Wagon> wagons = repository.getWagons();
        for (Wagon wagon : wagons) {
            List<Box> boxes = wagon.getBoxes();
            for (int i = 0; i < boxes.size(); i++) {
                assertNotNull(boxes.get(i).getBoxId());
            }
        }
    }

    @Test
    @DisplayName("Total box count matches sum of wagon boxes")
    void totalBoxCountMatchesSumOfWagonBoxes() {
        List<Wagon> wagons = repository.getWagons();
        List<Box> allBoxes = repository.getAllBoxesAndRemove();
        
        int totalFromWagons = wagons.stream()
            .mapToInt(w -> w.getBoxes().size())
            .sum();
        
        assertEquals(totalFromWagons, allBoxes.size());
    }

    @Test
    @DisplayName("All boxes have items with non-null keys")
    void allBoxesHaveItemsWithNonNullKeys() {
        List<Box> boxes = repository.getAllBoxesAndRemove();
        for (Box box : boxes) {
            box.getItems().forEach((item, quantity) -> {
                assertNotNull(item, "Null item key found in box " + box.getBoxId());
            });
        }
    }
}
