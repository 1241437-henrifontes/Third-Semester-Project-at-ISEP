package Repositories;

import Model.Item;
import Model.ReadFromCSV;
import Model.SKU;

import java.util.List;

/**
 * Repository for loading and accessing items by SKU.
 */
public class ItemRepository {
    private List<Item> items;
    private static ItemRepository instance = new ItemRepository();

    private ItemRepository() {
    }

    /** Returns the ItemRepository singleton. */
    public static ItemRepository getInstance() {
        return instance;
    }

    /** Returns the currently loaded items. */
    public List<Item> getItems() {
        return items;
    }

    /** Loads items from the CSV source named "items". */
    public void loadItems() {
        items = ReadFromCSV.readFile("items", campos -> {
            if (campos.length < 6) {
                throw new IllegalArgumentException("Invalid item record: expected 6 columns but got " + campos.length);
            }

            String skuStr = campos[0];
            String name = campos[1];
            String category = campos[2];
            String unit = campos[3];
            String volStr = campos[4];
            String weightStr = campos[5];

            if (skuStr == null || skuStr.isEmpty()) {
                throw new IllegalArgumentException("Missing SKU in items.csv record.");
            }
            if (!skuStr.matches("SKU\\d{4}")) {
                throw new IllegalArgumentException("Invalid SKU '" + skuStr + "' in items.csv: expected 'SKU' followed by 4 digits.");
            }
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Missing name for SKU " + skuStr + ".");
            }
            if (category == null || category.isEmpty()) {
                throw new IllegalArgumentException("Missing category for SKU " + skuStr + ".");
            }
            if (unit == null || unit.isEmpty()) {
                throw new IllegalArgumentException("Missing unit for SKU " + skuStr + ".");
            }

            float volume;
            try {
                volume = Float.parseFloat(volStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid volume '" + volStr + "' for SKU " + skuStr + ": must be a number.");
            }
            if (volume < 0) {
                throw new IllegalArgumentException("Invalid volume '" + volStr + "' for SKU " + skuStr + ": must be >= 0.");
            }

            float weight;
            try {
                weight = Float.parseFloat(weightStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid unitWeight '" + weightStr + "' for SKU " + skuStr + ": must be a number.");
            }
            if (weight < 0) {
                throw new IllegalArgumentException("Invalid unitWeight '" + weightStr + "' for SKU " + skuStr + ": must be >= 0.");
            }

            return new Item(new SKU(skuStr), name, category, unit, volume, weight);
        });
    }

    /**
     * Retrieves an item by SKU.
     *
     * @param sku the item SKU
     * @return the matching item or null if not found
     */
    public Item getItemBySKU(SKU sku) {
        for (Item item : items) {
            if (item.getSku().equals(sku)) {
                return item;
            }
        }
        return null;
    }

    /** Replaces the internal items list. */
    public void setItems(List<Item> items) {
        this.items = items;
    }

}
