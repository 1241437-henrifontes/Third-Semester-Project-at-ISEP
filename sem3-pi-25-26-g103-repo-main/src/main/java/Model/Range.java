package Model;

import java.util.Objects;

public class Range {
    private double maxLat;
    private double minLat;
    private double maxLon;
    private double minLon;

    public Range(double maxLat, double minLat, double maxLon, double minLon) {
        this.maxLat = maxLat;
        this.minLat = minLat;
        this.maxLon = maxLon;
        this.minLon = minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public double getMinLon() {
        return minLon;
    }

    public boolean contains(double lat, double lon) {
        return lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon;
    }

    public boolean valid() {
        return maxLat > minLat && maxLon > minLon;
    }

    @Override
    public String toString() {
        return "[" + "maxLat: " + maxLat + ", minLat: " + minLat + ", maxLon: " + maxLon + ", minLon: " + minLon + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return Double.compare(range.maxLat, maxLat) == 0 && Double.compare(range.minLat, minLat) == 0 && Double.compare(range.maxLon, maxLon) == 0 && Double.compare(range.minLon, minLon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxLat, minLat, maxLon, minLon);
    }
}
