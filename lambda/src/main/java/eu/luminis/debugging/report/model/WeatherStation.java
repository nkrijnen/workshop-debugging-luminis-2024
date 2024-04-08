package eu.luminis.debugging.report.model;

import java.util.List;

public record WeatherStation(String name, double longitude, double latitude) {

    public static final List<WeatherStation> stations = List.of(
            new WeatherStation("New York", 40.7128, -74.0060),
            new WeatherStation("Los Angeles", 34.0522, -118.2437),
            new WeatherStation("London", 51.5074, -0.1278),
            new WeatherStation("Paris", 48.8566, 2.3522),
            new WeatherStation("Tokyo", 35.6895, 139.6917)
    );

}
