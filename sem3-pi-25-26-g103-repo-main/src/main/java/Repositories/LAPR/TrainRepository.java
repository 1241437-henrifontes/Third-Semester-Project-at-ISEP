package Repositories.LAPR;

import Model.LAPR.Train;
import Model.LAPR.Locomotive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository that provides example train instances.
 *
 * <p>This class follows the Singleton pattern and is mainly
 * used for testing and demonstration purposes.</p>
 */
public class TrainRepository {
    private List<Train> trains;
    private static TrainRepository instance = new TrainRepository();

    // Atualizar TrainRepository para incluir trens mais realistas
    private TrainRepository() {
        this.trains = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of the repository.
     *
     * @return the TrainRepository instance
     */
    public static TrainRepository getInstance() {
        return instance;
    }

    /**
     * Returns the internal list of trains (mutable). For safety callers may copy the list.
     */
    /**
     * Retrieves all stored trains.
     *
     * @return a list of trains
     */
    public List<Train> getTrains() {
        return trains;
    }

    /**
     * Convenience alias used by other modules: returns an (unmodifiable) view of trains.
     */
    public List<Train> getAllTrains() {
        return Collections.unmodifiableList(trains);
    }

    /**
     * Adds a new train to the repository.
     *
     * @param t the train to add
     */
    public void addTrain(Train t) {
        if (t == null) return;
        trains.add(t);
    }

    public List<Train> getTrainsByRouteId(int routeId) {
        List<Train> result = new ArrayList<>();
        for (Train train : trains) {
            if (train.getAssignedRoute() != null && train.getAssignedRoute().getRouteId() == routeId) {
                result.add(train);
            }
        }
        return result;
    }

    /**
     * Removes a train from the repository.
     *
     * @param t the train to remove
     */
    public void removeTrain(Train t) {
        trains.remove(t);
    }
}
