package USEI05;

import Model.Return;
import Model.ReturnAction;
import Model.ReturnReason;
import Model.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReturnTest {

    private Return returnObj;
    private SKU sku;
    private LocalDateTime timestamp;
    private LocalDateTime expiryDate;

    @BeforeEach
    void setUp() {
        sku = new SKU("SKU-001");
        timestamp = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        expiryDate = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
        returnObj = new Return("RET-001", sku, 5, timestamp, expiryDate, ReturnReason.DAMAGED);
    }

    // Constructor Tests
    @Test
    void testConstructorInitializesAllFields() {
        assertEquals("RET-001", returnObj.getReturnId());
        assertEquals(sku, returnObj.getSku());
        assertEquals(5, returnObj.getQuantity());
        assertEquals(timestamp, returnObj.getTimestamp());
        assertEquals(expiryDate, returnObj.getExpiryDate());
        assertEquals(ReturnReason.DAMAGED, returnObj.getReason());
    }

    @Test
    void testConstructorInitializesProcessedAsFalse() {
        assertFalse(returnObj.isProcessed());
    }

    @Test
    void testConstructorInitializesActionAsNull() {
        assertNull(returnObj.getAction());
    }

    // Getter Tests
    @Test
    void testGetReturnId() {
        assertEquals("RET-001", returnObj.getReturnId());
    }

    @Test
    void testGetSku() {
        assertEquals(sku, returnObj.getSku());
    }

    @Test
    void testGetQuantity() {
        assertEquals(5, returnObj.getQuantity());
    }

    @Test
    void testGetTimestamp() {
        assertEquals(timestamp, returnObj.getTimestamp());
    }

    @Test
    void testGetExpiryDate() {
        assertEquals(expiryDate, returnObj.getExpiryDate());
    }

    @Test
    void testGetReason() {
        assertEquals(ReturnReason.DAMAGED, returnObj.getReason());
    }

    // Processed Flag Tests
    @Test
    void testIsProcessedInitiallyFalse() {
        assertFalse(returnObj.isProcessed());
    }

    @Test
    void testSetProcessedToTrue() {
        returnObj.setProcessed(true);
        assertTrue(returnObj.isProcessed());
    }

    @Test
    void testSetProcessedToFalse() {
        returnObj.setProcessed(true);
        returnObj.setProcessed(false);
        assertFalse(returnObj.isProcessed());
    }

    // Action Tests
    @Test
    void testGetActionInitiallyNull() {
        assertNull(returnObj.getAction());
    }

    @Test
    void testSetActionToRestocked() {
        returnObj.setAction(ReturnAction.RESTOCKED);
        assertEquals(ReturnAction.RESTOCKED, returnObj.getAction());
    }

    @Test
    void testSetActionToDiscarded() {
        returnObj.setAction(ReturnAction.DISCARDED);
        assertEquals(ReturnAction.DISCARDED, returnObj.getAction());
    }

    @Test
    void testSetActionCanBeChanged() {
        returnObj.setAction(ReturnAction.RESTOCKED);
        returnObj.setAction(ReturnAction.DISCARDED);
        assertEquals(ReturnAction.DISCARDED, returnObj.getAction());
    }

    // ToString Tests
    @Test
    void testToStringContainsAllFields() {
        String result = returnObj.toString();
        assertTrue(result.contains("RET-001"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("DAMAGED"));
        assertTrue(result.contains("false"));
    }

    @Test
    void testToStringFormat() {
        String result = returnObj.toString();
        assertTrue(result.startsWith("Return{"));
        assertTrue(result.endsWith("}"));
    }

    // CompareTo Tests - Expiry Date Comparison
    @Test
    void testCompareToWithBothExpiryDatesNotNull() {
        LocalDateTime earlierExpiry = LocalDateTime.of(2024, 5, 15, 10, 30, 0);
        LocalDateTime laterExpiry = LocalDateTime.of(2024, 7, 15, 10, 30, 0);

        Return return1 = new Return("RET-001", new SKU("SKU-001"), 5, timestamp, earlierExpiry, ReturnReason.DAMAGED);
        Return return2 = new Return("RET-002", new SKU("SKU-002"), 3, timestamp, laterExpiry, ReturnReason.EXPIRED);

        // return1 has earlier expiry, so it should be considered "greater" (return 1)
        assertTrue(return1.compareTo(return2) > 0);
        // return2 has later expiry, so it should be considered "less" (return -1)
        assertTrue(return2.compareTo(return1) < 0);
    }

    @Test
    void testCompareToWithSameExpiryDateCompareBySku() {
        LocalDateTime sameExpiry = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
        SKU sku1 = new SKU("SKU-001");
        SKU sku2 = new SKU("SKU-002");

        Return return1 = new Return("RET-001", sku1, 5, timestamp, sameExpiry, ReturnReason.DAMAGED);
        Return return2 = new Return("RET-002", sku2, 3, timestamp, sameExpiry, ReturnReason.EXPIRED);

        // When expiry dates are equal, compare by SKU
        int result = return1.compareTo(return2);
        assertEquals(sku1.getSku().compareTo(sku2.getSku()), result);
    }

    @Test
    void testCompareToFirstHasExpirySecondDoesNot() {
        Return return1 = new Return("RET-001", sku, 5, timestamp, expiryDate, ReturnReason.DAMAGED);
        Return return2 = new Return("RET-002", sku, 3, timestamp, null, ReturnReason.EXPIRED);

        // return1 has expiry date, return2 doesn't, so return1 should be "greater"
        assertTrue(return1.compareTo(return2) > 0);
    }

    @Test
    void testCompareToNeitherHasExpiryDateSameTimestampCompareBySku() {
        SKU sku1 = new SKU("SKU-001");
        SKU sku2 = new SKU("SKU-002");

        Return return1 = new Return("RET-001", sku1, 5, timestamp, null, ReturnReason.DAMAGED);
        Return return2 = new Return("RET-002", sku2, 3, timestamp, null, ReturnReason.EXPIRED);

        // When timestamps are equal, compare by SKU
        int result = return1.compareTo(return2);
        assertEquals(sku1.getSku().compareTo(sku2.getSku()), result);
    }

    @Test
    void testCompareToIdenticalReturns() {
        Return return1 = new Return("RET-001", sku, 5, timestamp, expiryDate, ReturnReason.DAMAGED);
        Return return2 = new Return("RET-001", sku, 5, timestamp, expiryDate, ReturnReason.DAMAGED);

        assertEquals(0, return1.compareTo(return2));
    }

    // Edge Cases
    @Test
    void testReturnWithZeroQuantity() {
        Return zeroQuantityReturn = new Return("RET-ZERO", sku, 0, timestamp, expiryDate, ReturnReason.CYCLE_COUNT);
        assertEquals(0, zeroQuantityReturn.getQuantity());
    }

    @Test
    void testReturnWithNegativeQuantity() {
        Return negativeReturn = new Return("RET-NEG", sku, -5, timestamp, expiryDate, ReturnReason.CUSTOMER_REMORSE);
        assertEquals(-5, negativeReturn.getQuantity());
    }

    @Test
    void testReturnWithLargeQuantity() {
        Return largeReturn = new Return("RET-LARGE", sku, 1000000, timestamp, expiryDate, ReturnReason.EXPIRED);
        assertEquals(1000000, largeReturn.getQuantity());
    }

    @Test
    void testReturnWithDifferentReasons() {
        for (ReturnReason reason : ReturnReason.values()) {
            Return ret = new Return("RET-" + reason, sku, 5, timestamp, expiryDate, reason);
            assertEquals(reason, ret.getReason());
        }
    }

    @Test
    void testReturnWithNullExpiryDate() {
        Return nullExpiryReturn = new Return("RET-NULL-EXPIRY", sku, 5, timestamp, null, ReturnReason.CUSTOMER_REMORSE);
        assertNull(nullExpiryReturn.getExpiryDate());
    }

    @Test
    void testMultipleReturnsWithSameSku() {
        Return return1 = new Return("RET-001", sku, 5, timestamp, expiryDate, ReturnReason.DAMAGED);
        Return return2 = new Return("RET-002", sku, 3, timestamp, expiryDate, ReturnReason.EXPIRED);

        assertEquals(sku, return1.getSku());
        assertEquals(sku, return2.getSku());
    }

    @Test
    void testReturnStateChanges() {
        assertFalse(returnObj.isProcessed());
        assertNull(returnObj.getAction());

        returnObj.setProcessed(true);
        returnObj.setAction(ReturnAction.RESTOCKED);

        assertTrue(returnObj.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, returnObj.getAction());
    }
}
