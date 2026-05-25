package USEI02;

import Model.Order;
import Model.OrderLine;
import Model.SKU;
import Model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Order model including constructors, accessors, equals, and compareTo.
 */
class OrderTest {
    private Order order;
    private final String TEST_ORDER_ID = "ORD123";
    private final LocalDateTime TEST_DUE_DATE = LocalDateTime.of(2023, 12, 31, 23, 59);
    private final int TEST_PRIORITY = 1;
    private List<OrderLine> TEST_LINES;

    @BeforeEach
    void setUp() {
        TEST_LINES = new ArrayList<>();
        TEST_LINES.add(new OrderLine(TEST_ORDER_ID, 1, new SKU("SKU123"), 10, Status.ELIGIBLE));
        TEST_LINES.add(new OrderLine(TEST_ORDER_ID, 2, new SKU("SKU456"), 5, Status.PARTIAL));

        order = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY, TEST_LINES);
    }

    @AfterEach
    void tearDown() {
        order = null;
        TEST_LINES = null;
    }

    @Test
    void testDefaultConstructor() {
        Order defaultOrder = new Order();

        assertEquals("", defaultOrder.getOrderId());
        assertNull(defaultOrder.getDueDate());
        assertEquals(0, defaultOrder.getPriority());
        assertNull(defaultOrder.getLines());
    }

    @Test
    void testConstructorWithoutLines() {
        Order orderWithoutLines = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY);

        assertEquals(TEST_ORDER_ID, orderWithoutLines.getOrderId());
        assertEquals(TEST_DUE_DATE, orderWithoutLines.getDueDate());
        assertEquals(TEST_PRIORITY, orderWithoutLines.getPriority());
        assertNull(orderWithoutLines.getLines());
    }

    @Test
    void testConstructorWithLines() {
        assertEquals(TEST_ORDER_ID, order.getOrderId());
        assertEquals(TEST_DUE_DATE, order.getDueDate());
        assertEquals(TEST_PRIORITY, order.getPriority());
        assertEquals(TEST_LINES, order.getLines());
    }

    @Test
    void getOrderId() {
        assertEquals(TEST_ORDER_ID, order.getOrderId());
    }

    @Test
    void getDueDate() {
        assertEquals(TEST_DUE_DATE, order.getDueDate());
    }

    @Test
    void getPriority() {
        assertEquals(TEST_PRIORITY, order.getPriority());
    }

    @Test
    void getLines() {
        assertEquals(TEST_LINES, order.getLines());
    }

    @Test
    void setOrderId() {
        String newOrderId = "ORD456";
        order.setOrderId(newOrderId);
        assertEquals(newOrderId, order.getOrderId());
    }

    @Test
    void setDueDate() {
        LocalDateTime newDueDate = LocalDateTime.of(2024, 1, 15, 12, 0);
        order.setDueDate(newDueDate);
        assertEquals(newDueDate, order.getDueDate());
    }

    @Test
    void setPriority() {
        int newPriority = 2;
        order.setPriority(newPriority);
        assertEquals(newPriority, order.getPriority());
    }

    @Test
    void setLines() {
        List<OrderLine> newLines = new ArrayList<>();
        newLines.add(new OrderLine(TEST_ORDER_ID, 3, new SKU("SKU789"), 15, Status.UNDISPATCHABLE));

        order.setLines(newLines);
        assertEquals(newLines, order.getLines());
    }

    @Test
    void testEquals() {
        Order sameOrder = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY, TEST_LINES);
        assertEquals(order, sameOrder);

        assertEquals(order, order);

        assertNotEquals(null, order);

        assertNotEquals("Not an Order", order);

        Order differentOrderId = new Order("DIFFERENT", TEST_DUE_DATE, TEST_PRIORITY, TEST_LINES);
        assertNotEquals(order, differentOrderId);

        Order differentDueDate = new Order(TEST_ORDER_ID, LocalDateTime.now(), TEST_PRIORITY, TEST_LINES);
        assertNotEquals(order, differentDueDate);

        Order differentPriority = new Order(TEST_ORDER_ID, TEST_DUE_DATE, 999, TEST_LINES);
        assertNotEquals(order, differentPriority);

        List<OrderLine> differentLinesList = new ArrayList<>();
        differentLinesList.add(new OrderLine("DIFFERENT", 99, new SKU("DIFFERENT"), 99, Status.UNDISPATCHABLE));
        Order differentLines = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY, differentLinesList);
        assertNotEquals(order, differentLines);
    }

    @Test
    void compareTo() {
        Order lowerPriority = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY - 1, TEST_LINES);
        assertTrue(order.compareTo(lowerPriority) > 0);

        Order higherPriority = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY + 1, TEST_LINES);
        assertTrue(order.compareTo(higherPriority) < 0);

        Order earlierDueDate = new Order(TEST_ORDER_ID, TEST_DUE_DATE.minusDays(1), TEST_PRIORITY, TEST_LINES);
        assertTrue(order.compareTo(earlierDueDate) > 0);

        Order laterDueDate = new Order(TEST_ORDER_ID, TEST_DUE_DATE.plusDays(1), TEST_PRIORITY, TEST_LINES);
        assertTrue(order.compareTo(laterDueDate) < 0);

        Order smallerOrderId = new Order("AAA", TEST_DUE_DATE, TEST_PRIORITY, TEST_LINES);
        assertTrue(order.compareTo(smallerOrderId) > 0);

        Order largerOrderId = new Order("ZZZ", TEST_DUE_DATE, TEST_PRIORITY, TEST_LINES);
        assertTrue(order.compareTo(largerOrderId) < 0);

        Order equalOrder = new Order(TEST_ORDER_ID, TEST_DUE_DATE, TEST_PRIORITY, TEST_LINES);
        assertEquals(0, order.compareTo(equalOrder));
    }
}
