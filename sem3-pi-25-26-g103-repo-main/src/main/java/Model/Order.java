package Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents a customer order containing order lines, a due date, and a priority.
 * Orders are comparable by priority, then due date, then order id.
 */
public class Order implements Comparable<Order> {
    private String orderId;
    private LocalDateTime dueDate;
    private int priority;
    private List<OrderLine> lines;

    public Order() {
        orderId = "";
        dueDate = null;
        priority = 0;
        lines = null;
    }

    public Order(String orderId, LocalDateTime dueDate, int priority) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public Order(String orderId, LocalDateTime dueDate, int priority, List<OrderLine> lines) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.lines = lines;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setLines(List<OrderLine> newLines) {
        this.lines = newLines;
    }

    /**
     * Two orders are equal if their id, due date, priority and line list references are equal.
     *
     * Note: line list comparison is by reference (not deep equality).
     *
     * @param o object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order order)) return false;
        return Objects.equals(orderId, order.orderId) && priority == order.priority && Objects.equals(dueDate, order.dueDate) && lines == order.lines;
    }

    /**
     * Hash code consistent with equals.
     *
     * @return hash of order fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId, dueDate, priority, lines);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", lines=" + lines +
                '}';
    }

    /**
     * Natural ordering by priority (ASC), then due date (ASC), then order id (ASC).
     *
     * @param o other order
     * @return comparison result following the ordering rules
     */
    @Override
    public int compareTo(Order o) {
        int cmp = Integer.compare(this.priority, o.priority);
        if (cmp != 0) return cmp;

        cmp = this.dueDate.compareTo(o.dueDate);
        if (cmp != 0) return cmp;

        return this.orderId.compareTo(o.orderId);
    }
}
