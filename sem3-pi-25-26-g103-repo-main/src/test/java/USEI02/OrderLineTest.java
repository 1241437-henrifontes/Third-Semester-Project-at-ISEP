package USEI02;

import Model.OrderLine;
import Model.SKU;
import Model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderLine model covering constructors, accessors, equals, and compareTo.
 */
class OrderLineTest {
    private OrderLine orderLine;
    private final String TEST_ORDER_ID = "ORD123";
    private final int TEST_LINE_NUMBER = 1;
    private final SKU TEST_SKU = new SKU("SKU123");
    private final int TEST_QTY = 10;
    private final Status TEST_STATUS = Status.ELIGIBLE;

    @BeforeEach
    void setUp() {
        orderLine = new OrderLine(TEST_ORDER_ID, TEST_LINE_NUMBER, TEST_SKU, TEST_QTY, TEST_STATUS);
    }

    @AfterEach
    void tearDown() {
        orderLine = null;
    }

    @Test
    void testDefaultConstructor() {
        OrderLine defaultOrderLine = new OrderLine();

        assertEquals("", defaultOrderLine.getOrderId());
        assertEquals(0, defaultOrderLine.getLineNumber());
        assertNull(defaultOrderLine.getSku());
        assertEquals(0, defaultOrderLine.getQty());
        assertNull(defaultOrderLine.getStatus());
    }

    @Test
    void testConstructorWithoutStatus() {
        OrderLine orderLineNoStatus = new OrderLine(TEST_ORDER_ID, TEST_LINE_NUMBER, TEST_SKU, TEST_QTY);

        assertEquals(TEST_ORDER_ID, orderLineNoStatus.getOrderId());
        assertEquals(TEST_LINE_NUMBER, orderLineNoStatus.getLineNumber());
        assertEquals(TEST_SKU, orderLineNoStatus.getSku());
        assertEquals(TEST_QTY, orderLineNoStatus.getQty());
        assertNull(orderLineNoStatus.getStatus());
    }

    @Test
    void testConstructorWithStatus() {
        assertEquals(TEST_ORDER_ID, orderLine.getOrderId());
        assertEquals(TEST_LINE_NUMBER, orderLine.getLineNumber());
        assertEquals(TEST_SKU, orderLine.getSku());
        assertEquals(TEST_QTY, orderLine.getQty());
        assertEquals(TEST_STATUS, orderLine.getStatus());
    }

    @Test
    void getOrderId() {
        assertEquals(TEST_ORDER_ID, orderLine.getOrderId());
    }

    @Test
    void setOrderId() {
        String newOrderId = "ORD456";
        orderLine.setOrderId(newOrderId);
        assertEquals(newOrderId, orderLine.getOrderId());
    }

    @Test
    void getLineNumber() {
        assertEquals(TEST_LINE_NUMBER, orderLine.getLineNumber());
    }

    @Test
    void setLineNumber() {
        int newLineNumber = 2;
        orderLine.setLineNumber(newLineNumber);
        assertEquals(newLineNumber, orderLine.getLineNumber());
    }

    @Test
    void getSku() {
        assertEquals(TEST_SKU, orderLine.getSku());
    }

    @Test
    void setSku() {
        SKU newSku = new SKU("SKU456");
        orderLine.setSku(newSku);
        assertEquals(newSku, orderLine.getSku());
    }

    @Test
    void getQty() {
        assertEquals(TEST_QTY, orderLine.getQty());
    }

    @Test
    void setQty() {
        int newQty = 20;
        orderLine.setQty(newQty);
        assertEquals(newQty, orderLine.getQty());
    }

    @Test
    void getStatus() {
        assertEquals(TEST_STATUS, orderLine.getStatus());
    }

    @Test
    void setStatus() {
        Status newStatus = Status.PARTIAL;
        orderLine.setStatus(newStatus);
        assertEquals(newStatus, orderLine.getStatus());
    }

    @Test
    void testEquals() {
        OrderLine sameOrderLine = new OrderLine(TEST_ORDER_ID, TEST_LINE_NUMBER, TEST_SKU, TEST_QTY, TEST_STATUS);
        assertTrue(orderLine.equals(sameOrderLine));

        assertTrue(orderLine.equals(orderLine));

        assertFalse(orderLine.equals(null));

        assertFalse(orderLine.equals("Not an OrderLine"));

        OrderLine differentOrderId = new OrderLine("DIFFERENT", TEST_LINE_NUMBER, TEST_SKU, TEST_QTY, TEST_STATUS);
        assertFalse(orderLine.equals(differentOrderId));

        OrderLine differentLineNumber = new OrderLine(TEST_ORDER_ID, 999, TEST_SKU, TEST_QTY, TEST_STATUS);
        assertFalse(orderLine.equals(differentLineNumber));

        OrderLine differentSku = new OrderLine(TEST_ORDER_ID, TEST_LINE_NUMBER, new SKU("DIFFERENT"), TEST_QTY, TEST_STATUS);
        assertFalse(orderLine.equals(differentSku));

        OrderLine differentQty = new OrderLine(TEST_ORDER_ID, TEST_LINE_NUMBER, TEST_SKU, 999, TEST_STATUS);
        assertFalse(orderLine.equals(differentQty));

        OrderLine differentStatus = new OrderLine(TEST_ORDER_ID, TEST_LINE_NUMBER, TEST_SKU, TEST_QTY, Status.UNDISPATCHABLE);
        assertFalse(orderLine.equals(differentStatus));
    }

    @Test
    void compareTo() {
        OrderLine sameLineNumber = new OrderLine("OTHER", TEST_LINE_NUMBER, new SKU("OTHER"), 5, Status.PARTIAL);
        assertEquals(0, orderLine.compareTo(sameLineNumber));

        OrderLine smallerLineNumber = new OrderLine("OTHER", TEST_LINE_NUMBER - 1, new SKU("OTHER"), 5, Status.PARTIAL);
        assertTrue(orderLine.compareTo(smallerLineNumber) > 0);

        OrderLine largerLineNumber = new OrderLine("OTHER", TEST_LINE_NUMBER + 1, new SKU("OTHER"), 5, Status.PARTIAL);
        assertTrue(orderLine.compareTo(largerLineNumber) < 0);
    }
}
