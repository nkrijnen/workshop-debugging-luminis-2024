package eu.luminis.debugging.report.model;

public enum WeatherCondition {
    SUNNY("sunny.png"),
    CLOUDY("cloudy.png"),
    POURING("pouring.png"),
    SNOW("snow.png");

    private final String imageUri;

    WeatherCondition(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public String getImageUri() {
        return imageUri;
    }
}
