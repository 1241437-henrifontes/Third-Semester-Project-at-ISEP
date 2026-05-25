package Model;

public enum TimeZoneGroup {
    WET_GMT ("WET/GMT", 0),
    CET ("CET", 1),
    EET ("EET", 2),
    FET ("FET", 3);

    private final String name;
    private final int order;

    TimeZoneGroup(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public boolean isBetween(TimeZoneGroup lower, TimeZoneGroup upper) {
        if (lower.order <= upper.order) {
            return order >= lower.order && order <= upper.order;
        } else {
            return order >= lower.order || order <= upper.order;
        }
    }
}
