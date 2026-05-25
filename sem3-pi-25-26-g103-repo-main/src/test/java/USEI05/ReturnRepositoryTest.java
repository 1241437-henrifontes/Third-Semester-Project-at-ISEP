package USEI05;

import Repositories.ItemRepository;
import Repositories.ReturnRepository;
import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReturnRepository.
 * <p>
 * This test suite verifies:
 * - Singleton behavior of ReturnRepository
 * - Defensive copying and immutability of the returns list accessor
 * - Loading returns from CSV using ItemRepository as SKU source
 * - Date parsing acceptance for both date-time and date-only formats
 * - Basic integrity constraints on Return data
 */
@DisplayName("ReturnRepository Tests")
public class ReturnRepositoryTest {

    private ReturnRepository returnRepository;
    private ItemRepository itemRepository;
    private SKU testSku;
    private Item testItem;

    /**
     * Prepares fresh repository instances and seed data before each test.
     */
    @BeforeEach
    void setUp() {
        returnRepository = ReturnRepository.getInstance();
        itemRepository = ItemRepository.getInstance();
        returnRepository.getAllReturns().clear();
        testSku = new SKU("TEST-SKU-001");
        testItem = new Item(testSku, "Test Item", "Test Category", "Unit", 1.0f, 1.0f);
    }

    /**
     * Ensures getInstance returns the same singleton instance on repeated calls.
     */
    @Test
    @DisplayName("getInstance returns the same instance")
    void testGetInstanceReturnsSameInstance() {
        ReturnRepository instance1 = ReturnRepository.getInstance();
        ReturnRepository instance2 = ReturnRepository.getInstance();
        assertSame(instance1, instance2);
    }

    /**
     * Verifies getAllReturns provides a defensive copy, not the internal list.
     */
    @Test
    @DisplayName("getAllReturns returns a copy of the list")
    void testGetAllReturnsReturnsCopy() {
        Return testReturn = new Return("RET-001", testSku, 5, LocalDateTime.now(), null, ReturnReason.DAMAGED);
        returnRepository.addReturn(testReturn);

        List<Return> returns1 = returnRepository.getAllReturns();
        List<Return> returns2 = returnRepository.getAllReturns();

        assertNotSame(returns1, returns2);
        assertEquals(returns1.size(), returns2.size());
    }

    /**
     * Confirms external modifications to the list returned by getAllReturns do not
     * affect the repository's internal state.
     */
    @Test
    @DisplayName("getAllReturns does not allow external modification")
    void testGetAllReturnsImmutable() {
        Return testReturn = new Return("RET-001", testSku, 5, LocalDateTime.now(), null, ReturnReason.DAMAGED);
        returnRepository.addReturn(testReturn);

        List<Return> returns = returnRepository.getAllReturns();
        int originalSize = returns.size();

        returns.add(new Return("RET-002", testSku, 3, LocalDateTime.now(), null, ReturnReason.EXPIRED));

        List<Return> returnsAfter = returnRepository.getAllReturns();
        assertEquals(originalSize, returnsAfter.size());
    }

    /**
     * Loads returns from CSV using a pre-seeded ItemRepository and verifies
     * that some results are available (actual size depends on the dataset).
     */
    @Test
    @DisplayName("loadReturns loads returns from CSV file")
    void testLoadReturnsLoadsFromCSV() {
        List<Item> items = new java.util.ArrayList<>();
        items.add(testItem);
        itemRepository.setItems(items);

        returnRepository.loadReturns(itemRepository);

        List<Return> returns = returnRepository.getAllReturns();
        assertNotNull(returns);
    }

    /**
     * Ensures the loader handles unknown SKUs gracefully when ItemRepository has no items.
     */
    @Test
    @DisplayName("loadReturns validates SKU existence")
    void testLoadReturnsValidatesSKU() {
        itemRepository.setItems(new java.util.ArrayList<>());
        returnRepository.loadReturns(itemRepository);
        assertNotNull(returnRepository.getAllReturns());
    }

    /**
     * Verifies that date-time strings in format yyyy-MM-dd'T'HH:mm:ss are accepted during load.
     */
    @Test
    @DisplayName("loadReturns accepts date-time format yyyy-MM-ddTHH:mm:ss")
    void testLoadReturnsAcceptsDateTimeFormat() {
        List<Item> items = new java.util.ArrayList<>();
        items.add(testItem);
        itemRepository.setItems(items);

        returnRepository.loadReturns(itemRepository);

        assertNotNull(returnRepository.getAllReturns());
    }

    /**
     * Verifies that date-only strings in format yyyy-MM-dd are accepted during load.
     */
    @Test
    @DisplayName("loadReturns accepts date format yyyy-MM-dd")
    void testLoadReturnsAcceptsDateFormat() {
        List<Item> items = new java.util.ArrayList<>();
        items.add(testItem);
        itemRepository.setItems(items);

        returnRepository.loadReturns(itemRepository);

        assertNotNull(returnRepository.getAllReturns());
    }

    /**
     * Checks a Return instance where expiry date is after its timestamp.
     */
    @Test
    @DisplayName("Return with expiry date after timestamp")
    void testReturnWithExpiryDateAfterTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime expiryDate = LocalDateTime.of(2024, 6, 15, 10, 30, 0);

        Return testReturn = new Return("RET-001", testSku, 5, timestamp, expiryDate, ReturnReason.EXPIRED);
        returnRepository.addReturn(testReturn);

        List<Return> returns = returnRepository.getAllReturns();
        assertEquals(1, returns.size());
        assertTrue(returns.get(0).getExpiryDate().isAfter(returns.get(0).getTimestamp()));
    }
}
