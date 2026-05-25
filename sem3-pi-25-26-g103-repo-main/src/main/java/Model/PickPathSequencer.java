package Model;

import java.util.*;

/**
 * Provides simple strategies to order bays for a picking route and compute travel distance.
 */
public class PickPathSequencer {

    /**
     * Removes duplicate bays preserving the first occurrence order, using (aisle,bay) as identity.
     *
     * @param bays input list possibly containing duplicates
     * @return a new list without duplicates, in original relative order
     */
    public List<Bay> removeDuplicates(List<Bay> bays) {
        List<Bay> unique = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Bay b : bays) {
            String key = b.getAisle() + ":" + b.getBay();
            if (seen.add(key)) unique.add(b);
        }
        return unique;
    }

    /**
     * Orders bays by aisle then bay (ascending) and returns a printable path and distance.
     *
     * @param bays list of bays to visit
     * @return formatted path and total distance string
     */
    public String strategyA(List<Bay> bays) {
        List<Bay> ordered = new ArrayList<>(bays);
        ordered.sort(Comparator.comparingInt(Bay::getAisle).thenComparingInt(Bay::getBay));
        return buildPathOutput(ordered);
    }

    /**
     * Greedy nearest-next heuristic starting from (0,0) to build a picking path and compute total distance.
     *
     * @param bays list of bays to visit
     * @return formatted path and total distance string
     */
    public String strategyB(List<Bay> bays) {
        List<Bay> unvisited = new ArrayList<>(bays);
        List<Bay> path = new ArrayList<>();
        Bay current = new Bay("", 0, 0, 0);
        int total = 0;

        while (!unvisited.isEmpty()) {
            Bay closest = null;
            int best = Integer.MAX_VALUE;

            for (Bay cand : unvisited) {
                int d = distance(current, cand);
                if (d < best) {
                    best = d;
                    closest = cand;
                }
            }

            path.add(closest);
            total += best;
            current = closest;
            unvisited.remove(closest);
        }

        return printPath(path) + "\nTotal Distance: " + total;
    }

    /**
     * Manhattan-like distance formula tailored to warehouse aisles and bays.
     * If in the same aisle, distance is the absolute difference of bay positions.
     * Otherwise, includes aisle traversal cost (factor 3) and entry/exit bays.
     *
     * @param b1 first bay
     * @param b2 second bay
     * @return estimated walking distance between bays
     */
    private int distance(Bay b1, Bay b2) {
        int a1 = b1.getAisle(), p1 = b1.getBay();
        int a2 = b2.getAisle(), p2 = b2.getBay();
        if (a1 == a2) return Math.abs(p1 - p2);
        return p1 + Math.abs(a1 - a2) * 3 + p2;
    }

    /**
     * Builds the formatted path string and computes total distance from origin (0,0) through the given sequence.
     *
     * @param path visiting order of bays
     * @return formatted text including path and total distance
     */
    private String buildPathOutput(List<Bay> path) {
        Bay current = new Bay("", 0, 0, 0);
        int total = 0;
        StringBuilder sb = new StringBuilder("Path: ");

        for (int i = 0; i < path.size(); i++) {
            Bay next = path.get(i);
            total += distance(current, next);
            sb.append("(").append(next.getAisle()).append(",").append(next.getBay()).append(")");
            if (i < path.size() - 1) sb.append(" -> ");
            current = next;
        }
        sb.append("\nTotal Distance: ").append(total);
        return sb.toString();
    }

    /**
     * Formats a path as a sequence of (aisle,bay) pairs separated by arrows.
     *
     * @param path ordered list of bays
     * @return human-readable representation of the path
     */
    private String printPath(List<Bay> path) {
        StringBuilder sb = new StringBuilder("Path: ");
        for (int i = 0; i < path.size(); i++) {
            Bay b = path.get(i);
            sb.append("(").append(b.getAisle()).append(",").append(b.getBay()).append(")");
            if (i < path.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }
}
