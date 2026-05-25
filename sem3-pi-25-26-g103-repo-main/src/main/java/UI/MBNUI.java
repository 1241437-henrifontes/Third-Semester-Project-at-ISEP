package UI;

import Controllers.MBNController;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * User interface for Minimal Backbone Network (MBN) related actions.
 * Provides menu options to generate the MBN DOT file and guidance
 * to create an SVG from it using Graphviz.
 */
public class MBNUI implements Runnable {
    private final MBNController controller;
    private final Scanner sc = new Scanner(System.in);

    /**
     * Creates a new MBNUI with its controller.
     */
    public MBNUI() {
        controller = new MBNController();
    }

    /**
     * Returns the controller associated with this UI.
     *
     * @return the MBNController instance
     */
    private MBNController getController() {
        return controller;
    }

    /**
     * Triggers generation of the Minimal Backbone Network DOT file.
     */
    private void generateDot() {
        getController().generateDot();
    }

    /**
     * Prints instructions to generate an SVG file from the DOT file
     * using Graphviz's neato command.
     */
    private void generateSvg() {
        System.out.println("In order to generate the SVG file, you need to paste the following command in your terminal: neato -Tsvg outputFiles/MBN.dot -o outputFiles/MBN.svg");
    }

    /**
     * Displays the MBN menu and handles user selection until exit.
     */
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Generate Minimal Backbone Network DOT file", this::generateDot));
        options.add(new MenuItem("Generate Minimal Backbone Network SVG file", this::generateSvg));
        int option;

        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MINIMAL BACKBONE NETWORK --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
