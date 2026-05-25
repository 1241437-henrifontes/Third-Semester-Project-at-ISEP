package USEI02;

import Repositories.OrderRepository;
import Model.Order;
import Model.OrderLine;
import Model.SKU;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderRepository focusing on singleton access,
 * queue management, loading behavior, and priority sorting.
 */
class OrderRepositoryTest {
    private OrderRepository orderRepository;
    private Order testOrder;
    private OrderLine testOrderLine;

    @BeforeEach
    void setUp() {
        orderRepository = OrderRepository.getInstance();

        orderRepository.getOrders().clear();

        testOrder = new Order("TEST123", LocalDateTime.now().plusDays(1), 1);
        testOrderLine = new OrderLine("TEST123", 1, new SKU("SKU123"), 5);
        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(testOrderLine);
        testOrder.setLines(orderLines);
    }

    @AfterEach
    void tearDown() {
        orderRepository.getOrders().clear();
    }

    @Test
    void getInstance() {
        assertNotNull(OrderRepository.getInstance());

        OrderRepository instance1 = OrderRepository.getInstance();
        OrderRepository instance2 = OrderRepository.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void getOrders() {
        assertNotNull(orderRepository.getOrders());

        assertTrue(orderRepository.getOrders().isEmpty());

        orderRepository.getOrders().add(testOrder);
        assertEquals(1, orderRepository.getOrders().size());
        assertTrue(orderRepository.getOrders().contains(testOrder));

        Order lowPriorityOrder = new Order("LOW123", LocalDateTime.now().plusDays(1), 0);
        orderRepository.getOrders().add(lowPriorityOrder);
        assertEquals(lowPriorityOrder, orderRepository.getOrders().peek());
    }

    @Test
    void loadOrders() {
        try {
            orderRepository.loadOrders();

            assertNotNull(orderRepository.getOrders());
        } catch (Exception e) {
            fail("loadOrders() threw an exception: " + e.getMessage());
        }
    }

    @Test
    void loadOrdersWithEmptyQueue() {
        orderRepository.getOrders().add(testOrder);
        assertFalse(orderRepository.getOrders().isEmpty());

        try {
            orderRepository.loadOrders();
            assertFalse(orderRepository.getOrders().contains(testOrder));
        } catch (Exception e) {
            assertFalse(orderRepository.getOrders().contains(testOrder));
        }
    }

    @Test
    void orderPriorityQueueSorting() {
        Order order1 = new Order("ORD1", LocalDateTime.now().plusDays(2), 2);
        Order order2 = new Order("ORD2", LocalDateTime.now().plusDays(1), 1);
        Order order3 = new Order("ORD3", LocalDateTime.now().plusDays(3), 1);

        orderRepository.getOrders().add(order1);
        orderRepository.getOrders().add(order2);
        orderRepository.getOrders().add(order3);

        assertEquals(order2, orderRepository.getOrders().poll());

        assertEquals(order3, orderRepository.getOrders().poll());
        assertEquals(order1, orderRepository.getOrders().poll());

        assertTrue(orderRepository.getOrders().isEmpty());
    }
}
