package Controllers.LAPR;

import Model.LAPR.*;
import Repositories.LAPR.TrainAssemblyRepository;
import Repositories.LAPR.TrainRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * USLP09 - Controller for Train Assembly operations.
 * As Traffic Manager, I want to be able to assemble and assign a train to a route.
 */
public class TrainAssemblyController {

    private final TrainAssemblyRepository repository;

    public TrainAssemblyController() {
        this.repository = TrainAssemblyRepository.getInstance();
    }

    /**
     * Gets all available routes from the database.
     *
     * @return list of available routes
     */
    public List<Route> getAllRoutes() {
        return repository.getAllRoutes();
    }

    /**
     * Gets available locomotives for a route.
     * Filters out locomotives that are already part of any train in the TrainRepository.
     */
    public List<RollingStockDTO> getAvailableLocomotivesForRoute(int routeStartStationId) {
        List<Locomotive> locomotives = repository.getAvailableLocomotives(routeStartStationId);

        List<RollingStockDTO> dtos = locomotives.stream()
            .map(RollingStockDTO::fromLocomotive)
            .collect(Collectors.toList());

        // Sort parked locomotives by distance (descending)
        dtos.sort((a, b) -> {
            if (a.getStatus() == RollingStockStatus.PARKED && b.getStatus() == RollingStockStatus.PARKED) {
                return Double.compare(
                    b.getDistanceFromStart() != null ? b.getDistanceFromStart() : 0,
                    a.getDistanceFromStart() != null ? a.getDistanceFromStart() : 0
                );
            }
            if (a.getStatus() == RollingStockStatus.IN_TRANSIT) return -1;
            if (b.getStatus() == RollingStockStatus.IN_TRANSIT) return 1;
            return 0;
        });

        // Remove locomotives already assigned to any train in TrainRepository
        List<Train> existingTrains = TrainRepository.getInstance().getAllTrains();
        Set<Integer> assignedLocoIds = new HashSet<>();
        if (existingTrains != null) {
            for (Train t : existingTrains) {
                Locomotive l = t.getLocomotive();
                if (l != null) assignedLocoIds.add(l.getNumberOfLocomotives());
            }
        }

        dtos.removeIf(dto -> assignedLocoIds.contains(dto.getId()));

        return dtos;
    }

    /**
     * Gets available wagons for a route.
     * Filters out wagons that are already part of any train in the TrainRepository.
     */
    public List<RollingStockDTO> getAvailableWagonsForRoute(int routeStartStationId) {
        List<RailwayWagon> wagons = repository.getAvailableWagons(routeStartStationId);

        List<RollingStockDTO> dtos = wagons.stream()
            .map(RollingStockDTO::fromWagon)
            .collect(Collectors.toList());

        // Sort parked wagons by distance (descending)
        dtos.sort((a, b) -> {
            if (a.getStatus() == RollingStockStatus.PARKED && b.getStatus() == RollingStockStatus.PARKED) {
                return Double.compare(
                    b.getDistanceFromStart() != null ? b.getDistanceFromStart() : 0,
                    a.getDistanceFromStart() != null ? a.getDistanceFromStart() : 0
                );
            }
            if (a.getStatus() == RollingStockStatus.IN_TRANSIT) return -1;
            if (b.getStatus() == RollingStockStatus.IN_TRANSIT) return 1;
            return 0;
        });

        // Remove wagons already assigned to any train in TrainRepository
        List<Train> existingTrains = TrainRepository.getInstance().getAllTrains();
        Set<String> assignedWagonIds = new HashSet<>();
        if (existingTrains != null) {
            for (Train t : existingTrains) {
                List<RailwayWagon> tw = t.getWagons();
                if (tw != null) {
                    for (RailwayWagon w : tw) {
                        if (w != null && w.getWagonId() != null) assignedWagonIds.add(w.getWagonId());
                    }
                }
            }
        }

        dtos.removeIf(dto -> assignedWagonIds.contains(dto.getName()));

        return dtos;
    }

    /**
     * Gets all locomotives (for status display) without removing those assigned to trains.
     * This is used by the status view which must show every rolling stock regardless of assignment.
     */
    public List<RollingStockDTO> getAllLocomotivesForStatus(int routeStartStationId) {
        List<Locomotive> locomotives = repository.getAvailableLocomotives(routeStartStationId);

        List<RollingStockDTO> dtos = locomotives.stream()
            .map(RollingStockDTO::fromLocomotive)
            .collect(Collectors.toList());

        // Sort parked locomotives by distance (descending)
        dtos.sort((a, b) -> {
            if (a.getStatus() == RollingStockStatus.PARKED && b.getStatus() == RollingStockStatus.PARKED) {
                return Double.compare(
                    b.getDistanceFromStart() != null ? b.getDistanceFromStart() : 0,
                    a.getDistanceFromStart() != null ? a.getDistanceFromStart() : 0
                );
            }
            if (a.getStatus() == RollingStockStatus.IN_TRANSIT) return -1;
            if (b.getStatus() == RollingStockStatus.IN_TRANSIT) return 1;
            return 0;
        });

        // Ensure locomotives already assigned to trains are included
        List<Train> existingTrains = TrainRepository.getInstance().getAllTrains();
        if (existingTrains != null) {
            // collect existing ids
            java.util.Set<Integer> existingIds = dtos.stream().map( RollingStockDTO::getId ).collect(Collectors.toSet());
            for (Train t : existingTrains) {
                if (t == null) continue;
                Locomotive l = t.getLocomotive();
                if (l == null) continue;
                int lid = l.getNumberOfLocomotives();
                if (!existingIds.contains(lid)) {
                    dtos.add(RollingStockDTO.fromLocomotive(l));
                    existingIds.add(lid);
                }
            }
        }

        return dtos;
    }

    /**
     * Gets all wagons (for status display) without removing those assigned to trains.
     */
    public List<RollingStockDTO> getAllWagonsForStatus(int routeStartStationId) {
        List<RailwayWagon> wagons = repository.getAvailableWagons(routeStartStationId);

        List<RollingStockDTO> dtos = wagons.stream()
            .map(RollingStockDTO::fromWagon)
            .collect(Collectors.toList());

        // Sort parked wagons by distance (descending)
        dtos.sort((a, b) -> {
            if (a.getStatus() == RollingStockStatus.PARKED && b.getStatus() == RollingStockStatus.PARKED) {
                return Double.compare(
                    b.getDistanceFromStart() != null ? b.getDistanceFromStart() : 0,
                    a.getDistanceFromStart() != null ? a.getDistanceFromStart() : 0
                );
            }
            if (a.getStatus() == RollingStockStatus.IN_TRANSIT) return -1;
            if (b.getStatus() == RollingStockStatus.IN_TRANSIT) return 1;
            return 0;
        });

        // Ensure wagons already assigned to trains are included
        List<Train> existingTrains2 = TrainRepository.getInstance().getAllTrains();
        if (existingTrains2 != null) {
            java.util.Set<String> existingNames = dtos.stream().map( RollingStockDTO::getName ).collect(Collectors.toSet());
            for (Train t : existingTrains2) {
                if (t == null) continue;
                List<RailwayWagon> tw = t.getWagons();
                if (tw == null) continue;
                for (RailwayWagon w : tw) {
                    if (w == null) continue;
                    String wid = w.getWagonId();
                    if (wid != null && !existingNames.contains(wid)) {
                        dtos.add(RollingStockDTO.fromWagon(w));
                        existingNames.add(wid);
                    }
                }
            }
        }

        return dtos;
    }

    /**
     * Assembles a train by selecting locomotives and wagons, then assigns it to a route.
     * Validates gauge compatibility and other constraints.
     *
     * @param trainId the train identifier
     * @param locomotiveIds list of locomotive IDs to include
     * @param wagonIds list of wagon IDs to include
     * @param route the route to assign
     * @return the assembled train if successful, null otherwise
     */
    public Train assembleAndAssignTrain(int trainId, List<Integer> locomotiveIds,
                                       List<String> wagonIds, Route route) {
        if (locomotiveIds == null || locomotiveIds.isEmpty()) {
            System.err.println("Error: At least one locomotive must be selected.");
            return null;
        }

        // Check for duplicate trainId
        String trainIdStr = String.valueOf(trainId);
        if (trainIdExists(trainIdStr)) {
            System.err.println("Error: trainId already exists: " + trainIdStr);
            return null;
        }

        // If user selected multiple locomotives, warn and use the first one as primary
        if (locomotiveIds.size() > 1) {
            System.out.println("Warning: Multiple locomotives selected. Using the first selected as primary locomotive.");
        }

        int selectedLocoId = locomotiveIds.get(0);

        // Get the locomotives from the database
        List<Locomotive> allLocomotives = repository.getAvailableLocomotives(0);
        Locomotive primaryLocomotive = allLocomotives.stream()
            .filter(l -> l.getNumberOfLocomotives() == selectedLocoId)
            .findFirst()
            .orElse(null);

        if (primaryLocomotive == null) {
            System.err.println("Error: Primary locomotive not found (ID: " + selectedLocoId + ").");
            return null;
        }

        // Create the train and assign the route
        Train train = new Train(String.valueOf(trainId), primaryLocomotive, route);
        // Ensure the train has the assigned route set (constructor above already does this)
        train.setAssignedRoute(route);


        // Add wagons if any
        if (wagonIds != null && !wagonIds.isEmpty()) {
            List<RailwayWagon> allWagons = repository.getAvailableWagons(0);

            for (String wagonId : wagonIds) {
                RailwayWagon wagon = allWagons.stream()
                    .filter(w -> w.getWagonId().equals(wagonId))
                    .findFirst()
                    .orElse(null);

                if (wagon != null) {
                    if (!train.addWagon(wagon)) {
                        System.err.println("Warning: Wagon " + wagonId + " could not be added (gauge incompatibility).");
                    }
                } else {
                    System.err.println("Warning: Wagon " + wagonId + " not found.");
                }
            }
        }

        // Validate train composition
        if (!train.isValid()) {
            System.err.println("Error: Train composition is invalid.");
            return null;
        }

        // Add the train to the local TrainRepository
        try {
            TrainRepository.getInstance().addTrain(train);
        } catch (Exception e) {
            System.err.println("Warning: unable to add train to TrainRepository: " + e.getMessage());
        }

        return train;
    }

    /**
     * Checks whether a train with the given trainId already exists either in the scheduler
     * repository (persisted/scheduled trains) or in the local TrainRepository (in-memory).
     *
     * @param trainIdStr train id as string
     * @return true if trainId already exists, false otherwise
     */
    public boolean trainIdExists(String trainIdStr) {
        if (trainIdStr == null || trainIdStr.isEmpty()) return false;


        // Check local repository
        try {
            TrainRepository localRepo = TrainRepository.getInstance();
            List<Train> existing = localRepo.getAllTrains();
            if (existing != null && existing.stream().anyMatch(t -> trainIdStr.equals(t.getTrainId()))) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Warning: unable to check TrainRepository for duplicates: " + e.getMessage());
        }

        return false;
    }

    /**
     * Filters rolling stock by gauge compatibility.
     *
     * @param rollingStock list of rolling stock DTOs
     * @param gaugeId the gauge ID to filter by
     * @return filtered list
     */
    public List<RollingStockDTO> filterByGauge(List<RollingStockDTO> rollingStock, int gaugeId) {
        return rollingStock.stream()
            .filter(rs -> rs.getGaugeId() == gaugeId)
            .collect(Collectors.toList());
    }

    /**
     * Filters rolling stock by operator.
     *
     * @param rollingStock list of rolling stock DTOs
     * @param operator the operator name to filter by
     * @return filtered list
     */
    public List<RollingStockDTO> filterByOperator(List<RollingStockDTO> rollingStock, String operator) {
        return rollingStock.stream()
            .filter(rs -> rs.getOperator().equals(operator))
            .collect(Collectors.toList());
    }
}
