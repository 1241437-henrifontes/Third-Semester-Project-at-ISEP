package Services.DTO;

import Model.Graph.Node;

public class StationMeasuresResultDTO {
    private Node station;
    private double betweenness;
    private double harmonicCloseness;
    private double degree;
    private double strength;
    private double hubScore;

    public StationMeasuresResultDTO(Node station, double betweenness, double harmonicCloseness, 
                                     double degree, double strength, double hubScore) {
        this.station = station;
        this.betweenness = betweenness;
        this.harmonicCloseness = harmonicCloseness;
        this.degree = degree;
        this.strength = strength;
        this.hubScore = hubScore;
    }

    public Node getStation() {
        return station;
    }

    public void setStation(Node station) {
        this.station = station;
    }

    public double getBetweenness() {
        return betweenness;
    }

    public void setBetweenness(double betweenness) {
        this.betweenness = betweenness;
    }

    public double getHarmonicCloseness() {
        return harmonicCloseness;
    }

    public void setHarmonicCloseness(double harmonicCloseness) {
        this.harmonicCloseness = harmonicCloseness;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getHubScore() {
        return hubScore;
    }

    public void setHubScore(double hubScore) {
        this.hubScore = hubScore;
    }

    @Override
    public String toString() {
        return "StationMeasuresResultDTO{" +
                "station=" + station.getName() +
                ", betweenness=" + betweenness +
                ", harmonicCloseness=" + harmonicCloseness +
                ", degree=" + degree +
                ", strength=" + strength +
                ", hubScore=" + hubScore +
                '}';
    }
}
