package Repositories;

import Model.Order;
import Model.OrderLine;
import Model.ReadFromCSV;
import Model.SKU;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Repository for loading orders and their lines, providing a priority queue for processing.
 */
public class OrderRepository {
    private PriorityQueue<Order> orders = new PriorityQueue<>();
    private static OrderRepository instance = new OrderRepository();

    private OrderRepository() {}

    /** Returns the OrderRepository singleton. */
    public static OrderRepository getInstance() {
        return instance;
    }

    /** Returns the current priority queue of orders. */
    public PriorityQueue<Order> getOrders() {
        return orders;
    }

    /**
     * Loads orders and their lines from CSV sources and rebuilds the priority queue.
     */
    public void loadOrders() {
        List<Order> ordersList = ReadFromCSV.readFile("orders", fields -> {
            if (fields.length < 3) {
                throw new IllegalArgumentException("Invalid order record: expected 3 columns but got " + fields.length);
            }
            String orderId = fields[0];
            String tsStr = fields[1];
            String priorityStr = fields[2];

            if (orderId == null || orderId.isEmpty()) {
                throw new IllegalArgumentException("Missing orderId in orders.csv record.");
            }
            LocalDateTime ts;
            try {
                ts = LocalDateTime.parse(tsStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid timestamp '" + tsStr + "' for order '" + orderId + "': expected ISO-8601 format yyyy-MM-ddTHH:mm:ss.");
            }
            int priority;
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid priority '" + priorityStr + "' for order '" + orderId + "': must be an integer.");
            }
            if (priority < 0) {
                throw new IllegalArgumentException("Invalid priority '" + priorityStr + "' for order '" + orderId + "': must be >= 0.");
            }
            return new Order(orderId, ts, priority);
        });
        loadLines(ordersList);
        orders.clear();
        orders = new PriorityQueue<>(ordersList);
    }

    /** Associates loaded order lines with their respective orders. */
    private void loadLines(List<Order> ordersList) {
        List<OrderLine> lines = ReadFromCSV.readFile("order_lines", fields -> {
            if (fields.length < 4) {
                throw new IllegalArgumentException("Invalid order line record: expected 4 columns but got " + fields.length);
            }
            String orderId = fields[0];
            String lineNumStr = fields[1];
            String skuStr = fields[2];
            String qtyStr = fields[3];

            if (orderId == null || orderId.isEmpty()) {
                throw new IllegalArgumentException("Missing orderId in order_lines.csv record.");
            }
            int lineNum;
            try {
                lineNum = Integer.parseInt(lineNumStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid line number '" + lineNumStr + "' for order '" + orderId + "': must be an integer.");
            }
            if (lineNum < 1) {
                throw new IllegalArgumentException("Invalid line number '" + lineNumStr + "' for order '" + orderId + "': must be >= 1.");
            }
            if (skuStr == null || skuStr.isEmpty()) {
                throw new IllegalArgumentException("Missing SKU in order_lines.csv for order '" + orderId + "', line " + lineNum + ".");
            }
            if (!skuStr.matches("SKU\\d{4}")) {
                throw new IllegalArgumentException("Invalid SKU '" + skuStr + "' in order_lines.csv for order '" + orderId + "', line " + lineNum + ": expected 'SKU' followed by 4 digits.");
            }
            int quantity;
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid quantity '" + qtyStr + "' in order_lines.csv for order '" + orderId + "', line " + lineNum + ": must be an integer.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity '" + qtyStr + "' in order_lines.csv for order '" + orderId + "', line " + lineNum + ": must be > 0.");
            }

            return new OrderLine(orderId, lineNum, new SKU(skuStr), quantity);
        });

        Map<String, List<OrderLine>> linesMap = new HashMap<>();

        for (OrderLine line : lines) {
            linesMap.computeIfAbsent(line.getOrderId(), k -> new ArrayList<>()).add(line);
        }

        for (Order order : ordersList) {
            order.setLines(linesMap.getOrDefault(order.getOrderId(), new ArrayList<>()));
        }
    }
}
