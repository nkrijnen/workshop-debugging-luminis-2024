package eu.luminis.observability.report.model;

public enum WeatherCondition {
    SUNNY("☀️"),
    CLOUDY("☁️"),
    POURING("🌧️"),
    SNOW("❄️");

    private final String imageUri;

    WeatherCondition(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public String getImageEmoji() {
        return imageUri;
    }
}
