package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wagon that can hold multiple boxes for transport or storage.
 */
public class Wagon {
    private String wagonId;
    private List<Box> boxes;

    public Wagon(String wagonId, List<Box> boxes) {
        this.wagonId = wagonId;
        this.boxes = boxes;
    }

    public Wagon() {
        this.wagonId = "";
        this.boxes = new ArrayList<>();
    }

    public String getWagonId() {
        return wagonId;
    }

    public void setWagonId(String wagonId) {
        this.wagonId = wagonId;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    /**
     * Adds a box to this wagon's load.
     *
     * @param box the box to add
     */
    public void addBox(Box box) {
        this.boxes.add(box);
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId='" + wagonId + '\'' +
                ", boxes=" + boxes +
                '}';
    }
}
