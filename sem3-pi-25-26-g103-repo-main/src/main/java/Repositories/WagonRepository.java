package Repositories;

import Model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Repository responsible for loading wagons and exposing wagon/box access operations.
 */
public class WagonRepository {
    private List<Wagon> wagons;
    private static WagonRepository instance = new WagonRepository();

    private WagonRepository() {}

    /** Returns the WagonRepository singleton. */
    public static WagonRepository getInstance() {
        return instance;
    }

    /** Returns the currently loaded wagons. */
    public List<Wagon> getWagons() {
        return wagons;
    }

    /**
     * Loads wagons and their boxes from the CSV source named "wagons".
     */
    public void loadWagons() {
        wagons = new ArrayList<>();

        ReadFromCSV.readFile("wagons", campos -> {
            if (campos.length < 6) {
                throw new IllegalArgumentException("Invalid record: expected 6 columns but got " + campos.length);
            }

            String wagonId = campos[0];
            String boxId = campos[1];
            String skuStr = campos[2];
            String qtyStr = campos[3];
            String expDateStr = campos[4];
            String arrivalTsStr = campos[5];

            if (!wagonId.matches("WGN\\d{3}")) {
                throw new IllegalArgumentException("Invalid WagonId '" + wagonId + "': expected 'WGN' followed by 3 digits.");
            }

            Wagon currentWagon;
            if (!wagons.isEmpty() && wagons.get(wagons.size() - 1).getWagonId().equals(wagonId)) {
                currentWagon = wagons.get(wagons.size() - 1);
            } else {
                currentWagon = new Wagon();
                currentWagon.setWagonId(wagonId);
                wagons.add(currentWagon);
            }

            if (boxId == null || boxId.isEmpty()) {
                throw new IllegalArgumentException("Missing BoxId for Wagon " + wagonId + ".");
            }
            if (!boxId.matches("BOX\\d{5}")) {
                throw new IllegalArgumentException("Invalid BoxId '" + boxId + "' for Wagon " + wagonId + ": expected 'BOX' followed by 5 digits.");
            }

            if (skuStr == null || skuStr.isEmpty()) {
                throw new IllegalArgumentException("Missing SKU for Box " + boxId + " (Wagon " + wagonId + ").");
            }
            if (!skuStr.matches("SKU\\d{4}")) {
                throw new IllegalArgumentException("Invalid SKU '" + skuStr + "' for Box " + boxId + " (Wagon " + wagonId + "): expected 'SKU' followed by 4 digits.");
            }

            Item item = ItemRepository.getInstance().getItemBySKU(new SKU(skuStr));
            if (item == null) {
                throw new IllegalArgumentException("SKU not found: '" + skuStr + "' for Box " + boxId + " (Wagon " + wagonId + ").");
            }

            int quantity;
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid quantity '" + qtyStr + "' for Box " + boxId + " (Wagon " + wagonId + "): must be an integer.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity '" + qtyStr + "' for Box " + boxId + " (Wagon " + wagonId + "): must be > 0.");
            }

            LocalDate expDate = null;
            if (expDateStr != null && !expDateStr.isEmpty()) {
                try {
                    expDate = LocalDate.parse(expDateStr);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid expiration date '" + expDateStr + "' for Box " + boxId + " (Wagon " + wagonId + "): expected ISO-8601 format yyyy-MM-dd.");
                }
            }

            LocalDateTime arrivalTs;
            try {
                arrivalTs = LocalDateTime.parse(arrivalTsStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid arrival timestamp '" + arrivalTsStr + "' for Box " + boxId + " (Wagon " + wagonId + "): expected ISO-8601 format yyyy-MM-ddTHH:mm:ss.");
            }

            Box box = new Box(boxId, arrivalTs, expDate);
            box.addItem(item, quantity);
            currentWagon.addBox(box);

            return currentWagon;
        }).stream().filter(Objects::nonNull).forEach(w -> {});
    }

    /**
     * Returns all boxes from all wagons without mutating the internal wagons list.
     *
     * @return list of boxes aggregated from all wagons
     */
    public List<Box> getAllBoxesAndRemove() {
        List<Box> allBoxes = new ArrayList<>();
        for (Wagon wagon : wagons) {
            allBoxes.addAll(wagon.getBoxes());
        }
        return allBoxes;
    }

    /**
     * Adds the provided boxes back to wagons in sequence.
     *
     * @param boxes boxes to distribute across wagons
     */
    public void addBoxes(List<Box> boxes) {
        int index = 0;
        while(!boxes.isEmpty()) {
            wagons.get(index).addBox(boxes.remove(0));
        }
    }
}