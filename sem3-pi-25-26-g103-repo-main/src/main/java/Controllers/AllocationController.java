package Controllers;

import Repositories.AllocationRepository;
import Repositories.OrderRepository;
import Model.Allocation;
import UI.Utils.AllocationPrinter;
import Model.Order;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Coordinates the allocation of inventory to orders and triggers eligibility evaluation.
 * <p>
 * This controller delegates data access to repositories and invokes the printer
 * to display election results based on the chosen criteria.
 */
public class AllocationController {

    private OrderRepository getOrderRepository() {
        return OrderRepository.getInstance();
    }

    private AllocationRepository getAllocationRepository() {
        return AllocationRepository.getInstance();
    }

    private PriorityQueue<Order> getOrders() {
        return getOrderRepository().getOrders();
    }

    /**
     * Allocates inventory for all queued orders and evaluates order-line eligibility.
     *
     * @param isStrictFlag when true, only fully allocated lines are considered eligible; when false, partial allocations are allowed
     */
    public void attendOrders(boolean isStrictFlag) {
        getAllocationRepository().allocate(getOrders());
        electOrders(getAllocationRepository().getAllocationList(), isStrictFlag);
    }

    /**
     * Applies the eligibility rules to the produced allocations and prints the results.
     *
     * @param results      the allocations produced by the allocation process
     * @param isStrictFlag strict eligibility flag; see {@link #attendOrders(boolean)}
     */
    private void electOrders(List<Allocation> results, boolean isStrictFlag) {
        AllocationPrinter.elect(results, isStrictFlag);
    }
}
