import Model.ReadFromCSV;
import UI.Menu.MainMenuUI;

/**
 * Application entry point.
 */
public class Main {
    /**
     * Starts the application: bootstraps data and launches the main menu.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();

        while (!ReadFromCSV.isFirstErrorPrinted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            MainMenuUI menu = new MainMenuUI();
            menu.run();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
