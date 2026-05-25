package Model;

/**
 * Domain object representing a catalog item identified by a SKU, with metadata
 * such as name, category, unit of measure, unit volume and weight.
 */
public class Item {
    private SKU sku;
    private String name;
    private String category;
    private String unit;
    private float volume;
    private float unitWeight;

    public Item(SKU sku, String name,String category, String unit, float volume, float unitWeight) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    public Item() {
        this.sku = null;
        this.name = null;
        this.category = null;
        this.unit = null;
        this.volume = 0;
        this.unitWeight = 0;
    }

    public SKU getSku() {
        return sku;
    }

    public void setSku(SKU sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(float unitWeight) {
        this.unitWeight = unitWeight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Item{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unit='" + unit + '\'' +
                ", volume=" + volume +
                ", unitWeight=" + unitWeight +
                '}';
    }
}
