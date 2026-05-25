package UI;

import Controllers.AllocationController;

import java.util.Scanner;

/**
 * UI to check order eligibility and perform allocation.
 */
public class AllocationUI implements Runnable {
    private final AllocationController controller;

    /**
     * Constructs the AllocationUI and initializes its controller.
     */
    public AllocationUI() {
        controller = new AllocationController();
    }

    /**
     * Gets the underlying AllocationController.
     * @return controller instance
     */
    private AllocationController getController() {
        return controller;
    }

    /**
     * Reads the strict/partial allocation preference from the user.
     * @param sc scanner for input
     * @return true if strict, false if partial
     */
    private boolean getFlag(Scanner sc) {
        System.out.print("Flag Preference (1 for Strict and 0 for Partial): ");
        int flag = sc.nextInt();
        while (flag != 1 && flag != 0) {
            System.out.print("Invalid preference (1 for Strict and 0 for Partial): ");
            flag = sc.nextInt();
        }
        return flag == 1;
    }

    /**
     * Runs the allocation workflow, warning about prerequisites and delegating to the controller.
     */
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println();
        System.out.println("--- ORDERS ELIGIBILITY & ALLOCATION --------------------------");
        System.out.println("WARNING: Be aware that before checking the eligibility of the orders, you have to had " +
                "already organized the warehouses (Functionality 'Unload Wagons to Warehouses' in 'Unload and Organize Warehouse').");
        System.out.println();
        boolean isStrictFlag = getFlag(sc);
        getController().attendOrders(isStrictFlag);
    }
}
