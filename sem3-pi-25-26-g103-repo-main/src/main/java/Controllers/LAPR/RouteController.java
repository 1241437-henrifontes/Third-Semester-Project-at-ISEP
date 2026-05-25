package Controllers.LAPR;

import Model.LAPR.*;
import Repositories.LAPR.FreightRepository;
import Repositories.LAPR.FreightRepositoryOracle;
import Repositories.LAPR.LineRepository;
import Repositories.LAPR.RouteRepository;

import java.util.ArrayList;
import java.util.List;

public class RouteController {

    private final FreightRepository freightRepository;
    private final LineRepository lineRepository;
    private final RouteRepository routeRepository;

    private final List<Freight> selectedFreights;
    private final List<Facility> facilitySequence;

    private final java.util.Map<String, List<Facility>> connectionCache = new java.util.HashMap<>();

    public RouteController() {
        this.freightRepository = new FreightRepositoryOracle();
        this.lineRepository = LineRepository.getInstance();
        this.routeRepository = RouteRepository.getInstance();
        this.selectedFreights = new ArrayList<>();
        this.facilitySequence = new ArrayList<>();
    }

    private List<Facility> getCachedConnections(Facility f) {
        return connectionCache.computeIfAbsent(f.getStationId(), id -> getFacilitiesConnectedTo(f));
    }

    public List<Freight> getPendingFreights() {
        List<Freight> pending = freightRepository.getPendingFreights();
        List<Route> createdRoutes = routeRepository.getRoutes();
        List<Freight> filtered = new ArrayList<>();

        for (Freight f : pending) {
            boolean alreadyInRoute = false;
            for (Route r : createdRoutes) {
                for (Freight rf : r.getFreights()) {
                    if (rf.getId() == f.getId()) {
                        alreadyInRoute = true;
                        break;
                    }
                }
                if (alreadyInRoute) break;
            }
            if (!alreadyInRoute) {
                filtered.add(f);
            }
        }
        return filtered;
    }

    public List<Facility> getAvailableFacilities() {
        if (facilitySequence.isEmpty()) {
            return freightRepository.getAllFacilities();
        }

        List<Facility> all = freightRepository.getAllFacilities();
        List<Line> allLines = lineRepository.getLines();
        List<Facility> connected = new ArrayList<>();

        for (Facility f : all) {
            int id = Integer.parseInt(f.getStationId());
            boolean hasConnection = false;
            for (Line l : allLines) {
                if (l.getStartId() == id || l.getEndId() == id) {
                    hasConnection = true;
                    break;
                }
            }
            if (hasConnection) {
                connected.add(f);
            }
        }
        return connected;
    }

    public List<Facility> getFacilitiesConnectedTo(Facility current) {
        if (current == null) return getAvailableFacilities();
        
        int currentId = Integer.parseInt(current.getStationId());
        List<Line> allLines = lineRepository.getLines();
        List<Facility> allFacilities = freightRepository.getAllFacilities();
        List<Facility> neighbors = new ArrayList<>();

        for (Line l : allLines) {
            int neighborId = -1;
            if (l.getStartId() == currentId) {
                neighborId = l.getEndId();
            } else if (l.getEndId() == currentId) {
                neighborId = l.getStartId();
            }

            if (neighborId != -1) {
                for (Facility f : allFacilities) {
                    if (Integer.parseInt(f.getStationId()) == neighborId) {
                        if (!neighbors.contains(f)) {
                            neighbors.add(f);
                        }
                        break;
                    }
                }
            }
        }
        return neighbors;
    }

    public Facility getLastSelectedFacility() {
        if (facilitySequence.isEmpty()) return null;
        return facilitySequence.get(facilitySequence.size() - 1);
    }

    public boolean validateAllFreightsInSequence() {
        int currentIndex = 0;
        for (Freight f : selectedFreights) {
            int satisfiedAt = findFreightSatisfactionIndex(f, currentIndex);
            if (satisfiedAt == -1) {
                return false;
            }
            // O próximo frete tem de começar a ser validado a partir de onde o anterior terminou (ou depois)
            currentIndex = satisfiedAt;
        }
        return true;
    }

    private int findFreightSatisfactionIndex(Freight f, int startIndex) {
        String originId = f.getOrigin().getStationId();
        String destId = f.getDestination().getStationId();
        int originIdx = -1;

        for (int i = startIndex; i < facilitySequence.size(); i++) {
            String currentId = facilitySequence.get(i).getStationId();
            if (currentId.equals(originId)) {
                originIdx = i;
            }
            if (currentId.equals(destId) && originIdx != -1) {
                return i; // Retorna o índice onde o frete foi concluído
            }
        }
        return -1;
    }

    public List<Freight> getSelectedFreights() {
        return new ArrayList<>(selectedFreights);
    }

    public String getFacilityNameById(String id) {
        List<Facility> all = freightRepository.getAllFacilities();
        for (Facility f : all) {
            if (f.getStationId().equals(id)) {
                return f.getName();
            }
        }
        return id; // Fallback para o ID se não encontrar
    }

    public void addFreightToCurrentPlan(Freight f) { if (!selectedFreights.contains(f)) selectedFreights.add(f); }
    public void addFacilityToSequence(Facility f) { if (f != null) facilitySequence.add(f); }
    public void removeLastFacilityFromSequence() {
        if (!facilitySequence.isEmpty()) {
            facilitySequence.remove(facilitySequence.size() - 1);
        }
    }

    public boolean createAndSaveRoute(String routeName) {
        if (facilitySequence.size() < 2 || selectedFreights.isEmpty()) {
            System.out.println("Erro: Seleção inválida.");
            return false;
        }

        List<Segment> routeLogicalSegments = new ArrayList<>();

        // 1. Validar Conexões e Calcular Atributos Reais
        for (int i = 0; i < facilitySequence.size() - 1; i++) {
            Facility startNode = facilitySequence.get(i);
            Facility endNode = facilitySequence.get(i + 1);

            int startId = Integer.parseInt(startNode.getStationId());
            int endId = Integer.parseInt(endNode.getStationId());

            // A. Encontra a Linha Física
            Line connection = lineRepository.findLineConnecting(startId, endId);

            if (connection == null) {
                System.out.println("ERRO: Não existe linha física entre " + startNode.getName() + " e " + endNode.getName());
                return false;
            }

            // B. Busca os Segmentos Físicos (da tua classe Segment)
            List<Segment> physicalSegments = lineRepository.getSegmentsByLineId(connection.getLineId());

            // C. Calcular totais reais baseados na BD
            double totalLength = 0;
            double limitWeight = Double.MAX_VALUE;
            boolean isAllElectrified = true;
            int minTracks = 99;
            boolean hasSiding = false;

            for (Segment ps : physicalSegments) {
                totalLength += ps.getLength();
                if (ps.getMaxWeight() < limitWeight) limitWeight = ps.getMaxWeight();
                if (!ps.getElectrified()) isAllElectrified = false;
                if (ps.getNumberOfTracks() < minTracks) minTracks = ps.getNumberOfTracks();
                if (ps.hasSiding()) hasSiding = true;
            }

            // D. Criar o Segmento Lógico da Rota (Usando a tua classe com o novo construtor)
            Segment logicalSeg = new Segment(
                    connection.getLineId(),
                    i + 1, // Ordem na Rota
                    isAllElectrified,
                    limitWeight, // Peso limitado pelo pior segmento
                    totalLength, // Comprimento total somado
                    minTracks == 99 ? 1 : minTracks,
                    hasSiding,
                    startNode.getStationId(), // Origem
                    endNode.getStationId(),    // Destino
                    startNode.getName(),      // Nome Origem
                    endNode.getName()         // Nome Destino
            );
            routeLogicalSegments.add(logicalSeg);
        }

        // 2. Validar Freights (Origem e Destino presentes na rota)
        for (Freight f : selectedFreights) {
            if (!validateFreightPath(f)) return false;
        }

        // 3. Guardar em Memória
        Route newRoute = new Route((int)(System.currentTimeMillis() & 0xffff), routeName);
        for (Segment s : routeLogicalSegments) {
            newRoute.addSegment(s);
        }
        for (Freight f : selectedFreights) {
            newRoute.addFreight(f);
        }

        routeRepository.addRoute(newRoute);
        clearSelection();
        return true;
    }

    private boolean validateFreightPath(Freight f) {
        String originId = f.getOrigin().getStationId();
        String destId = f.getDestination().getStationId();
        int originIndex = -1, destIndex = -1;

        for (int i = 0; i < facilitySequence.size(); i++) {
            String currentId = facilitySequence.get(i).getStationId();
            if (currentId.equals(originId)) originIndex = i;
            // Só aceitamos o destino se aparecer DEPOIS da origem
            if (currentId.equals(destId) && originIndex != -1) {
                destIndex = i;
                break;
            }
        }

        if (originIndex == -1 || destIndex == -1) {
            System.out.println("ERRO: A rota não cumpre a viagem da carga: " + f.getId());
            return false;
        }
        return true;
    }
    public List<Line> getAllLines() {
        return lineRepository.getLines();
    }

    public boolean createAutomaticRoute(String routeName, Facility startStation, Facility endStation) {
        if (selectedFreights.isEmpty()) return false;

        List<Facility> fullPath = new ArrayList<>();
        Facility currentPos = startStation;

        if (currentPos != null) {
            fullPath.add(currentPos);
        }

        for (Freight f : selectedFreights) {
            if (currentPos == null) {
                // Se não foi definida estação inicial, começa na origem do primeiro frete
                fullPath.add(f.getOrigin());
                currentPos = f.getOrigin();
            } else {
                // Do ponto atual até a origem do frete
                if (!currentPos.getStationId().equals(f.getOrigin().getStationId())) {
                    List<Facility> toOrigin = findShortestPath(currentPos, f.getOrigin());
                    if (toOrigin == null) return false;
                    for (int i = 1; i < toOrigin.size(); i++) {
                        fullPath.add(toOrigin.get(i));
                    }
                    currentPos = f.getOrigin();
                }
            }

            // Da origem do frete até o destino do frete
            List<Facility> toDest = findShortestPath(currentPos, f.getDestination());
            if (toDest == null) return false;
            for (int i = 1; i < toDest.size(); i++) {
                fullPath.add(toDest.get(i));
            }
            currentPos = f.getDestination();
        }

        // Se houver estação final definida, vai do destino do último frete até ela
        if (endStation != null && !currentPos.getStationId().equals(endStation.getStationId())) {
            List<Facility> toFinal = findShortestPath(currentPos, endStation);
            if (toFinal == null) return false;
            for (int i = 1; i < toFinal.size(); i++) {
                fullPath.add(toFinal.get(i));
            }
        }

        // Definir a sequência e salvar
        this.facilitySequence.clear();
        this.facilitySequence.addAll(fullPath);

        return createAndSaveRoute(routeName);
    }

    private List<Facility> findShortestPath(Facility start, Facility end) {
        if (start.getStationId().equals(end.getStationId())) {
            List<Facility> path = new ArrayList<>();
            path.add(start);
            return path;
        }

        // BFS com cache de conexões para performance
        java.util.Queue<Facility> queue = new java.util.LinkedList<>();
        java.util.Map<String, Facility> parentMap = new java.util.HashMap<>();
        java.util.Set<String> visited = new java.util.HashSet<>();

        queue.add(start);
        visited.add(start.getStationId());

        while (!queue.isEmpty()) {
            Facility current = queue.poll();
            if (current.getStationId().equals(end.getStationId())) {
                // Reconstruir caminho
                List<Facility> path = new ArrayList<>();
                Facility step = current;
                while (step != null) {
                    path.add(0, step);
                    step = parentMap.get(step.getStationId());
                }
                return path;
            }

            for (Facility neighbor : getCachedConnections(current)) {
                if (!visited.contains(neighbor.getStationId())) {
                    visited.add(neighbor.getStationId());
                    parentMap.put(neighbor.getStationId(), current);
                    queue.add(neighbor);
                }
            }
        }

        return null;
    }

    public List<Route> getAllCreatedRoutes() {
        return routeRepository.getRoutes();
    }
    private void clearSelection() {
        this.selectedFreights.clear();
        this.facilitySequence.clear();
    }
}