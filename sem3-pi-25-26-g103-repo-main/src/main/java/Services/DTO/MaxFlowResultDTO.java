package Services.DTO;

import Model.Graph.Node;

public class MaxFlowResultDTO {
    private Node sourceStation;
    private Node sinkStation;
    private double maxFlowValue;
    private String temporalComplexity;

    public MaxFlowResultDTO(Node sourceStation, Node sinkStation, double maxFlowValue, String temporalComplexity) {
        this.sourceStation = sourceStation;
        this.sinkStation = sinkStation;
        this.maxFlowValue = maxFlowValue;
        this.temporalComplexity = temporalComplexity;
    }

    public Node getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(Node sourceStation) {
        this.sourceStation = sourceStation;
    }

    public Node getSinkStation() {
        return sinkStation;
    }

    public void setSinkStation(Node sinkStation) {
        this.sinkStation = sinkStation;
    }

    public double getMaxFlowValue() {
        return maxFlowValue;
    }

    public void setMaxFlowValue(double maxFlowValue) {
        this.maxFlowValue = maxFlowValue;
    }

    public String getTemporalComplexity() {
        return temporalComplexity;
    }

    public void setTemporalComplexity(String temporalComplexity) {
        this.temporalComplexity = temporalComplexity;
    }

    @Override
    public String toString() {
        return "MaxFlowResultDTO{" +
                "sourceStation=" + sourceStation.getName() +
                ", sinkStation=" + sinkStation.getName() +
                ", maxFlowValue=" + maxFlowValue +
                ", temporalComplexity='" + temporalComplexity + '\'' +
                '}';
    }
}