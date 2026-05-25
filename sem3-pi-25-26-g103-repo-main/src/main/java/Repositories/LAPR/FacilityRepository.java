package Repositories.LAPR;

import Model.LAPR.Facility;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository providing access to a predefined list of facilities.
 */
public class FacilityRepository {
    private List<Facility> facilities;
    private static FacilityRepository instance = new FacilityRepository();

    private FacilityRepository() {
        facilities = new ArrayList<>();
        facilities.add(new Facility("1", "São Romão", true, 2));
        facilities.add(new Facility("2", "Tamel", false, 0));
        facilities.add(new Facility("3", "Senhora das Dores", true, 3));
        facilities.add(new Facility("4", "Lousado", false, 0));
        facilities.add(new Facility("5", "Porto Campanhã", true, 2));
        facilities.add(new Facility("6", "Leandro", true, 1));
        facilities.add(new Facility("7", "Porto São Bento", false, 0));
        facilities.add(new Facility("8", "Barcelos", false, 0));
        facilities.add(new Facility("9", "Vila Nova da Cerveira", false, 0));
        facilities.add(new Facility("10", "Midões", true, 2));
        facilities.add(new Facility("11", "Valença", false, 0));
        facilities.add(new Facility("12", "Darque", true, 1));
        facilities.add(new Facility("13", "Contumil", true, 3));
        facilities.add(new Facility("14", "Ermesinde", false, 0));
        facilities.add(new Facility("15", "São Frutuoso", false, 0));
        facilities.add(new Facility("16", "São Pedro da Torre", true, 2));
        facilities.add(new Facility("17", "Viana do Castelo", false, 0));
        facilities.add(new Facility("18", "Famalicão", true, 1));
        facilities.add(new Facility("19", "Barroselas", true, 2));
        facilities.add(new Facility("20", "Nine", false, 0));
        facilities.add(new Facility("21", "Caminha", false, 0));
        facilities.add(new Facility("22", "Carvalha", true, 1));
        facilities.add(new Facility("23", "Carreço", false, 0));
    }

    /** Returns the FacilityRepository singleton. */
    public static FacilityRepository getInstance() {
        return instance;
    }

    /** Returns all facilities. */
    public List<Facility> getFacilities() {
        return facilities;
    }
}