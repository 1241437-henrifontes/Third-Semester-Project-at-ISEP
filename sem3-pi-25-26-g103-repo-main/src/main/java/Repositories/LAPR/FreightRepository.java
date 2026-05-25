package Repositories.LAPR;

import Model.LAPR.Facility;
import Model.LAPR.Freight;
import java.util.List;

/**
 * Repository interface for accessing freight-related data.
 *
 * <p>This interface defines the contract for retrieving facilities
 * and pending freight records from a data source.</p>
 */
public interface FreightRepository {

    /**
     * Retrieves all facilities available in the system.
     *
     * @return a list of all facilities
     */
    List<Facility> getAllFacilities();

    /**
     * Retrieves all pending freight operations.
     *
     * @return a list of pending freights
     */
    List<Freight> getPendingFreights();
}