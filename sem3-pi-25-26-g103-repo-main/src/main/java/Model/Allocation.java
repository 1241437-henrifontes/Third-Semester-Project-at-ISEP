package Model;

import java.util.*;

/**
 * Represents the allocation result for a single order line.
 * It records the line, the total quantity allocated, and the list of boxes used with their picked quantities.
 */
public class Allocation {
    private OrderLine line;
    private Integer allocatedQty;
    private List<Map<Box, Integer>> boxes;

    public Allocation() {
        this.line = null;
        this.allocatedQty = null;
        this.boxes = new ArrayList<>();
    }

    public Allocation(OrderLine line, Integer allocatedQty, List<Map<Box, Integer>> boxes) {
        this.line = line;
        this.allocatedQty = allocatedQty;
        this.boxes = boxes;
    }

    public OrderLine getLine() {
        return line;
    }

    public void setLine(OrderLine line) {
        this.line = line;
    }

    public Integer getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(Integer allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public List<Map<Box, Integer>> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Map<Box, Integer>> boxes) {
        this.boxes = boxes;
    }
}
