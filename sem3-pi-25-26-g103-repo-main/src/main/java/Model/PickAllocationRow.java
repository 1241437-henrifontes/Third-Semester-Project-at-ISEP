package Model;

import java.util.Objects;

/**
 * Immutable value object representing a single pick operation allocated to a box
 * for an order line. Comparable to support deterministic ordering when planning picks.
 */
public class PickAllocationRow implements Comparable<PickAllocationRow> {

    /** Unique identifier of the order this line belongs to. */
    private final String orderId;

    /** Line number within the order (positive). */
    private final int lineNo;

    /** SKU (Stock Keeping Unit) of the item to be picked. */
    private final SKU sku;

    /** Identifier of the box where the item is stored. */
    private final String boxId;

    /** Aisle number where the box is located. */
    private final int aisle;

    /** Bay number where the box is located. */
    private final int bay;

    /** Quantity of units in this allocation row. */
    private final int qty;

    /** Unit weight of ONE unit of the SKU (in kg). */
    private final double unitWeightKg;

    /**
     * ORIGINAL total weight of the allocation before any split (in kg).
     * This value is preserved even when the row is divided into multiple trolleys.
     * Used for sorting in FFD/BFD.
     */
    private final double originalTotalWeight;

    /**
     * Main constructor that creates an allocation row with automatically calculated original weight.
     *
     * @param orderId order identifier (cannot be null or blank)
     * @param lineNo line number (must be &gt; 0)
     * @param sku item SKU (cannot be null)
     * @param qty quantity of units (must be &gt; 0)
     * @param boxId box identifier (cannot be null or blank)
     * @param aisle aisle number
     * @param bay bay number
     * @param unitWeightKg unit weight in kg (must be &gt; 0)
     *
     * @throws NullPointerException if orderId, sku or boxId are null
     * @throws IllegalArgumentException if orderId or boxId are blank, or if lineNo, qty or unitWeightKg are not positive
     */
    public PickAllocationRow(String orderId, int lineNo, SKU sku,
                             int qty, String boxId, int aisle, int bay,
                             double unitWeightKg) {
        this(orderId, lineNo, sku, qty, boxId, aisle, bay, unitWeightKg, qty * unitWeightKg);
    }

    /**
     * Private constructor for internal control of creating copies with different quantities.
     * Allows preserving the original weight even when dividing the allocation.
     *
     * @param orderId order identifier
     * @param lineNo line number
     * @param sku item SKU
     * @param qty quantity of units
     * @param boxId box identifier
     * @param aisle aisle number
     * @param bay bay number
     * @param unitWeightKg unit weight in kg
     * @param originalTotalWeight original total weight (before splits)
     */
    private PickAllocationRow(String orderId, int lineNo, SKU sku,
                              int qty, String boxId, int aisle, int bay,
                              double unitWeightKg, double originalTotalWeight) {
        this.orderId = Objects.requireNonNull(orderId, "orderId");
        this.sku = Objects.requireNonNull(sku, "sku");
        this.boxId = Objects.requireNonNull(boxId, "boxId");
        if (orderId.isBlank()) throw new IllegalArgumentException("orderId is blank");
        if (boxId.isBlank()) throw new IllegalArgumentException("boxId is blank");
        if (lineNo <= 0) throw new IllegalArgumentException("lineNo must be > 0");
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
        if (unitWeightKg <= 0) throw new IllegalArgumentException("unitWeightKg must be > 0");
        this.lineNo = lineNo; this.qty = qty; this.aisle = aisle; this.bay = bay;
        this.unitWeightKg = unitWeightKg;
        this.originalTotalWeight = originalTotalWeight;
    }

    /**
     * Creates a copy of this allocation row with a different quantity.
     * <p>The ORIGINAL total weight is preserved to maintain ordering integrity in FFD/BFD.</p>
     *
     * @param newQty new quantity (must be &gt; 0)
     * @return new instance with altered quantity but preserved original weight
     * @throws IllegalArgumentException if newQty is not positive
     */
    public PickAllocationRow copyWithQty(int newQty) {
        if (newQty <= 0) throw new IllegalArgumentException("newQty must be > 0");
        return new PickAllocationRow(orderId, lineNo, sku, newQty, boxId, aisle, bay, unitWeightKg, originalTotalWeight);
    }

    /**
     * @return order identifier
     */
    public String getOrderId() { return orderId; }

    /**
     * @return line number
     */
    public int getLineNo() { return lineNo; }

    /**
     * @return item SKU
     */
    public SKU getSku() { return sku; }

    /**
     * @return box identifier
     */
    public String getBoxId() { return boxId; }

    /**
     * @return aisle number
     */
    public int getAisle() { return aisle; }

    /**
     * @return bay number
     */
    public int getBay() { return bay; }

    /**
     * @return quantity of units
     */
    public int getQty() { return qty; }

    /**
     * @return unit weight of one unit in kg
     */
    public double getUnitWeightKg(){ return unitWeightKg; }

    /**
     * Returns the CURRENT weight of this allocation row (considering current quantity).
     * <p>This is the weight that will actually be placed in a trolley.</p>
     *
     * @return current weight in kg (qty × unitWeightKg)
     */
    public double getWeightKg() { return qty * unitWeightKg; }

    /**
     * Returns the ORIGINAL TOTAL weight of the allocation row before any split.
     * <p>This value is used for sorting in FFD/BFD and remains constant
     * even when the row is divided into multiple trolleys.</p>
     *
     * @return original total weight in kg
     */
    public double getOriginalTotalWeight() { return originalTotalWeight; }

    /**
     * Compares this allocation row with another for natural ordering.
     * <p>Sorting is done by multiple criteria (in order of precedence):</p>
     * <ol>
     *   <li>Original total weight (DESCENDING) - heavier items first</li>
     *   <li>Unit weight (DESCENDING) - heavier units first</li>
     *   <li>Quantity (DESCENDING) - more units first</li>
     *   <li>Order ID (ASCENDING) - alphabetical order</li>
     *   <li>Line number (ASCENDING) - numerical order</li>
     * </ol>
     *
     * <p>This ordering guarantees determinism and is used by FFD and BFD heuristics.</p>
     *
     * @param o other allocation row to compare
     * @return negative if this row is "less", zero if equal, positive if "greater"
     */
    @Override
    public int compareTo(PickAllocationRow o) {
        // Sort by ORIGINAL allocation row weight (not current trolley weight)
        int cmp = Double.compare(o.originalTotalWeight, this.originalTotalWeight); // desc
        if (cmp != 0) return cmp;

        // Tiebreaker 1: unitWeight desc
        cmp = Double.compare(o.unitWeightKg, this.unitWeightKg);
        if (cmp != 0) return cmp;

        // Tiebreaker 2: qty desc
        cmp = Integer.compare(o.qty, this.qty);
        if (cmp != 0) return cmp;

        // Tiebreaker 3: orderId asc
        cmp = this.orderId.compareTo(o.orderId);
        if (cmp != 0) return cmp;

        // Tiebreaker 4: lineNo asc
        return Integer.compare(this.lineNo, o.lineNo);
    }

    /**
     * Checks equality between allocation rows.
     * <p>Two rows are equal if all their fields (except originalTotalWeight) are equal.</p>
     *
     * @param o object to compare
     * @return true if rows are equal, false otherwise
     */
    @Override
    public boolean equals(Object o){
        if (this==o) return true;
        if (!(o instanceof PickAllocationRow other)) return false;
        return lineNo==other.lineNo && aisle==other.aisle && bay==other.bay && qty==other.qty
                && orderId.equals(other.orderId) && sku.equals(other.sku) && boxId.equals(other.boxId)
                && Double.compare(unitWeightKg, other.unitWeightKg)==0;
    }

    /**
     * @return hash code based on immutable fields
     */
    @Override
    public int hashCode(){
        return java.util.Objects.hash(orderId, lineNo, sku, boxId, aisle, bay, qty, unitWeightKg);
    }

    /**
     * @return readable text representation of the allocation row
     */
    @Override
    public String toString(){
        return "PickRow{"+orderId+"#"+lineNo+", "+sku+", qty="+qty+", box="+boxId+
                " @["+aisle+","+bay+"], w="+getWeightKg()+"}";
    }
}
