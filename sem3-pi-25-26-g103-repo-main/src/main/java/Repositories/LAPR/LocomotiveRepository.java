package Repositories.LAPR;

import Model.LAPR.Locomotive;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository exposing a predefined list of locomotives.
 */
public class LocomotiveRepository {
    private List<Locomotive> locomotives;
    private static LocomotiveRepository instance = new LocomotiveRepository();

    private LocomotiveRepository() {
        locomotives = new ArrayList<>();
        locomotives.add(new Locomotive(5621, "Inês", "Siemens", "Eurosprinter", 1995, 2, "Bo-Bo", 5600, 19.2, 3, 4.375, 87, 220, 70, 300, "Electric", 25, 50, "Medway", 1, 0));
        locomotives.add(new Locomotive(5623, "Paz", "Siemens", "Eurosprinter", 1995, 2, "Bo-Bo", 5600, 19.2, 3, 4.375, 87, 220, 70, 300, "Electric", 25, 50, "Medway", 1, 0));
        locomotives.add(new Locomotive(5630, "Helena", "Siemens", "Eurosprinter", 1996, 2, "Bo-Bo", 5600, 19.2, 3, 4.375, 87, 220, 70, 300, "Electric", 25, 50, "Medway", 1, 0));
        locomotives.add(new Locomotive(1903, "Eva", "Sorefame - Alsthom", "CP 1900", 1981, 2, "Co-Co", 1623, 19.084, 3.062, 4.31, 117, 100, 42.5, 396, "Diesel", 0, 0, "Medway", 1, 4882));
    }

    /** Returns the LocomotiveRepository singleton. */
    public static LocomotiveRepository getInstance() {
        return instance;
    }

    /** Returns all locomotives. */
    public List<Locomotive> getLocomotives() {
        return locomotives;
    }
}