package Model;

import java.util.Objects;

/**
 * Represents a single order line within an order including SKU, requested quantity and status.
 */
public class OrderLine implements Comparable<OrderLine> {
    private String orderId;
    private int lineNumber;
    private SKU sku;
    private int qty;
    private Status status;

    public OrderLine() {
        orderId = "";
        lineNumber = 0;
        sku = null;
        qty = 0;
        status = null;
    }

    public OrderLine(String orderId, int lineNumber, SKU sku, int qty) {
        this.orderId = orderId;
        this.lineNumber = lineNumber;
        this.sku = sku;
        this.qty = qty;
        this.status = null;
    }

    public OrderLine(String orderId, int lineNumber, SKU sku, int qty, Status status) {
        this.orderId = orderId;
        this.lineNumber = lineNumber;
        this.sku = sku;
        this.qty = qty;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public SKU getSku() {
        return sku;
    }

    public void setSku(SKU sku) {
        this.sku = sku;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Two order lines are equal if all their fields match.
     *
     * @param o object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderLine orderLine)) return false;
        return Objects.equals(orderId, orderLine.orderId) && lineNumber == orderLine.lineNumber && qty == orderLine.qty && Objects.equals(sku, orderLine.sku) && Objects.equals(status, orderLine.status);
    }

    /**
     * Hash code consistent with equals.
     *
     * @return hash of order line fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId, lineNumber, sku, qty, status);
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "orderId=" + orderId +
                ", lineNumber=" + lineNumber +
                ", sku=" + sku +
                ", qty=" + qty +
                ", status=" + status +
                '}';
    }

    /**
     * Natural ordering by line number (ASC).
     *
     * @param ol other order line
     * @return comparison result for ascending line number
     */
    @Override
    public int compareTo(OrderLine ol) {
        return Integer.compare(this.lineNumber, ol.lineNumber);
    }
}
