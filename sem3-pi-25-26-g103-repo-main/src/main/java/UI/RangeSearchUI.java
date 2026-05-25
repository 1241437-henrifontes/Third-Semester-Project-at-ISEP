package UI;

import Controllers.RangeSearchController;
import Model.Country;
import Model.Filters.RailwayStationSearchFilters;
import Model.Range;
import UI.Utils.FilePrinterStations;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class RangeSearchUI implements Runnable{
    private RangeSearchController controller;
    private Scanner sc = new Scanner(System.in);

    public void run() {
        controller = new RangeSearchController();
        Range range = requestRange();
        RailwayStationSearchFilters filters = requestFilters();
        if(!controller.alreadyExists(filters, range)){
            controller.searchRange(filters, range);
        }else{
            System.out.println("Range search already exists. Retrieving from repository...\n");
        }
        FilePrinterStations.printAllRangeSearch(controller.getRangeSearchRepository());
    }

    public Range requestRange() {
        System.out.println("Enter min latitude: ");
        double minLat = sc.nextDouble();
        System.out.println("Enter max latitude: ");
        double maxLat = sc.nextDouble();
        System.out.println("Enter min longitude: ");
        double minLon = sc.nextDouble();
        System.out.println("Enter max longitude: ");
        double maxLon = sc.nextDouble();
        Range range = new Range(maxLat, minLat, maxLon, minLon);
        if (!range.valid()) {
            System.out.println("Invalid range. Please try again.");
            return requestRange();
        }
        return range;
    }

    public RailwayStationSearchFilters requestFilters(){
        boolean isCity = booleanRequest("City");
        boolean isMainStation = booleanRequest("Main Station");
        boolean isAirport = booleanRequest("Airport");
        List<Country> countries = requestCountries();
        return new RailwayStationSearchFilters(isCity, isMainStation, isAirport, countries);
    }

    public boolean booleanRequest(String message) {
        System.out.println("Filter by " + message + " (true = 1 or false = 0): ");
        while (true) {
            if (sc.hasNextInt()) {
                int answer = sc.nextInt();
                if (answer == 1 || answer == 0) {
                    return answer == 1;
                } else {
                    System.out.println("Invalid input. Please enter 1 or 0.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number (1 or 0).");
                sc.next();
            }
        }
    }

    public List<Country> requestCountries() {
        List<Country> countries = new ArrayList<>();
        List<Country> allCountries = Country.getAllCountries();
        int answer = -1;
        boolean flag = true;
        while (flag) {
            for (int i = 0; i < allCountries.size(); i++) {
                System.out.println((i + 1) + ". " + allCountries.get(i).getName());
            }
            System.out.println((allCountries.size() + 1) + ". All");
            System.out.println("Enter a country index or 0 to exit: ");
            if (sc.hasNextInt()) {
                answer = sc.nextInt();
                if (answer == 0) {
                    flag = false;
                } else if (answer >= 1 && answer <= allCountries.size()) {
                    if (!countries.contains(allCountries.get(answer - 1))) {
                        countries.add(allCountries.get(answer - 1));
                    }else{
                        System.out.println("Country already selected. Please choose another one.\n");
                    }
                } else if (answer == allCountries.size() + 1) {
                    countries.clear();
                    countries.addAll(allCountries);
                    flag = false;
                } else {
                    System.out.println("Invalid index. Please choose a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }

        return countries;
    }
}
