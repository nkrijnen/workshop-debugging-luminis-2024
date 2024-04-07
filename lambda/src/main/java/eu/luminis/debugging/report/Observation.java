package eu.luminis.debugging.report;

public final class Observation {
    private final String station;
    private final double temperature;
    private final double humidity;

    public Observation(String station, double temperature, double humidity) {
        this.station = station;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public String getStation() {
        return station;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {
        return station + " (🌡️" + String.format("%5.2f", temperature) + " 💦" + String.format("%5.2f", humidity) + ")";
    }
}
