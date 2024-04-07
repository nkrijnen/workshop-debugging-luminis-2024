package eu.luminis.debugging.report.model;

import java.time.LocalDateTime;
import java.util.List;

public final class ObservationEvent {
    private final List<Observation> observations;
    private final LocalDateTime date;

    public ObservationEvent(List<Observation> observations, LocalDateTime date) {
        this.observations = observations;
        this.date = date;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public LocalDateTime getDate() {
        return date;
    }
}