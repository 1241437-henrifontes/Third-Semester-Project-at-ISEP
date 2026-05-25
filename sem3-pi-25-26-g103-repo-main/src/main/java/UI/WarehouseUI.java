package UI;

import Repositories.ItemRepository;
import Controllers.WarehouseController;
import Model.Bay;
import Model.Box;
import Model.Item;
import Model.Warehouse;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * UI for warehouse management operations such as loading, viewing, searching,
 * and moving boxes across bays.
 */
public class WarehouseUI implements Runnable {

    private WarehouseController controller;
    private Scanner sc = new Scanner(System.in);

    /**
     * Displays the warehouse menu and executes selected actions.
     */
    public void run() {
        controller = new WarehouseController();
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Unload Wagons to Warehouses", this::loadWarehouses));
        options.add(new MenuItem("View Warehouses", this::printWarehouses));
        options.add(new MenuItem("Get Boxes by SKU", this::getBoxesBySKU));
        options.add(new MenuItem("Change Box into bays", this::requestBoxDetails));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- WAREHOUSE MANAGEMENT SYSTEM --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

    /**
     * Loads the warehouses from wagons and reports any remaining boxes.
     */
    public void loadWarehouses() {
        List<Box> remainingBoxes = controller.loadWarehouses();
        if (remainingBoxes.isEmpty()){
            System.out.println("Warehouses loaded successfully.");
        }else{
            System.out.println("Some boxes could not be loaded. Please check the following boxes:");
            printBox(remainingBoxes);
        }
    }

    /**
     * Guides the user to move a selected box from one bay to another.
     */
    public void requestBoxDetails() {
        Warehouse warehouse = selectWarehouse();

        ArrayList<ArrayList<Bay>> layout = warehouse.getLayout();

        ArrayList<Integer> availableAisles = new ArrayList<>();
        System.out.println("Select an aisle to unload boxes:");
        for (int aisleIndex = 0; aisleIndex < layout.size(); aisleIndex++) {
            ArrayList<Bay> aisle = layout.get(aisleIndex);
            boolean hasBox = aisle.stream().anyMatch(bay -> bay != null && !bay.getBoxes().isEmpty());
            if (hasBox) {
                printAisle(aisleIndex, aisle, false, true);
                availableAisles.add(aisleIndex);
            }
        }
        if (availableAisles.isEmpty()) {
            System.out.println("No aisles with boxes available.");
            return;
        }
        int aisle1Idx = availableAisles.get(getUserSelection(availableAisles.size()));
        ArrayList<Bay> aisle1 = layout.get(aisle1Idx);

        ArrayList<Integer> availableBays = new ArrayList<>();
        System.out.println("Select a bay to unload boxes:");
        for (int bayIndex = 0; bayIndex < aisle1.size(); bayIndex++) {
            Bay bay = aisle1.get(bayIndex);
            if (bay != null && !bay.getBoxes().isEmpty()) {
                printBay(bay, false);
                availableBays.add(bayIndex);
            }
        }
        if (availableBays.isEmpty()) {
            System.out.println("No bays with boxes available.");
            return;
        }
        int bay1Idx = availableBays.get(getUserSelection(availableBays.size()));
        Bay bay1 = aisle1.get(bay1Idx);

        System.out.println("Select a box to move:");
        ArrayList<Box> boxes = bay1.getBoxes();
        printBox(boxes);
        int boxIdx = getUserSelection(boxes.size());
        Box box = boxes.get(boxIdx);

        ArrayList<Integer> destAisles = new ArrayList<>();
        System.out.println("Select an aisle to load boxes:");
        for (int aisleIndex = 0; aisleIndex < layout.size(); aisleIndex++) {
            ArrayList<Bay> aisle = layout.get(aisleIndex);
            boolean hasBay = aisle.stream().anyMatch(bay -> bay != null);
            if (hasBay) {
                printAisle(aisleIndex , aisle, true, true);
                destAisles.add(aisleIndex);
            }
        }
        if (destAisles.isEmpty()) {
            System.out.println("No aisles available to load boxes.");
            return;
        }
        int aisle2Idx = destAisles.get(getUserSelection(destAisles.size()));
        ArrayList<Bay> aisle2 = layout.get(aisle2Idx);

        ArrayList<Integer> destBays = new ArrayList<>();
        System.out.println("Select a bay to load boxes:");
        for (int bayIndex = 0; bayIndex < aisle2.size(); bayIndex++) {
            Bay bay = aisle2.get(bayIndex);
            if (bay != null) {
                printBay(bay, false);
                destBays.add(bayIndex);
            }
        }
        if (destBays.isEmpty()) {
            System.out.println("No bays available in selected aisle.");
            return;
        }
        int bay2Idx = -1;
        do {
            bay2Idx = destBays.get(getUserSelection(destBays.size()));
            if (aisle1 == aisle2 && bay1 == aisle2.get(bay2Idx)) {
                System.out.println("Cannot move the box to the same bay. Please select a different bay.");
                bay2Idx = -1;
            }
        } while (bay2Idx == -1);
        Bay bay2 = aisle2.get(bay2Idx);

        controller.changeBoxIntoBays(warehouse, bay1, bay2, box);
    }

    /**
     * Reads an option number between 1 and max from the user.
     * @param max number of options
     * @return zero-based selection index
     */
    private int getUserSelection(int max) {
        int selection = -1;
        while (selection < 0 || selection >= max) {
            System.out.print("Select an option (1-" + max + "): ");
            while (!sc.hasNextInt()) {
                System.out.print("Invalid input. Enter a number (1-" + max + "): ");
                sc.next();
            }
            selection = sc.nextInt() - 1;
            if (selection < 0 || selection >= max) {
                System.out.println("Invalid selection. Try again.");
            }
        }
        return selection;
    }

    /**
     * Prints information for an aisle and optionally its bays/boxes.
     * @param aisleIndex index of the aisle
     * @param aisle list of bays
     * @param printEmpty whether to print empty bays
     * @param printAllClean auxiliary flag to control printing format
     */
    public void printAisle(int aisleIndex, ArrayList<Bay> aisle, Boolean printEmpty, Boolean printAllClean) {
        boolean hasBox = false;
        for (Bay bay : aisle) {
            if (bay != null && !bay.getBoxes().isEmpty()) {
                hasBox = true;
                break;
            }
        }
        if (!printEmpty && !hasBox) {
            return;
        }
        System.out.print("🛒 Aisle " + aisleIndex );
        if(printEmpty && !printAllClean) System.out.println(":\n");
        for (int bayIndex = 0; bayIndex < aisle.size(); bayIndex++) {
            Bay bay = aisle.get(bayIndex);
            if (printEmpty&&!printAllClean){
                printBay(bay, true);
            }
        }
        System.out.println();
    }

    /**
     * Prints a bay summary and optionally its boxes.
     * @param bay the bay to print
     * @param flag if true, prints contained boxes as well
     */
    public void printBay(Bay bay, Boolean flag){
        if (bay != null) {
            System.out.printf("  ▪ Bay %02d → Capacity: %d boxes | Loaded: %d boxes%n",
                    bay.getBay(), bay.getCapacityBoxes(), bay.getBoxes().size());
                    if (flag) {
                        printBox(bay.getBoxes());
                    }
        }
    }

    /**
     * Prints a list of boxes and their items.
     * @param boxes list of boxes
     */
    public void printBox(List<Box> boxes){
        for (Box box : boxes) {
            System.out.println("     📦 Box " + box.getBoxId() +
                    " (Received: " + box.getReceivedAt() +
                    (box.getExpiryDate() == null ? "" : ", Expiry: " + box.getExpiryDate()) + ")");

            for (Map.Entry<Item, Integer> entry : box.getItems().entrySet()) {
                Item item = entry.getKey();
                int qty = entry.getValue();
                System.out.printf("       - %s (%s): %d units%n", item.getName(), item.getSku(), qty);
            }
        }
    }

    /**
     * Prompts the user to select a warehouse.
     * @return selected warehouse
     */
    public Warehouse selectWarehouse(){
        System.out.println("Select a warehouse:");
        ArrayList<String> keys = new ArrayList<>(controller.getWarehouses().keySet());
        for (int i = 0; i < keys.size(); i++) {
            System.out.println((i + 1) + ". " + keys.get(i));
        }
        int warehouseIdx = getUserSelection(keys.size());
        Warehouse warehouse = controller.getWarehouses().get(keys.get(warehouseIdx));
        return warehouse;
    }

    /**
     * Prints the structure of a selected warehouse.
     */
    public void printWarehouses() {
        Warehouse warehouse = selectWarehouse();

        System.out.println("📦 Warehouse: " + warehouse.getWarehouseID());
        System.out.println("────────────────────────────────────────────");

        ArrayList<ArrayList<Bay>> layout = warehouse.getLayout();
        for (int aisleIndex = 0; aisleIndex < layout.size(); aisleIndex++) {
            ArrayList<Bay> aisle = layout.get(aisleIndex);
            boolean hasBay = false;

            for (Bay bay : aisle) {
                if (bay != null) {
                    hasBay = true;
                    break;
                }
            }

            if (hasBay) {
                printAisle(aisleIndex, aisle, true,false);
            }
        }
        System.out.println("════════════════════════════════════════════\n");
    }

    /**
     * Lists boxes containing at least a given quantity for a selected SKU.
     */
    public void getBoxesBySKU(){
        Warehouse warehouse = selectWarehouse();
        List<Item> items = ItemRepository.getInstance().getItems();
        System.out.println("Select an item by SKU:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getSku() + " - " + items.get(i).getName());
        }
        int itemIdx = getUserSelection(items.size());
        Item item = items.get(itemIdx);
        System.out.println("Enter quantity to search for:");
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            sc.next();
        }
        int quantity = sc.nextInt();
        List<Box> boxes =warehouse.getBoxesBySKU(item.getSku(), quantity);
        if (boxes.isEmpty()) {
            System.out.println("No boxes found containing at least " + quantity + " units of SKU " + item.getSku());
        } else {
            for (Box box : boxes) {
                System.out.println("     📦 Box " + box.getBoxId() +
                        " ( Aisle: " + box.getAssignedBay().getAisle() +
                        " Bay: "+ box.getAssignedBay().getBay()+
                        " Quantity: "+ box.getItems().get(item) + " )");
            }
            int allQuantity = boxes.stream().mapToInt(box -> box.getItems().get(item)).sum();
            if (allQuantity< quantity) {
                System.out.println("Warning: Total quantity found (" + allQuantity + ") is less than requested (" + quantity + ").");
            }
        }
    }
}
