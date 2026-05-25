package UI;

import Controllers.TimeZoneIndexController;
import Model.*;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for tree-based indexes over railway stations (latitude/longitude/timezone).
 * Provides queries by ranges, time zone group + country, and windowed queries,
 * as well as a way to check async loading status.
 */
public class TimeZoneIndexUI implements Runnable {
    private final TimeZoneIndexController controller;
    private final Scanner sc = new Scanner(System.in);

    public TimeZoneIndexUI() {
        controller = new TimeZoneIndexController();
    }

    private TimeZoneIndexController getController() {
        return controller;
    }

    /**
     * Prompts the user to enter a latitude range and displays matching stations.
     */
    private void searchByLat() {
        if (!getController().getStationRepository().isLoading()) {
            System.out.print("Enter min latitude: ");
            double min = sc.nextDouble();
            System.out.print("Enter max latitude: ");
            double max = sc.nextDouble();

            getController().printStationsByLat(min, max);
        } else {
            System.out.println("Data is still being loaded. Please, try again later.");
        }
    }

    /**
     * Prompts the user to enter a longitude range and displays matching stations.
     */
    private void searchByLon() {
        if (!getController().getStationRepository().isLoading()) {
            System.out.print("Enter min longitude: ");
            double min = sc.nextDouble();
            System.out.print("Enter max longitude: ");
            double max = sc.nextDouble();

            getController().printStationsByLon(min, max);
        } else {
            System.out.println("Data is still being loaded. Please, try again later.");
        }
    }

    /**
     * Allows the user to select a Time Zone Group and Country, then displays matching stations.
     */
    private void searchByTZC() {
        if (!getController().getStationRepository().isLoading()) {
            TimeZoneGroup[] tzGroups = TimeZoneGroup.values();
            printEnum(tzGroups);
            System.out.print("Enter a timezone group: ");
            int tzg = sc.nextInt();
            TimeZoneGroup tzGroup = tzGroups[tzg - 1];

            System.out.println();

            List<Country> countries = getController().filterCountries(tzGroup);
            printList(countries);
            System.out.print("Enter country code: ");
            int country = sc.nextInt();
            Country c = countries.get(country - 1);

            getController().printStationsByTZC(tzGroup, c);
        } else {
            System.out.println("Data is still being loaded. Please, try again later.");
        }
    }

    private void printEnum(Enum[] enumList) {
        for (int i = 0; i < enumList.length; i++) {
            System.out.println("  " + (i + 1) + " - " + enumList[i].name());
        }
    }

    private void printList(List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + list.get(i));
        }
    }

    /**
     * Allows the user to select two Time Zone Groups and displays stations within that range.
     */
    private void searchByWin() {
        if (!getController().getStationRepository().isLoading()) {
            TimeZoneGroup[] tzGroups = TimeZoneGroup.values();
            printEnum(tzGroups);
            System.out.print("Enter a timezone group: ");
            int lower = sc.nextInt();
            TimeZoneGroup lowerGroup = tzGroups[lower - 1];

            System.out.println();

            printEnum(tzGroups);
            System.out.print("Enter another timezone group: ");
            int upper = sc.nextInt();
            TimeZoneGroup upperGroup = tzGroups[upper - 1];

            while (lowerGroup == upperGroup) {
                System.out.println("Repeated timezone group. Please, select another one: ");
                upper = sc.nextInt();
                upperGroup = tzGroups[upper - 1];
            }

            if (lowerGroup.compareTo(upperGroup) > 0) {
                TimeZoneGroup tmp = lowerGroup;
                lowerGroup = upperGroup;
                upperGroup = tmp;
            }

            getController().printStationsByWindowQuery(lowerGroup, upperGroup);
        } else {
            System.out.println("Data is still being loaded. Please, try again later.");
        }
    }

    /**
     * Displays the current loading status of station data.
     */
    private void checkLoading() {
        boolean loading = getController().getStationRepository().isLoading();
        System.out.print("Loading status: ");

        if (loading) {
            System.out.println("Still loading...");
        } else {
            System.out.println("Finished loading!");
        }
    }

    /**
     * Runs the Time Zone Index UI menu loop.
     */
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Search By Latitude", this::searchByLat));
        options.add(new MenuItem("Search By Longitude", this::searchByLon));
        options.add(new MenuItem("Search by Time Zone Group and Country", this::searchByTZC));
        options.add(new MenuItem("Search by Window of Time Zone Groups", this::searchByWin));
        options.add(new MenuItem("Check Loading Status", this::checkLoading));
        int option;

        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- TIME ZONE INDEX & WINDOWED QUERIES --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
