package Model;

import java.time.LocalDateTime;

/**
 * Represents a product return placed into quarantine until inspected.
 * Captures SKU, quantity, timestamps, reason, and the processing outcome.
 */
public class Return implements Comparable<Return> {
    private String returnId;
    private SKU sku;
    private int quantity;
    private LocalDateTime timestamp;
    private LocalDateTime expiryDate;
    private ReturnReason reason;
    private boolean processed = false;
    private ReturnAction action;

    public Return(String returnId, SKU sku, int quantity, LocalDateTime timestamp, LocalDateTime expiryDate, ReturnReason reason) {
        this.returnId = returnId;
        this.sku = sku;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.expiryDate = expiryDate;
        this.reason = reason;
    }

    public String getReturnId() {
        return returnId;
    }

    public SKU getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public ReturnReason getReason() {
        return reason;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public ReturnAction getAction() {
        return action;
    }

    public void setAction(ReturnAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Return{" +
                "returnId='" + returnId + '\'' +
                ", sku=" + sku +
                ", quantity=" + quantity +
                ", timestamp=" + timestamp +
                ", expiryDate=" + expiryDate +
                ", reason=" + reason +
                ", processed=" + processed +
                ", action=" + action +
                '}';
    }

    /**
     * Natural ordering prioritizing earlier expiry dates first, then earlier timestamps, then SKU as tiebreaker.
     * Returns with an expiry date are considered before those without.
     *
     * @param o other return to compare
     * @return negative, zero, or positive as this object is less than, equal to, or greater than the specified object
     */
    @Override
    public int compareTo(Return o) {
        if (this.expiryDate != null) {
            if (o.expiryDate != null){
                return this.expiryDate.isBefore(o.expiryDate) ? 1 : (this.expiryDate.isAfter(o.expiryDate) ? -1 : this.sku.getSku().compareTo(o.sku.getSku()));
            }
            return 1;
        }
        return this.timestamp.isAfter(o.timestamp) ? 1 : (this.timestamp.isBefore(o.timestamp) ? -1 : this.sku.getSku().compareTo(o.sku.getSku()));
    }
}
