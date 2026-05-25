package Model;

import java.util.*;

/**
 * DTO holding the computed picking path and its total travel distance.
 */
public class RouteResult {
    private List<Bay> path;
    private int total;


    public RouteResult(List<Bay> path, int total) {
        this.path = path;
        this.total = total;
    }

    public List<Bay> getPath() {
        return path;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Path: " + path + "\nTotal Distance: " + total;
    }
}

