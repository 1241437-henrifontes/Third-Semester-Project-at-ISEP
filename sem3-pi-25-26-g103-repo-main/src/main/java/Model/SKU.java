package Model;

import java.util.Objects;

/**
 * Value object representing a Stock Keeping Unit (SKU) identifier.
 */
public class SKU {
    private String sku;

    public SKU(String sku) {
        this.sku = sku;
    }

    public SKU() {
        this.sku = "SKU***";
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "SKU{" +
                "sku='" + sku + '\'' +
                '}';
    }

    /**
     * Two SKU value objects are equal if their underlying code strings are equal.
     *
     * @param o object to compare
     * @return true if both represent the same SKU code
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SKU sku1 = (SKU) o;
        return Objects.equals(sku, sku1.sku);
    }

    /**
     * Hash code derived from the SKU code string.
     *
     * @return hash value consistent with equals
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(sku);
    }
}
