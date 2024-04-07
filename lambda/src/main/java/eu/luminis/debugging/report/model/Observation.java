package eu.luminis.debugging.report.model;

public record Observation(
        String station,
        double temperature,
        double humidity
) {
    @Override
    public String toString() {
        return station + " (🌡️" + String.format("%5.2f", temperature) + " 💦" + String.format("%5.2f", humidity) + ")";
    }
}
